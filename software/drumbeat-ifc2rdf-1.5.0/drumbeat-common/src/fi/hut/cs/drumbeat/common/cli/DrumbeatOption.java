package fi.hut.cs.drumbeat.common.cli;

import org.apache.commons.cli.Option;

public class DrumbeatOption extends Option {
	
	private static final long serialVersionUID = 1L;
	
	private int index;

	public DrumbeatOption(int index, String opt, String description)
			throws IllegalArgumentException {
		super(opt, description);
		this.index = index;
	}

	public DrumbeatOption(int index, String opt, boolean hasArg, String description)
			throws IllegalArgumentException {
		super(opt, hasArg, description);
		this.index = index;
	}

	public DrumbeatOption(int index, String opt, String longOpt, boolean hasArg,
			String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

}
