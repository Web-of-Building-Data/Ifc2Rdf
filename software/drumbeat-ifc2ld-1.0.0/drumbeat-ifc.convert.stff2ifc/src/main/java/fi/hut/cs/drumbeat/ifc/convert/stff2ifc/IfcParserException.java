package fi.hut.cs.drumbeat.ifc.convert.stff2ifc;

import fi.hut.cs.drumbeat.ifc.common.IfcException;

public class IfcParserException extends IfcException {

	private static final long serialVersionUID = 1L;

	public IfcParserException() {
	}

	public IfcParserException(String message) {
		super(message);
	}

	public IfcParserException(Throwable cause) {
		super(cause);
	}

	public IfcParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public IfcParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
