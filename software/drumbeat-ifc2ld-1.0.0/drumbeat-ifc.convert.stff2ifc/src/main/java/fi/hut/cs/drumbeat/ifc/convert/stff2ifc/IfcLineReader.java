package fi.hut.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;


public class IfcLineReader {
	
	public static final String OPENING_COMMENT = "(*"; 
	public static final String CLOSING_COMMENT = "*)";
	public static final int LINE_NUMBER_OF_END = -1;
	
	// 
	// Regular expression pattern of a statement that doesn't consist a semicolon inside single quotes
	//
	private static final Pattern STATEMENT = Pattern.compile("([^;\\']|(\\'([^\\']|(\\'\\'))*\\'))+;");
	
	
	/**
	 * Temporary variables
	 */
	// The input reader
	protected BufferedReader reader;
	
	// Buffer for reading lines 
	private StringBuilder cache;
	
	private long currentLineNumber;
	
	public IfcLineReader(InputStream input) {
		reader = new BufferedReader(new InputStreamReader(input));
		cache = new StringBuilder();
		currentLineNumber = 0;
	}
	

	private String getNextLine() throws IOException, IfcFormatException {
		++currentLineNumber;
		String line = reader.readLine();

		if (line == null) {
			currentLineNumber = LINE_NUMBER_OF_END;
			return null;
		}
		
		if (line.startsWith(OPENING_COMMENT)) {
			for (;;) {
				++currentLineNumber;
				line = reader.readLine();
				if (line != null) {
					if (line.startsWith(CLOSING_COMMENT)) {
						return getNextLine();
					}
				} else {
					currentLineNumber = LINE_NUMBER_OF_END;
					throw new IfcFormatException(currentLineNumber, "Expected closing comment");					
				}
			}
		}
		
		return !line.isEmpty() ? line : getNextLine();
	}
	
	public long getCurrentLineNumber() {
		return currentLineNumber;
	}
	

	/**
	 * Gets the next statement from the input stream   
	 * @return Tokens of the next statement in the file (without semicolon)
	 * @throws IOException
	 * @throws IfcFormatException
	 */
//	public String getNextStatement() throws IOException, IfcFormatException {
//		
//		String line = "";
//		
//		if (stringBuilder.length() > 0) {
//			line = stringBuilder.toString();
//			stringBuilder = new StringBuilder();			
//		}
//		
//		for (;;) {
//
//			if (!line.isEmpty()) {
//				
//				//
//				// skip comments (if any)
//				//
//				if (stringBuilder.length() == 0 && line.startsWith(OPENING_COMMENT)) {
//					while (!line.trim().endsWith(CLOSING_COMMENT)) {
//						line = getNextLine();
//						//line = reader.readLine();
//						if (line == null) {
//							throw new IfcFormatException("Expected closing comment");
//						}
//					}
//					return getNextStatement();
//				}
//				
//				//
//				// Find the end of the statement and return its tokens
//				//
//				// Check if semicolon is inside a string with quotes!
////				int indexOfSemicolon = line.lastIndexOf(StringUtils.SEMICOLON_CHAR);
////				if (indexOfSemicolon > 0) {					
////					if (line.lastIndexOf(StringUtils.SEMICOLON_CHAR, indexOfSemicolon - 1) == -1) {
////						String statement = line.substring(0, indexOfSemicolon);
////						
////						stringBuilder.append(' ').append(statement);
////						statement = stringBuilder.toString().trim();
////						
////						// put the rest part of line to the string buffer
////						stringBuilder = new StringBuilder(line.substring(indexOfSemicolon + 1));
////						
////						return statement;							
////					} else {
//						String statement = line; // .substring(0, indexOfSemicolon + 1);
//						Matcher matcher = STATEMENT.matcher(statement);
//						if (matcher.find(0)) {
//							int end = matcher.end();
//							statement = statement.substring(0, end - 1);
//							
//							stringBuilder.append(' ').append(statement);
//							statement = stringBuilder.toString().trim();
//							
//							// put the rest part of line to the string buffer
//							stringBuilder = new StringBuilder(line.substring(end));
//							
//							return statement;							
//						}						
////					}
////				}
//				
//				stringBuilder.append(' ').append(line);					
//			}
//			
//			//
//			// read next line
//			//
//			//line = reader.readLine();
//			line = getNextLine();
//			if (line == null) {
//				if (stringBuilder.length() == 0) {
//					return null;
//				} else {
//					throw new IfcFormatException(String.format("Expected '%s'", StringUtils.SEMICOLON));
//				}
//			}
//		}		
//	}
	
	public String getNextStatement() throws IOException, IfcFormatException {
		
		int length;
		
		for (;;) {			
			
			if ((length = cache.length()) > 0) {
				int indexOfEndLine = cache.indexOf(IfcVocabulary.StepFormat.END_LINE);
				
				if (indexOfEndLine >= 0) {
					
					int indexOfApostrophe = cache.lastIndexOf(IfcVocabulary.StepFormat.STRING_VALUE, indexOfEndLine);
					if (indexOfApostrophe >= 0) {
						Matcher matcher = STATEMENT.matcher(cache);
						indexOfEndLine = matcher.find(0) ? matcher.end() - 1 : -1;  
					}
					
					if (indexOfEndLine > 0) {
						String statement = cache.substring(0, indexOfEndLine).trim();
						
						if (indexOfEndLine == length - 1) {
							cache = new StringBuilder();
						} else {
							cache = new StringBuilder(cache.substring(indexOfEndLine + 1));
						}
									
						return statement;						
					}
					
				}
				
			}
				
			
			String line = getNextLine();
			
			if (line == null) {
				if (cache.length() == 0) {
					return null;
				} else {
					throw new IfcFormatException(currentLineNumber, String.format("Expected '%s'", StringUtils.SEMICOLON));
				}
			}
			
			cache.append(line);				
			
		}		
	}

	
}
