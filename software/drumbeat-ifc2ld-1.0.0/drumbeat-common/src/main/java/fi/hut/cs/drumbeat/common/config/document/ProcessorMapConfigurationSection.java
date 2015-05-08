package fi.hut.cs.drumbeat.common.config.document;

import java.util.Map;

import org.w3c.dom.Element;

import fi.hut.cs.drumbeat.common.config.ProcessorConfiguration;


public class ProcessorMapConfigurationSection extends ConfigurationSection {

	private Map<String, ProcessorConfiguration> configurationMap;
	
	public ProcessorMapConfigurationSection(Element parentElement, String processorTypeName, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, processorTypeName, true, isMandatory);		
	}
	
	public Map<String, ProcessorConfiguration> getConfigurationMap() {
		return configurationMap;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		String processorTypeName = element.getAttribute(ConfigurationDocument.ATTRIBUTE_TYPE);	
		if (filter != null && !filter.equals(processorTypeName)) {
			return false;
		}

		ProcessorConfigurationSection childConfigurationSection = new ProcessorConfigurationSection(element, null, true);
		if (childConfigurationSection.getInitialized() > 0) {
			configurationMap = childConfigurationSection.getConfigurationMap();
			return true;
		}
		return false;
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_PROCESSOR_MAP;
	}
	

}
