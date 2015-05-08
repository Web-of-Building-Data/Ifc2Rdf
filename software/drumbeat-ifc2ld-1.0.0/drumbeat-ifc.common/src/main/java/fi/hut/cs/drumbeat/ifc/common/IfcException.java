package fi.hut.cs.drumbeat.ifc.common;

/**
 * Super class for all IFC exceptions in this program
 * @author Nam Vu
 *
 */
import fi.hut.cs.drumbeat.common.DrumbeatException;

public class IfcException extends DrumbeatException {

	private static final long serialVersionUID = 1L;

	public IfcException() {
	}

	public IfcException(String arg0) {
		super(arg0);
	}

	public IfcException(Throwable arg0) {
		super(arg0);
	}

	public IfcException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IfcException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
