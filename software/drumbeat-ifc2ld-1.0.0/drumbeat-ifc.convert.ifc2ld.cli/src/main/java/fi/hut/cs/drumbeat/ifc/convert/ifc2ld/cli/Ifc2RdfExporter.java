package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.cli;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.EnumSet;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import fi.hut.cs.drumbeat.ifc.processing.IfcModelAnalyser;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.common.config.*;
import fi.hut.cs.drumbeat.common.config.document.*;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcCommandLineOptions;
import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.util.Ifc2RdfExportUtil;
import fi.hut.cs.drumbeat.ifc.convert.stff2ifc.IfcParserException;
import fi.hut.cs.drumbeat.ifc.convert.stff2ifc.util.IfcParserUtil;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;
import fi.hut.cs.drumbeat.rdf.modelfactory.MemoryJenaModelFactory;
import fi.hut.cs.drumbeat.rdf.modelfactory.config.JenaModelFactoryPoolConfigurationSection;

import org.apache.jena.riot.RDFFormat;

public class Ifc2RdfExporter {
	
	private static final Logger logger = Logger.getRootLogger();
	
	private String inputSchemaFilePath;
	private String inputModelFilePath;
	private String outputLayerName;
	private String outputSchemaFilePath;
	private String outputSchemaName;
	private String outputModelFilePath;
	private String outputModelName;
	private String outputMetaModelFilePath;
	private String outputMetaModelName;
	private String outputFileFormatName;
	
	public Ifc2RdfExporter(
		 String inputSchemaFilePath,
		 String inputModelFilePath,
		 String outputLayerName,
		 String outputSchemaFilePath,
		 String outputSchemaName,
		 String outputModelFilePath,
		 String outputModelName,
		 String outputMetaModelFilePath,
		 String outputMetaModelName,
		 String outputFileFormatName)
	{
		this.inputSchemaFilePath = inputSchemaFilePath;
		this.inputModelFilePath = inputModelFilePath;
		this.outputLayerName = outputLayerName;
		this.outputSchemaFilePath = outputSchemaFilePath;
		this.outputSchemaName = outputSchemaName;
		this.outputModelFilePath = outputModelFilePath;
		this.outputModelName = outputModelName;
		this.outputMetaModelFilePath = outputMetaModelFilePath;
		this.outputMetaModelName = outputMetaModelName;
		this.outputFileFormatName = outputFileFormatName;		
	}
	
	public static void init(String loggerConfigFilePath, String configFilePath) throws ConfigurationParserException {
		//
		// config logger
		//
		loadLoggerConfigration(loggerConfigFilePath);
		
		//
		// load configuration document
		//
		loadConfiguration(configFilePath);
	}
	
	/**
	 * @param args
	 */	
	public void run() throws Exception {
		
		//
		// load jena model factory configuration pool
		//
		ConfigurationPool<ConfigurationItemEx> jenaModelFactoryConfigurationPool;
		if (!StringUtils.isEmptyOrNull(outputSchemaName) || !StringUtils.isEmptyOrNull(outputModelName)) {
			jenaModelFactoryConfigurationPool = getJenaModelFactoryConfigurationPool();
		} else {
			jenaModelFactoryConfigurationPool  = null;
		}
		
		//
		// define jena-model factory for the output IFC schema
		//
		JenaModelFactoryBase outputSchemaJenaModelFactory = null;		
		if (!StringUtils.isEmptyOrNull(outputSchemaName)) {
			outputSchemaJenaModelFactory = getJenaModelFactory(jenaModelFactoryConfigurationPool, outputSchemaName);
		} else if (!StringUtils.isEmptyOrNull(outputSchemaFilePath)) {
			outputSchemaJenaModelFactory =  new MemoryJenaModelFactory();			
		}
		
		//
		// define jena-model factory for the output IFC model 
		//
		JenaModelFactoryBase outputModelJenaModelFactory = null;		
		if (!StringUtils.isEmptyOrNull(outputModelName)) {
			outputModelJenaModelFactory = getJenaModelFactory(jenaModelFactoryConfigurationPool, outputModelName);
		} else if (!StringUtils.isEmptyOrNull(outputModelFilePath)) {
			outputModelJenaModelFactory =  new MemoryJenaModelFactory();			
		}
		
		//
		// define jena-model factory for the output IFC model 
		//
		JenaModelFactoryBase outputMetaModelJenaModelFactory = null;		
		if (!StringUtils.isEmptyOrNull(outputMetaModelName)) {
			outputMetaModelJenaModelFactory = getJenaModelFactory(jenaModelFactoryConfigurationPool, outputMetaModelName);
		} else if (!StringUtils.isEmptyOrNull(outputMetaModelFilePath)) {
			outputMetaModelJenaModelFactory =  new MemoryJenaModelFactory();			
		}

		try {
			
			RDFFormat outputFileFormat = null;
			boolean gzipOutputFile = false;
			
			if (outputFileFormatName != null) {
				outputFileFormatName = outputFileFormatName.toUpperCase();
				
				String[] tokens = outputFileFormatName.split("\\.");
				
				if (tokens.length == 2) {
					if (tokens[1].equals("GZIP") || tokens[1].equals("GZ")) {
						gzipOutputFile = true;
					} else {
						throw new IfcException(String.format("Unknown ZIP format: '%s'", tokens[1]));
					}
				}
				
				try {
					outputFileFormat = (RDFFormat) RDFFormat.class.getField(tokens[0]).get(null);		
				} catch (NoSuchFieldException e) {
					throw new IfcException(
							String.format("Unknown RDF format: '%s', see: %s", tokens[0], IfcCommandLineOptions.URL_RIOT_FORMAT));
				}
			}			
			
		
			//
			// parse and export schema
			//
			System.err.printf("Exporting schemas%n");

			final List<IfcSchema> schemas = parseSchemas(inputSchemaFilePath);
			
			for (IfcSchema schema : schemas) {
				System.out.printf("SCHEMA %s", schema.getVersion());
				schema.getAllTypeInfos().stream().filter(t -> t instanceof IfcSelectTypeInfo).forEach(t -> {
					EnumSet<IfcTypeEnum> valueTypes = t.getValueTypes();
					if (valueTypes.size() > 1) {
						System.out.printf("SELECT type %s %s%n", t, valueTypes);
					}
				});
			}
			
			
			if (outputSchemaJenaModelFactory != null) {
				for (IfcSchema schema : schemas) {
					exportSchema(outputSchemaJenaModelFactory, schema, outputLayerName, outputSchemaFilePath, outputFileFormat, gzipOutputFile);
				}
			}
			
			//
			// parse model
			//
			if (outputModelJenaModelFactory != null || outputMetaModelJenaModelFactory != null) {				
				IfcModel model = parseModel(inputModelFilePath);
				
				//
				// export model
				//
				if (outputModelJenaModelFactory != null) {
					exportModel(outputModelJenaModelFactory, model, outputLayerName, outputModelFilePath, outputFileFormat, gzipOutputFile);
				}
				
				//
				// export meta-model
				//
				if (outputMetaModelJenaModelFactory != null) {				
					exportMetaModel(outputMetaModelJenaModelFactory, model, outputLayerName, outputMetaModelFilePath, outputFileFormat, gzipOutputFile);				
				}
				
			}			

		} finally {
		
			//
			//  release jena-model factories
			//
			if (outputSchemaJenaModelFactory != null) {
				outputSchemaJenaModelFactory.release();
			}
			
			if (outputModelJenaModelFactory != null) {
				outputModelJenaModelFactory.release();
			}

		}
		
		
		logger.info("END OF PROGRAM");
		
	}

	/**
	 * Loads logger configuration
	 * @throws FactoryConfigurationError
	 */
	private static void loadLoggerConfigration(String loggerConfigFilePath) throws FactoryConfigurationError {
		if (loggerConfigFilePath.endsWith("xml")) {
			DOMConfigurator.configure(loggerConfigFilePath);			
		} else {
			PropertyConfigurator.configure(loggerConfigFilePath);			
		}
	}
	
	/**
	 * Loads configuration document
	 * @throws ConfigurationParserException
	 */
	private static void loadConfiguration(String configFilePath) throws ConfigurationParserException {
		logger.info(String.format("Loading configuration in '%s'", configFilePath));
		ConfigurationDocument.load(configFilePath);
		logger.info("Loading configuration has been completed successfully");
	}

	/**
	 * Gets Jena-model factory configuration pool  
	 * @return
	 * @throws ConfigurationParserException
	 */
	private static ConfigurationPool<ConfigurationItemEx> getJenaModelFactoryConfigurationPool()
			throws ConfigurationParserException {
		return JenaModelFactoryPoolConfigurationSection.getInstance().getConfigurationPool();
	}
	
	/**
	 * Gets a Jena model factory by name from a pool
	 * @param jenaModelFactoryConfigurationPool
	 * @param jenaModelFactoryName
	 * @return
	 * @throws Exception
	 */
	private static JenaModelFactoryBase getJenaModelFactory(
			ConfigurationPool<ConfigurationItemEx> jenaModelFactoryConfigurationPool,
			String jenaModelFactoryName) throws Exception {
		
		try {
			ConfigurationItemEx configuration = jenaModelFactoryConfigurationPool.getByName(jenaModelFactoryName);
			return JenaModelFactoryBase.getFactory(configuration);
		} catch(InvalidParameterException e) {
			throw new IfcException(String.format("Jena model %s is not found", jenaModelFactoryName), e);
		}
		
	}

	
	/**
	 * Imports IFC schema from EXPRESS file
	 * @param inputSchemaFilePath
	 * @return
	 * @throws IOException
	 * @throws IfcParserException
	 */
	private static List<IfcSchema> parseSchemas(String inputSchemaFilePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing schema from file or folder '%s'", inputSchemaFilePath));
		final List<IfcSchema> schemas = IfcParserUtil.parseSchemas(inputSchemaFilePath);
		logger.info("Parsing schema is compeleted");
		return schemas;
	}

	/**
	 * Exports schema
	 * @param outputSchemaJenaModelFactory
	 * @param schema
	 * @param outputSchemaFilePath
	 * @param outputFileFormat
	 * @param gzipOutputFile 
	 * @throws Exception
	 */
	private static Model exportSchema(
			JenaModelFactoryBase outputSchemaJenaModelFactory,
			IfcSchema schema,
			String conversionContextName,
			String outputSchemaFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile)
			throws Exception {
		// export model to RDF graph
		logger.info("Exporting schema to RDF graph");
		Model schemaGraph = outputSchemaJenaModelFactory.createModel();
		if (schemaGraph.supportsTransactions()) {
			schemaGraph.begin();				
		}
		Ifc2RdfExportUtil.exportSchemaToJenaModel(schemaGraph, schema, conversionContextName);
		if (schemaGraph.supportsTransactions()) {
			schemaGraph.commit();
		}
		logger.info("Exporting schema RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputSchemaFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(schemaGraph, outputSchemaFilePath, outputFileFormat, gzipOutputFile);
		}
		return schemaGraph;
	}

	/**
	 * Imports IFC model from a STEP file
	 * @param inputModelFilePath 
	 * @return
	 * @throws IOException
	 * @throws IfcParserException
	 */
	private static IfcModel parseModel(String inputModelFilePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing model from file '%s'", inputModelFilePath));
		IfcModel model = IfcParserUtil.parseModel(inputModelFilePath);
		logger.info("Parsing model is completed");
		return model;
	}
	
	/**
	 * Exports IFC model to a Jena model (and writes it to a file if needed)
	 * @param outputModelJenaModelFactory
	 * @param model
	 * @param contextName
	 * @param outputModelFilePath
	 * @param outputFileLanguage
	 * @throws Exception
	 */
	private static Model exportModel(
			JenaModelFactoryBase outputModelJenaModelFactory,
			IfcModel model,
			String conversionContextName,
			String outputModelFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile) throws Exception {
		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		// export model to RDF graph
		logger.info("Exporting model to RDF graph");
		Model modelGraph = outputModelJenaModelFactory.createModel();
		if (modelGraph.supportsTransactions()) {
			logger.info("Enabling RDF graph transactions");
			modelGraph.begin();				
		}
		Ifc2RdfExportUtil.exportModelToJenaModel(modelGraph, model, conversionContextName);
		if (modelGraph.supportsTransactions()) {
			logger.info("Committing RDF graph transactions");
			modelGraph.commit();
		}
		logger.info("Exporting model to RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputModelFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(modelGraph, outputModelFilePath, outputFileFormat, gzipOutputFile);
		}
		
		return modelGraph;
	}
	
	/**
	 * Exports IFC model to a Jena model (and writes it to a file if needed)
	 * @param outputModelJenaModelFactory
	 * @param model
	 * @param contextName
	 * @param outputModelFilePath
	 * @param outputFileLanguage
	 * @throws Exception
	 */
	private static Model exportMetaModel(
			JenaModelFactoryBase outputMetaModelJenaModelFactory,
			IfcModel model,
			String conversionContextName,
			String outputMetaModelFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile) throws Exception {
		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		// export model to RDF graph
		logger.info("Exporting meta model to RDF graph");
		Model modelGraph = outputMetaModelJenaModelFactory.createModel();
		if (modelGraph.supportsTransactions()) {
			logger.info("Enabling RDF graph transactions");
			modelGraph.begin();				
		}
		Ifc2RdfExportUtil.exportMetaModelToJenaModel("http://example.org", modelGraph, model, conversionContextName);
		if (modelGraph.supportsTransactions()) {
			logger.info("Committing RDF graph transactions");
			modelGraph.commit();
		}
		logger.info("Exporting meta model to RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputMetaModelFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(modelGraph, outputMetaModelFilePath, outputFileFormat, gzipOutputFile);
		}
		
		return modelGraph;
	}
	
	
//	private void logError(Object message, Throwable t) {
//	logger.error(message, t);
//	Logger.getLogger(t.getClass()).error(message, t);
//}

	
//	protected void testSchema(IfcSchema schema) {
//		for (IfcEntityTypeInfo entityInfo : schema.getEntityTypeInfos()) {
//			for (IfcInverseLinkInfo inverseLinkInfo : entityInfo.getInverseLinkInfos()) {
//				if (inverseLinkInfo.getCardinality().isSingle() && !inverseLinkInfo.getCardinality().isOptional()) {
//					IfcLinkInfo outgoingLinkInfo = inverseLinkInfo.getOutgoingLinkInfo();
//					if (outgoingLinkInfo.getCardinality().isSingle()) {
//						System.out.println(String.format("%s.%s<--%s.%s", inverseLinkInfo
//								.getDestinationEntityTypeInfo().getName(), inverseLinkInfo.getName(), outgoingLinkInfo
//								.getEntityTypeInfo().getName(), outgoingLinkInfo.getName()));
//					}
//				}
//			}
//		}
//	}


}
