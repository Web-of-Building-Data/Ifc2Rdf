package fi.hut.cs.drumbeat.ifc.convert.step2ifc;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.hut.cs.drumbeat.common.string.StringUtils;


public class IfcLineReader {
	
	public static final String OPENING_COMMENT = "(*"; 
	public static final String CLOSING_COMMENT = "*)";
	
	private static final Pattern STATEMENT = Pattern.compile("([^;\\']|(\\'([^\\']|(\\'\\'))*\\'))+;");
	
	
	/**
	 * Temporary variables
	 */
	// The input reader
	protected BufferedReader reader;
	
	// Buffer for reading lines 
	private StringBuilder stringBuilder;
	
	private long lineNumber = 0;
	
	private String getNextLine() throws IOException {
		++lineNumber;
//		System.out.println(lineNumber);
		return reader.readLine();
	}
	
	public long getLineNumber() {
		return lineNumber;
	}
	

	public IfcLineReader(InputStream input) {
		reader = new BufferedReader(new InputStreamReader(input));
		stringBuilder = new StringBuilder();		
	}
	
	
	/**
	 * Gets the next statement from the input stream   
	 * @return Tokens of the next statement in the file (without semicolon)
	 * @throws IOException
	 * @throws IfcFormatException
	 */
	public String getNextStatement() throws IOException, IfcFormatException {
		
		String line = "";
		
		if (stringBuilder.length() > 0) {
			line = stringBuilder.toString();
			stringBuilder = new StringBuilder();			
		}
		
		for (;;) {

			if (!line.isEmpty()) {
				
				//
				// skip comments (if any)
				//
				if (stringBuilder.length() == 0 && line.startsWith(OPENING_COMMENT)) {
					while (!line.trim().endsWith(CLOSING_COMMENT)) {
						line = getNextLine();
						//line = reader.readLine();
						if (line == null) {
							throw new IfcFormatException("Expected closing comment");
						}
					}
					return getNextStatement();
				}
				
				//
				// Find the end of the statement and return its tokens
				//
				// Check if semicolon is inside a string with quotes!
				int indexOfSemicolon = line.lastIndexOf(StringUtils.SEMICOLON_CHAR);
				if (indexOfSemicolon > 0) {					
					if (line.lastIndexOf(StringUtils.SEMICOLON_CHAR, indexOfSemicolon - 1) == -1) {
						String statement = line.substring(0, indexOfSemicolon);
						
						stringBuilder.append(' ').append(statement);
						statement = stringBuilder.toString().trim();
						
						// put the rest part of line to the string buffer
						stringBuilder = new StringBuilder(line.substring(indexOfSemicolon + 1));
						
						return statement;							
					} else {
						String statement = line.substring(0, indexOfSemicolon + 1);
						Matcher matcher = STATEMENT.matcher(statement);
						if (matcher.find(0)) {
							int end = matcher.end();
							statement = statement.substring(0, end - 1);
							
							stringBuilder.append(' ').append(statement);
							statement = stringBuilder.toString().trim();
							
							// put the rest part of line to the string buffer
							stringBuilder = new StringBuilder(line.substring(end));
							
							return statement;							
						}						
					}
				}
				
				stringBuilder.append(' ').append(line);					
			}
			
			//
			// read next line
			//
			//line = reader.readLine();
			line = getNextLine();
			if (line == null) {
				if (stringBuilder.length() == 0) {
					return null;
				} else {
					throw new IfcFormatException(String.format("Expected '%s'", StringUtils.SEMICOLON));
				}
			}
		}		
	}
	
}
