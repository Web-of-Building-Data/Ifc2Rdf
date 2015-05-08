package fi.hut.cs.drumbeat.ifc.processing.grounding;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;

public class IfcNameConflictException extends IfcAnalyserException {

	private static final long serialVersionUID = 1L;

	public IfcNameConflictException() {
	}

	public IfcNameConflictException(String arg0) {
		super(arg0);
	}

	public IfcNameConflictException(Throwable arg0) {
		super(arg0);
	}

	public IfcNameConflictException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IfcNameConflictException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
