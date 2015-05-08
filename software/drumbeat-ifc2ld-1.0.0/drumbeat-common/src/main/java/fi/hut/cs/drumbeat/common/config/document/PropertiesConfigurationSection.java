package fi.hut.cs.drumbeat.common.config.document;

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PropertiesConfigurationSection extends ConfigurationSection {
	
	private Properties properties;
	
	public PropertiesConfigurationSection(Element parentElement, boolean isMandatory) throws ConfigurationParserException {
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

		NodeList propertyElements = element.getElementsByTagName(ConfigurationDocument.TAG_PARAM);

		for (int j = 0; j < propertyElements.getLength(); ++j) {

			Element propertyElement = (Element) propertyElements.item(j);

			String propertyName = propertyElement.getNodeName();
			String paramValue = propertyElement.getNodeValue();

//			if (propertyName == null || paramValue == null) {
//				throw new ConfigurationParserException(String.format("Invalid tag '%s/%s/%s'",
//						element.getTagName(), getTagName(), ConfigurationDocument.TAG_PARAM));
//			}

			properties.put(propertyName, paramValue);
		}
		
		return true;
	}
	

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_PROPERTIES;
	}

}
