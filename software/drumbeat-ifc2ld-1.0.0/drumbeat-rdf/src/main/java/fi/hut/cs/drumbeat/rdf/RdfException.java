package fi.hut.cs.drumbeat.rdf;

import fi.hut.cs.drumbeat.common.DrumbeatException;

public class RdfException extends DrumbeatException {

	private static final long serialVersionUID = 1L;

	public RdfException() {
	}

	public RdfException(String arg0) {
		super(arg0);
	}

	public RdfException(Throwable arg0) {
		super(arg0);
	}

	public RdfException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RdfException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
