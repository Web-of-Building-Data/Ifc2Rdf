package fi.hut.cs.drumbeat.common.config.document;

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class ParamsConfigurationSection extends ConfigurationSection {
	
	private Properties properties;
	
	public ParamsConfigurationSection(Element parentElement, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, null, true, isMandatory);
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {

		properties = new Properties();

		NodeList processorParamElements = element.getElementsByTagName(ConfigurationDocument.TAG_PARAM);

		for (int j = 0; j < processorParamElements.getLength(); ++j) {

			Element processorParamElement = (Element) processorParamElements.item(j);

			String paramName = processorParamElement.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);
			String paramValue = processorParamElement.getAttribute(ConfigurationDocument.ATTRIBUTE_VALUE);

			if (paramName == null || paramValue == null) {
				throw new ConfigurationParserException(String.format("Invalid tag '%s/%s/%s'",
						element.getTagName(), ConfigurationDocument.TAG_PARAMS, ConfigurationDocument.TAG_PARAM));
			}

			properties.put(paramName, paramValue);
		}
		
		return true;
	}
	

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_PARAMS;
	}

}
