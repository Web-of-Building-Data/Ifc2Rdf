package net.linkedbuildingdata.common.config.document;

import net.linkedbuildingdata.common.LbdException;

public class ConfigurationParserException extends LbdException {

	private static final long serialVersionUID = 1L;

	public ConfigurationParserException() {
	}

	public ConfigurationParserException(String arg0) {
		super(arg0);
	}

	public ConfigurationParserException(Throwable arg0) {
		super(arg0);
	}

	public ConfigurationParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigurationParserException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
