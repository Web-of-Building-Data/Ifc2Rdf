package net.linkedbuildingdata.common;

/**
 * Super class for all exceptions in the DSG software
 * 
 * @author Nam Vu
 *
 */
public class LbdException extends Exception {

	private static final long serialVersionUID = 1L;

	public LbdException() {
	}

	public LbdException(String arg0) {
		super(arg0);
	}

	public LbdException(Throwable arg0) {
		super(arg0);
	}

	public LbdException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LbdException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
