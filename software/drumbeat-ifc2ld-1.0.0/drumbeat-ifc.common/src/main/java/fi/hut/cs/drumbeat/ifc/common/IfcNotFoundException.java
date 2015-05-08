package fi.hut.cs.drumbeat.ifc.common;

import fi.hut.cs.drumbeat.ifc.common.IfcException;

public class IfcNotFoundException extends IfcException {

	private static final long serialVersionUID = 1L;
	
	public IfcNotFoundException() {
	}

	public IfcNotFoundException(String arg0) {
		super(arg0);
	}

	public IfcNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public IfcNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IfcNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}	

}
