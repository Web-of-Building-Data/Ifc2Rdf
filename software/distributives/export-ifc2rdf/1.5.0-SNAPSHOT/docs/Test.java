package fi.hut.cs.drumbeat.ifc.convert.ifc2rdf.cli;

import java.util.List;

import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.common.config.ComplexProcessorConfiguration;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.ifc.convert.ifc2rdf.util.Ifc2RdfExportUtil;
import fi.hut.cs.drumbeat.ifc.convert.step2ifc.util.IfcParserUtil;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.util.IfcModelAnalyser;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;
import fi.hut.cs.drumbeat.rdf.modelfactory.MemoryJenaModelFactory;

public class Test {

	private String loggerConfigFilePath;
	private String configFilePath;
	private String inputSchemaFilePath;
	private String inputModelFilePath;
	private String outputSchemaFilePath;
	private String outputModelFilePath;
	RDFFormat outputFileFormat;
	boolean gzipOutputFile;

	public void run() throws Exception {
		
		//
		// load logger configuration
		//
		PropertyConfigurator.configure(loggerConfigFilePath);			
		
		//
		// load converter configuration
		//
		ConfigurationDocument.load(configFilePath);
		
		//
		// load IFC schemas
		//
		List<IfcSchema> schemas = IfcParserUtil.parseSchemas(inputSchemaFilePath);
		
		// export IFC schema(s)
		//
		final JenaModelFactoryBase jenaModelFactory = new MemoryJenaModelFactory();
		
		for (IfcSchema schema : schemas) {
			// export IFC schema into in-memory Jena graph using default conversion context
			Model schemaGraph = jenaModelFactory.createModel();
			Ifc2RdfExportUtil.exportSchemaToJenaModel(schemaGraph, schema);		

			// export the in-memory Jena graph to file  
			RdfUtils.exportJenaModelToRdfFile(schemaGraph, outputSchemaFilePath, outputFileFormat, gzipOutputFile);			
		}
		
		//
		// load IFC model
		//
		IfcModel model = IfcParserUtil.parseModel(inputModelFilePath);		

		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		
		//
		// export IFC model
		//
		
		// export IFC model into in-memory Jena graph using default conversion context
		Model modelGraph = jenaModelFactory.createModel();
		Ifc2RdfExportUtil.exportModelToJenaModel(modelGraph, model);
		
		// export the in-memory Jena graph to file  
		RdfUtils.exportJenaModelToRdfFile(modelGraph, outputModelFilePath, outputFileFormat, gzipOutputFile);
		
	}
	
}
