package fi.hut.cs.drumbeat.common;

/**
 * Super class for all exceptions in the DRUMBEAT software
 * 
 * @author Nam Vu
 *
 */
public class DrumbeatException extends Exception {

	private static final long serialVersionUID = 1L;

	public DrumbeatException() {
	}

	public DrumbeatException(String arg0) {
		super(arg0);
	}

	public DrumbeatException(Throwable arg0) {
		super(arg0);
	}

	public DrumbeatException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DrumbeatException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
