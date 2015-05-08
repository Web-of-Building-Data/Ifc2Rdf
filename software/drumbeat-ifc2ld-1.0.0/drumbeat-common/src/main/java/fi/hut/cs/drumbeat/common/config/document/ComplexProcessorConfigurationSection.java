package fi.hut.cs.drumbeat.common.config.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fi.hut.cs.drumbeat.common.config.ComplexProcessorConfiguration;
import fi.hut.cs.drumbeat.common.config.ProcessorConfiguration;
import fi.hut.cs.drumbeat.common.string.StringUtils;


public class ComplexProcessorConfigurationSection extends ConfigurationSection {
	
	private Map<String, ProcessorConfiguration> configurationMap;
	private List<ComplexProcessorConfiguration> complexProcessorConfigurations;

	public ComplexProcessorConfigurationSection(Element parentElement, String filter, Map<String, ProcessorConfiguration> configurationMap, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, filter, false, isMandatory);
		this.configurationMap = configurationMap;
		complexProcessorConfigurations = new ArrayList<>();
		parse(parentElement);
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_COMPLEX_PROCESSOR;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		
		String isEnabledString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_ENABLED);
		if (!StringUtils.isEmptyOrNull(isEnabledString) && !Boolean.parseBoolean(isEnabledString)) {
			return false;
		}
		
		ComplexProcessorConfiguration complexProcessorConfiguration = new ComplexProcessorConfiguration();
		String name = element.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);		
		complexProcessorConfiguration.setName(name);
		complexProcessorConfiguration.setEnabled(true);
		
		String isDefaultString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_DEFAULT);
		if (!StringUtils.isEmptyOrNull(isDefaultString)) {
			complexProcessorConfiguration.setDefault(Boolean.parseBoolean(isDefaultString));
		}

		NodeList addedElements = element.getElementsByTagName(ConfigurationDocument.TAG_ADD);
		for (int i = 0; i < addedElements.getLength(); ++i) {
			Element addedElement = (Element)addedElements.item(i);
			String configurationName = addedElement.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);
			
			ProcessorConfiguration processorConfiguration;			
			if (configurationName != null && (processorConfiguration = configurationMap.get(configurationName)) != null) {
				complexProcessorConfiguration.add(processorConfiguration);
			} else {
				throw new ConfigurationParserException(
						String.format("Processor '%s' is not found", configurationName));
			}			
		}
		
		complexProcessorConfigurations.add(complexProcessorConfiguration);			
		return true;
	}

	public List<ComplexProcessorConfiguration> getComplexProcessorConfigurations() {
		return complexProcessorConfigurations;
	}

}
