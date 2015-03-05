package net.linkedbuildingdata.ifc.convert.ifc2rdf.util;


import net.linkedbuildingdata.common.config.document.ConfigurationParserException;
import net.linkedbuildingdata.ifc.convert.ifc2rdf.Ifc2RdfConversionContext;
import net.linkedbuildingdata.ifc.convert.ifc2rdf.Ifc2RdfModelExporter;
import net.linkedbuildingdata.ifc.convert.ifc2rdf.Ifc2RdfSchemaExporter;
import net.linkedbuildingdata.ifc.data.model.IfcModel;
import net.linkedbuildingdata.ifc.data.schema.IfcSchema;
import net.linkedbuildingdata.rdf.export.JenaModelExportAdapter;
import net.linkedbuildingdata.rdf.export.RdfExportAdapter;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;


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
			defaultContext = Ifc2RdfConversionContext.loadFromConfigurationFile(); 
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
		exportSchemaToJenaModel(jenaModel, schema, null);
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
	 * Exports an IFC model to Jena model using the default IFC-to-RDF conversion context.
	 * 
	 * @param jenaModel a Jena {@link Model} (the target).
	 * @param model an {@link IfcModel} (the source).
	 * 
	 * @throws Exception
	 */
	public static void exportModelToJenaModel(Model jenaModel, IfcModel model) throws Exception {
		exportModelToJenaModel(jenaModel, model, null);
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

}
