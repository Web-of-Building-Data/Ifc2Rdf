package fi.hut.cs.drumbeat.common.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class FileManager {

	public static File createDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
	
	public static String createFileName(File directory, String fileName) {
		return String.format("%s%s%s", directory.getAbsolutePath(), File.separatorChar, fileName);
	}
	
	public static File createFile(String filePath) throws IOException {
		File file = new File(filePath);
		File parentFile = file.getParentFile();
		if (parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		return file;
	}
	
	
	public static FileWriter createFileWriter(String filePath) throws IOException {		
		File file = new File(filePath);
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			parentFile.mkdirs();
		}
		return new FileWriter(file);
	}

	public static FileOutputStream createFileOutputStream(String filePath) throws IOException {
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}
	
	public static String getReadableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static String createFileNameWithExtension(String filePath, String extension) {
		
		String tempFilePath = filePath;
		
		String[] tokens = extension.split("\\.");
		
		int firstExtentionToBeAdded = tokens.length;
		
		for (int i = tokens.length - 1; i >= 0; --i) {
			tokens[i] = "." + tokens[i];
			if (tempFilePath.endsWith(tokens[i])) {
				tempFilePath = tempFilePath.substring(0, tempFilePath.length() - tokens[i].length());
			} else {
				firstExtentionToBeAdded = i;
			}
		}
		
		for (int i = firstExtentionToBeAdded; i < tokens.length; ++i) {
			filePath += tokens[i];
		}
		
		return filePath;
	}

}
