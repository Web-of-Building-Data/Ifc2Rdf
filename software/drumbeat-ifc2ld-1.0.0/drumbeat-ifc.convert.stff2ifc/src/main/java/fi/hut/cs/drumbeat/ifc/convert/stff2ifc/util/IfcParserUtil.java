package fi.hut.cs.drumbeat.ifc.convert.stff2ifc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.convert.stff2ifc.IfcModelParser;
import fi.hut.cs.drumbeat.ifc.convert.stff2ifc.IfcParserException;
import fi.hut.cs.drumbeat.ifc.convert.stff2ifc.IfcSchemaParser;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchemaPool;



public class IfcParserUtil {
	
	private static final Logger logger = Logger.getRootLogger();
	
	public static List<IfcSchema> parseSchemas(String filePath) throws IOException, IfcParserException {
		File file = new File(filePath);
		
		final List<IfcSchema> schemas = new ArrayList<>();
		
		if (file.isDirectory()) {
			logger.info(String.format("Parsing schemas in folder '%s'", filePath));
			FileNameExtensionFilter filter = new FileNameExtensionFilter(null, IfcVocabulary.ExpressFormat.FILE_EXTENSION);
			for (File schemaFile : file.listFiles()) {
				if (filter.accept(schemaFile)) {
					final IfcSchema schema = parseSchema(schemaFile.getPath());
					schemas.add(schema);
				}
			}
			logger.info(String.format("Parsing schemas in folder '%s' has been completed successfully", filePath));
		} else {
			final IfcSchema schema = parseSchema(filePath);
			schemas.add(schema);
		}
		return schemas;
	}	
	
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
