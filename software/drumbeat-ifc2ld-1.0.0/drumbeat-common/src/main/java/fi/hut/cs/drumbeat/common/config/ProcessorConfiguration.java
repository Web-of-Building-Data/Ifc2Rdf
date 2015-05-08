package fi.hut.cs.drumbeat.common.config;

public class ProcessorConfiguration extends ConfigurationItemEx {
	
	private String name;
	private String className;
	private boolean isRepeatable;
	
	public ProcessorConfiguration(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the isRepeatable
	 */
	public boolean isRepeatable() {
		return isRepeatable;
	}

	/**
	 * @param isRepeatable the isRepeatable to set
	 */
	public void setRepeatable(boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
