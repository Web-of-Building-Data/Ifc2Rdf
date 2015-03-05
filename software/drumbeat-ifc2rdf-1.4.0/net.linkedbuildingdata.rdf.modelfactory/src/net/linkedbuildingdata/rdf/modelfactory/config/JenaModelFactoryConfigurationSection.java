package net.linkedbuildingdata.rdf.modelfactory.config;

import java.util.ArrayList;
import java.util.List;

import net.linkedbuildingdata.common.config.ConfigurationItemEx;
import net.linkedbuildingdata.common.config.document.ClassConfigurationSection;
import net.linkedbuildingdata.common.config.document.ConfigurationDocument;
import net.linkedbuildingdata.common.config.document.ConfigurationParserException;
import net.linkedbuildingdata.common.config.document.ConfigurationSection;
import net.linkedbuildingdata.common.config.document.ParamsConfigurationSection;
import net.linkedbuildingdata.common.string.StringUtils;

import org.w3c.dom.Element;


public class JenaModelFactoryConfigurationSection extends ConfigurationSection {

	public static final String TAG_NAME = "jenaModel";
	public static final String TYPE_NAME_DEFAULT = null;

	private List<ConfigurationItemEx> configurations;	

	public JenaModelFactoryConfigurationSection(Element parentElement, String processorTypeName, boolean isMandatory) throws ConfigurationParserException {
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
		return TAG_NAME;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {		
		String name = element.getAttribute(ConfigurationDocument.ATTRIBUTE_NAME);		
		if (StringUtils.isEmptyOrNull(name)) {
			throw new ConfigurationParserException(String.format("Invalid attribute '%s[%s]'",
					TAG_NAME, ConfigurationDocument.ATTRIBUTE_NAME));
		}

//		String type = element.getAttribute(ConfigurationDocument.ATTRIBUTE_TYPE);		
//		if (StringUtils.isEmptyOrNull(type)) {
//			throw new ConfigurationParserException(String.format("Invalid attribute '%s[%s]'",
//					TAG_NAME, ConfigurationDocument.ATTRIBUTE_TYPE));
//		}
//
//		if (filter != null && !filter.equals(type)) {
//			return false;
//		}

		ConfigurationItemEx configuration = new ConfigurationItemEx();
		configuration.setName(name);
		
		ClassConfigurationSection classConfigurationSection = new ClassConfigurationSection(element, true);
		if (classConfigurationSection.getInitialized() > 0) {
			configuration.setType(classConfigurationSection.getClassName());			
		}		

		String isDefaultString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_DEFAULT);
		if (!StringUtils.isEmptyOrNull(isDefaultString)) {
			configuration.setDefault(Boolean.parseBoolean(isDefaultString));
		}
		
		configuration.setProperties(new ParamsConfigurationSection(element, false).getProperties());
		
		configurations.add(configuration);
		
		return true;
	}

}
