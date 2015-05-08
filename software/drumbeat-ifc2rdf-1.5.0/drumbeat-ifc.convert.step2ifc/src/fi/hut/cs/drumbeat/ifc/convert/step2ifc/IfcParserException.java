package fi.hut.cs.drumbeat.ifc.convert.step2ifc;

import fi.hut.cs.drumbeat.ifc.common.IfcException;

public class IfcParserException extends IfcException {

	private static final long serialVersionUID = 1L;

	public IfcParserException() {
	}

	public IfcParserException(String arg0) {
		super(arg0);
	}

	public IfcParserException(Throwable arg0) {
		super(arg0);
	}

	public IfcParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IfcParserException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
