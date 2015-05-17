package fi.hut.cs.drumbeat.ifc.convert.stff2ifc;

public class IfcFormatException extends IfcParserException {

	private static final long serialVersionUID = 1L;

	public IfcFormatException(long lineNumber) {
	}

	public IfcFormatException(long lineNumber, String message) {
		super(formatMessage(lineNumber, message));
	}

	public IfcFormatException(long lineNumber, Throwable cause) {
		super(cause);
	}

	public IfcFormatException(long lineNumber, String message, Throwable cause) {
		super(formatMessage(lineNumber, message), cause);
	}

	public IfcFormatException(long lineNumber, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(formatMessage(lineNumber, message), cause, enableSuppression, writableStackTrace);
	}
	
	private static String formatMessage(long lineNumber, String message) {
		return String.format("%s [LINE %d]", message, lineNumber);
	}
	

}
