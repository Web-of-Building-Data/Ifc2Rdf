package fi.hut.cs.drumbeat.common.config.document;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;

import fi.hut.cs.drumbeat.common.config.ProcessorConfiguration;
import fi.hut.cs.drumbeat.common.string.StringUtils;


public class ProcessorConfigurationSection extends ConfigurationSection {
	
	private Map<String, ProcessorConfiguration> configurationMap;

	public ProcessorConfigurationSection(Element parentElement, String filter, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, filter, true, isMandatory);
	}

	public Map<String, ProcessorConfiguration> getConfigurationMap() {
		return configurationMap;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		String name = element.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);		
		if (filter != null && !filter.equals(name)) {
			return false;
		}

		String isEnabledString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_ENABLED);
		if (!StringUtils.isEmptyOrNull(isEnabledString) && !Boolean.parseBoolean(isEnabledString)) {
			return false;
		}
		
		ProcessorConfiguration configuration = new ProcessorConfiguration(name);
		configuration.setEnabled(true);
		
		ClassConfigurationSection classConfigurationSection = new ClassConfigurationSection(element, true);
		if (classConfigurationSection.getInitialized() > 0) {
			configuration.setClassName(classConfigurationSection.getClassName());			
		}

		
		ParamsConfigurationSection paramsConfigurationSection = new ParamsConfigurationSection(element, false);
		if (paramsConfigurationSection.getInitialized() > 0) {
			configuration.setProperties(paramsConfigurationSection.getProperties());			
		}
		
		if (configurationMap == null) {
			configurationMap = new TreeMap<>();
		}
		
		configurationMap.put(name, configuration);
		
		return true;
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_PROCESSOR;
	}
	
}
