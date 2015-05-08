package fi.hut.cs.drumbeat.common.config.document;

import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Element;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.config.ConfigurationPool;


public class ConverterPoolConfigurationSection extends ConfigurationSection {
	
	//////////////////////////////////
	// Static Members
	//////////////////////////////////

	private static SortedMap<String, ConverterPoolConfigurationSection> globalInstanceMap = new TreeMap<>();
	
	public static ConverterPoolConfigurationSection getInstance(String converterTypeName) throws ConfigurationParserException {
		ConverterPoolConfigurationSection instance = globalInstanceMap.get(converterTypeName); 
		if (instance == null) {
			instance = new ConverterPoolConfigurationSection(
					ConfigurationDocument.getInstance().getDocument().getDocumentElement(),
					converterTypeName,
					true);
			globalInstanceMap.put(converterTypeName, instance);
		}
		return instance;
	}
	
	//////////////////////////////////
	// Non-Static Members
	//////////////////////////////////

	private ConfigurationPool<ConfigurationItemEx> converterConfigurationPool;

	public ConverterPoolConfigurationSection(Element parentElement, String converterTypeName, boolean isMandatory)
			throws ConfigurationParserException {
		super(parentElement, converterTypeName, true, isMandatory);
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_CONVERTER_POOL;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		String converterTypeName = element.getAttribute(ConfigurationDocument.ATTRIBUTE_TYPE);	
		if (filter != null && !filter.equals(converterTypeName)) {
			return false;
		}
		
		ConverterConfigurationSection childConfigurationSection = new ConverterConfigurationSection(element, null, true);
		if (childConfigurationSection.getInitialized() > 0) {
			converterConfigurationPool = new ConfigurationPool<ConfigurationItemEx>();
			converterConfigurationPool.addAll(childConfigurationSection.getConfigurations());
			return true;
		}
		
		return false;
	}
	
	public ConfigurationPool<ConfigurationItemEx> getConfigurationPool() {
		return converterConfigurationPool;
	}

}
