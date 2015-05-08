package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.util;


import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfMetaModelExporter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfModelExporter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfSchemaExporter;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.rdf.export.JenaModelExportAdapter;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


/**
 * Contains main entry points for exporting IFC schemas and IFC models to RDF. 
 * @author Nam Vu
 *
 */
public class Ifc2RdfExportUtil {
	
	private static final Logger logger = Logger.getRootLogger();
	
	private static Ifc2RdfConversionContext defaultContext = null;
	
	
	
	/**
	 * Gets the default IFC-to-RDF conversion context loaded from the configuration file.
	 * 
	 * @return the default {@link Ifc2RdfConversionContext} object
	 *  
	 * @throws ConfigurationParserException
	 */
	public static Ifc2RdfConversionContext getDefaultConversionContext() throws ConfigurationParserException {
		if (defaultContext == null) {
			defaultContext = Ifc2RdfConversionContext.loadFromConfigurationFile(null); 
		}
		return defaultContext;
	}
	
	
	
	/**
	 * Exports an IFC schema to Jena model using the default IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param schema an {@link IfcSchema} (the source).
	 * 
	 * @throws Exception
	 */
	public static void exportSchemaToJenaModel(Model jenaModel, IfcSchema schema) throws Exception {
		exportSchemaToJenaModel(jenaModel, schema, (Ifc2RdfConversionContext)null);
	}


	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param schema an {@link IfcSchema} (the source).
	 * @param context an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportSchemaToJenaModel(Model jenaModel, IfcSchema schema, Ifc2RdfConversionContext context) throws Exception {
		
		if (context == null) {
			context = getDefaultConversionContext();
		}
		
		logger.info("Exporting schema to Jena");
		try {
			
			RdfExportAdapter rdfExportAdapter = new JenaModelExportAdapter(jenaModel);			
			
			new Ifc2RdfSchemaExporter(schema, context, rdfExportAdapter).export();
			
			logger.info("Exporting schema has been completed successfully");
			
		} catch (Exception e) {
			logger.error("Error exporting schema", e);
			throw e;
		}
	}
	
	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param schema an {@link IfcSchema} (the source).
	 * @param contextName an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportSchemaToJenaModel(Model jenaModel, IfcSchema schema, String contextName) throws Exception {
		Ifc2RdfConversionContext context = Ifc2RdfConversionContext.loadFromConfigurationFile(contextName);
		exportSchemaToJenaModel(jenaModel, schema, context);
	}
	
	
	/**
	 * Exports an IFC model to Jena model using the default IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * 
	 * @throws Exception
	 */
	public static void exportModelToJenaModel(Model jenaModel, IfcModel model) throws Exception {
		exportModelToJenaModel(jenaModel, model, (Ifc2RdfConversionContext)null);
	}


	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * @param context an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportModelToJenaModel(Model jenaModel, IfcModel model, Ifc2RdfConversionContext context) throws Exception {
		if (context == null) {
			context = getDefaultConversionContext();
		}

		logger.info("Exporting model to Jena");
		try {
			RdfExportAdapter rdfExportAdapter = new JenaModelExportAdapter(jenaModel);			

			new Ifc2RdfModelExporter(model, context, rdfExportAdapter).export();
			
			logger.info("Exporting model has been completed successfully");
			
		} catch (Exception e) {
			logger.error("Error exporting model", e);
			throw e;
		}		
	}
	
	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * @param contextName an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportModelToJenaModel(Model jenaModel, IfcModel model, String contextName) throws Exception {
		Ifc2RdfConversionContext context = Ifc2RdfConversionContext.loadFromConfigurationFile(contextName);
		exportModelToJenaModel(jenaModel, model, context);
	}

	/**
	 * Exports an IFC model to Jena model using the default IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * 
	 * @throws Exception
	 */
	public static void exportMetaModelToJenaModel(String metaDataSetUri, Model jenaModel, IfcModel model) throws Exception {
		exportMetaModelToJenaModel(metaDataSetUri, jenaModel, model, (Ifc2RdfConversionContext)null);
	}


	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * @param context an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportMetaModelToJenaModel(String metaDataSetUri, Model jenaModel, IfcModel model, Ifc2RdfConversionContext context) throws Exception {
		if (context == null) {
			context = getDefaultConversionContext();
		}

		logger.info("Exporting model to Jena");
		try {
			RdfExportAdapter rdfExportAdapter = new JenaModelExportAdapter(jenaModel);			

			new Ifc2RdfMetaModelExporter(metaDataSetUri, model, context, rdfExportAdapter).export();
			
			logger.info("Exporting model has been completed successfully");
			
		} catch (Exception e) {
			logger.error("Error exporting model", e);
			throw e;
		}		
	}
	
	/**
	 * Exports an IFC schema to Jena model using a specified IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * @param contextName an {@link Ifc2RdfConversionContext} (the null param indicates to use the default context).  
	 * 
	 * @throws Exception
	 */
	public static void exportMetaModelToJenaModel(String metaDataSetUri, Model jenaModel, IfcModel model, String contextName) throws Exception {
		Ifc2RdfConversionContext context = Ifc2RdfConversionContext.loadFromConfigurationFile(contextName);
		exportMetaModelToJenaModel(metaDataSetUri, jenaModel, model, context);
	}
}
