package fi.hut.cs.drumbeat.common.config;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ConfigurationPool<T extends ConfigurationItem> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	
	private T defaultT;
	
	public T getDefault() {
		if (defaultT == null) {
			if (!isEmpty()) {
				for (T configuration : this) {
					if (configuration.isDefault()) {
						defaultT = configuration;
						return defaultT;
					}
				}
				
				defaultT = get(0);
			}
		}
		return defaultT;
	}
	
	/**
	 * Gets configuration by a specified name 
	 * @param name
	 * @return configuration
	 * @throws InvalidParameterException if no configuration with the specified name was found
	 */
	public T getByName(String name) {
		for (T configuration : this) {
			if (name.equals(configuration.getName())) {
				return configuration;
			}
		}
		throw new InvalidParameterException(String.format("Cannot find complex processor by name '%s'", name));
	}

}
