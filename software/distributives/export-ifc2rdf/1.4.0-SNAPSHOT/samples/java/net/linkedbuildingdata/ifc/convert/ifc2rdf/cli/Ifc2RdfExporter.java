package net.linkedbuildingdata.ifc.convert.ifc2rdf.cli;
import java.io.IOException;
import java.security.InvalidParameterException;

import javax.xml.parsers.FactoryConfigurationError;

import net.linkedbuildingdata.common.config.*;
import net.linkedbuildingdata.common.config.document.*;
import net.linkedbuildingdata.common.string.StringUtils;
import net.linkedbuildingdata.ifc.IfcException;
import net.linkedbuildingdata.ifc.convert.ifc2rdf.util.Ifc2RdfExportUtil;
import net.linkedbuildingdata.ifc.convert.step2ifc.IfcParserException;
import net.linkedbuildingdata.ifc.convert.step2ifc.util.IfcParserUtil;
import net.linkedbuildingdata.ifc.data.model.IfcModel;
import net.linkedbuildingdata.ifc.data.schema.*;
import net.linkedbuildingdata.ifc.util.IfcModelAnalyser;
import net.linkedbuildingdata.rdf.RdfUtils;
import net.linkedbuildingdata.rdf.modelfactory.JenaModelFactoryBase;
import net.linkedbuildingdata.rdf.modelfactory.MemoryJenaModelFactory;
import net.linkedbuildingdata.rdf.modelfactory.config.JenaModelFactoryPoolConfigurationSection;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

import org.apache.jena.riot.RDFFormat;

public class Ifc2RdfExporter {
	
	private static final Logger logger = Logger.getRootLogger();
	
	private String inputSchemaFilePath;
	private String inputModelFilePath;
	private String outputSchemaFilePath;
	private String outputSchemaName;
	private String outputModelFilePath;
	private String outputModelName;
	private String outputFileFormatName;
	
	public Ifc2RdfExporter(
		 String inputSchemaFilePath,
		 String inputModelFilePath,
		 String outputLayerName,
		 String outputSchemaFilePath,
		 String outputSchemaName,
		 String outputModelFilePath,
		 String outputModelName,
		 String outputFileFormatName)
	{
		this.inputSchemaFilePath = inputSchemaFilePath;
		this.inputModelFilePath = inputModelFilePath;
		this.outputSchemaFilePath = outputSchemaFilePath;
		this.outputSchemaName = outputSchemaName;
		this.outputModelFilePath = outputModelFilePath;
		this.outputModelName = outputModelName;
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
					throw new IfcException(String.format("Unknown RDF format: '%s', see: https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/riot/RDFFormat.html",
							tokens[0]));
				}
			}			
			
		
			//
			// parse and export schema
			//
			IfcSchema schema = parseSchema(inputSchemaFilePath);
			if (outputSchemaJenaModelFactory != null) {	
				exportSchema(outputSchemaJenaModelFactory, schema, outputSchemaFilePath, outputFileFormat, gzipOutputFile);
			}
			
			//
			// parse and export model
			//
			if (outputModelJenaModelFactory != null) {				
				IfcModel model = parseModel(inputModelFilePath);				
				exportModel(outputModelJenaModelFactory, model, outputModelFilePath, outputFileFormat, gzipOutputFile);				
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
	private static IfcSchema parseSchema(String inputSchemaFilePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing schema from file '%s'", inputSchemaFilePath));
		IfcSchema schema = IfcParserUtil.parseSchema(inputSchemaFilePath);
		logger.info("Parsing schema is compeleted");
		return schema;
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
		Ifc2RdfExportUtil.exportSchemaToJenaModel(schemaGraph, schema, null);
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
	 * @param outputModelFilePath
	 * @param outputFileLanguage
	 * @throws Exception
	 */
	private static Model exportModel(
			JenaModelFactoryBase outputModelJenaModelFactory,
			IfcModel model,
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
		Ifc2RdfExportUtil.exportModelToJenaModel(modelGraph, model, null);
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
