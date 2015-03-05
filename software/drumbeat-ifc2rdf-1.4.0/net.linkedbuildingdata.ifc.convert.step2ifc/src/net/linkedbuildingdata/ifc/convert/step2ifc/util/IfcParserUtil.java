package net.linkedbuildingdata.ifc.convert.step2ifc.util;

import java.io.FileInputStream;
import java.io.IOException;

import net.linkedbuildingdata.ifc.convert.step2ifc.IfcModelParser;
import net.linkedbuildingdata.ifc.convert.step2ifc.IfcParserException;
import net.linkedbuildingdata.ifc.convert.step2ifc.IfcSchemaParser;
import net.linkedbuildingdata.ifc.data.model.IfcModel;
import net.linkedbuildingdata.ifc.data.schema.IfcSchema;
import net.linkedbuildingdata.ifc.data.schema.IfcSchemaPool;

import org.apache.log4j.Logger;



public class IfcParserUtil {
	
	private static final Logger logger = Logger.getRootLogger();
	
	public static IfcSchema parseSchema(String filePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing schema in '%s'", filePath));

		FileInputStream input = new FileInputStream(filePath);

		IfcSchema schema = IfcSchemaParser.parse(input);
		IfcSchemaPool.addSchema(schema);

		logger.info("Parsing schema has been completed successfully");
		return schema;
	}

	public static IfcModel parseModel(String filePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing model in '%s'", filePath));
		FileInputStream input = new FileInputStream(filePath);
		IfcModel model = IfcModelParser.parse(input);
		logger.info("Parsing model has been completed successfully");
		return model;
	}
	

}
