package fi.hut.cs.drumbeat.common.config.document;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.string.StringUtils;


public class ConverterConfigurationSection extends ConfigurationSection {

	public static final String CONVERTER_NAME_DEFAULT = null;

	private List<ConfigurationItemEx> configurations;

	public ConverterConfigurationSection(Element parentElement, String processorTypeName, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, processorTypeName, false, isMandatory);
		configurations = new ArrayList<>(); 
		parse(parentElement);
	}

	/**
	 * @return the configurations
	 */
	public List<ConfigurationItemEx> getConfigurations() {
		return configurations;
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_CONVERTER;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		if (!super.initialize(element, filter)) {
			return false;
		}
		
		String name = element.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);		
		if (StringUtils.isEmptyOrNull(name)) {
			throw new ConfigurationParserException(String.format("Invalid attribute '%s[%s]'",
					ConfigurationDocument.TAG_CONVERTER, ConfigurationDocument.ATTRIBUTE_NAME));
		}

		if (filter != null && !filter.equals(name)) {
			return false;
		}

		ConfigurationItemEx converterConfigration = new ConfigurationItemEx();		
		converterConfigration.setName(name);

		String isEnabledString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_ENABLED);
		if (!StringUtils.isEmptyOrNull(isEnabledString)) {
			boolean isEnabled = Boolean.parseBoolean(isEnabledString);
			converterConfigration.setEnabled(isEnabled);
		}

		String isDefaultString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_DEFAULT);
		if (!StringUtils.isEmptyOrNull(isDefaultString)) {
			converterConfigration.setDefault(Boolean.parseBoolean(isDefaultString));
		}
		
		converterConfigration.setProperties(new ParamsConfigurationSection(element, false).getProperties());
		
		configurations.add(converterConfigration);
		
		return true;
	}

}
