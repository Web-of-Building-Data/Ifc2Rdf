package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;



/**
 * Exports IFC schemas and models to binary files .
 * 
 * @author Nam Vu
 *
 */
public class Ifc2BinaryFileExportUtil {
	
	private static final Logger logger = Logger.getRootLogger();
	
	public static void exportSchemaToBinaryFile(IfcSchema schema, String filePath) throws IOException {		
		logger.info(String.format("Exporting schema to a binary file '%s' (%,d types)", filePath, schema.getAllTypeInfos().size()));
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filePath);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(schema);
			logger.info(
					String.format("Exporting schema has been completed successfully (%,d bytes)", new File(filePath).length()));
		} catch (IOException e) {
			logger.error("Error exporting schema", e);
			throw e;
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}				
			}
		}
		
	}

	public static IfcSchema importSchemaFromBinaryFile(String filePath) throws Exception {
		logger.info(String.format("Importing schema from a binary file '%s'", filePath));
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			IfcSchema schema = (IfcSchema)objectInputStream.readObject();
			logger.info(
					String.format("Importing schema has been completed successfully (%,d types)", schema.getAllTypeInfos().size()));
			return schema;
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Error exporting schema", e);
			throw e;
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		
	}

	public static void exportModelToBinaryFile(IfcModel model, String filePath) throws IOException {		
		logger.info(String.format("Exporting model to a binary file '%s' (%,d entities)", filePath, model.getEntities().size()));
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filePath);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(model);
			logger.info(
					String.format("Exporting model has been completed successfully (%,d bytes)", new File(filePath).length()));
		} catch (IOException e) {
			logger.error("Error exporting model", e);
			throw e;
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
			}
		}
		
	}

	public static IfcModel importModelFromBinaryFile(String filePath) throws Exception {
		logger.info(String.format("Importing model from a binary file '%s'", filePath));
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			IfcModel model = (IfcModel)objectInputStream.readObject();
			logger.info(
					String.format("Importing model has been completed successfully (%,d entities)", model.getEntities().size()));
			return model;
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Error exporting model", e);
			throw e;
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		
	}
	

}
