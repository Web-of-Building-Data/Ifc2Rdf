package fi.hut.cs.drumbeat.common.config.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;


public class ConfigurationDocument {
	
	public static final String TAG_ADD = "add";
	public static final String TAG_CLASS = "class";
	public static final String TAG_COMPLEX_PROCESSOR = "complexProcessor";
	public static final String TAG_COMPLEX_PROCESSOR_POOL = "complexProcessorPool";
	public static final String TAG_CONFIG = "config";
	public static final String TAG_CONVERTER = "converter";
	public static final String TAG_CONVERTER_POOL = "converterPool";
	public static final String TAG_PARAMS = "params";
	public static final String TAG_PARAM = "param";
	public static final String TAG_PROCESSOR = "processor";
	public static final String TAG_PROCESSOR_MAP = "processorMap";
	public static final String TAG_PROPERTIES = "properties";
	
	public static final String ATTRIBUTE_DEFAULT = "default";
	public static final String ATTRIBUTE_ENABLED = "enabled";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_TYPE = "type";	
	public static final String ATTRIBUTE_VALUE = "value";
	
	
	private static ConfigurationDocument defaultConfiguration;
	
	private Document document;
	
	public ConfigurationDocument(Document document) {
		this.document = document;
	}
	
	public static ConfigurationDocument getInstance() {
		if (defaultConfiguration != null) {
			return defaultConfiguration;
		} else {
			throw new NullPointerException("Undefined default configuration");
		}
	}
	
	public static void load(InputStream is) throws ConfigurationParserException {		
		try {
			DocumentBuilder documentBuilder;
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			defaultConfiguration = new ConfigurationDocument(document);
		} catch (Exception e) {
			throw new ConfigurationParserException(String.format("ConfigurationItem parser error: %s", e.getMessage()), e);
		}
	}

	public static void load(String configFilePath) throws ConfigurationParserException {
		try {
			load(new FileInputStream(configFilePath));
		} catch (FileNotFoundException e) {
			throw new ConfigurationParserException(String.format("ConfigurationItem parser error: %s", e.getMessage()), e);
		}
	}

	/**
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}
	
	public Properties getProperties() throws ConfigurationParserException {
		PropertiesConfigurationSection propertiesConfigurationSection = new PropertiesConfigurationSection(document.getDocumentElement(), false);
//		if (propertiesConfigurationSection != null) {
			return propertiesConfigurationSection.getProperties();
//		} else {
//			return new Properties();
//		}
	}

	
}
