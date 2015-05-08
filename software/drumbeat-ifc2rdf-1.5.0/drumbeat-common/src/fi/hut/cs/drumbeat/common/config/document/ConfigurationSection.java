package fi.hut.cs.drumbeat.common.config.document;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fi.hut.cs.drumbeat.common.string.StringUtils;

public abstract class ConfigurationSection {
	
	private String filter;
	private int initialized;
	private boolean isMandatory;
	
	public ConfigurationSection(Element parentElement, String filter, boolean autoParse, boolean isMandatory) throws ConfigurationParserException {
		this.isMandatory = isMandatory;
		this.filter = filter;
		if (autoParse) {
			parse(parentElement);
		}
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public int getInitialized() {
		return initialized;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	protected void parse(Element parentElement) throws ConfigurationParserException {
		NodeList processorElements = parentElement.getElementsByTagName(getTagName());
		
		initialized = 0;
		
		for (int i = 0; i < processorElements.getLength(); ++i) {
			Element element = (Element) processorElements.item(i);			
			if (initialize(element, filter)) {
				++initialized;
			}
		}
		
		if (initialized == 0 && isMandatory()) {
			throw new ConfigurationParserException(String.format("Cannot initialize any configuration section <%s>/<%s>[filter=]", parentElement.getTagName(), getTagName(), filter));
		}
	}
	
	abstract protected String getTagName();
	
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		String isEnabledString = element.getAttribute(ConfigurationDocument.ATTRIBUTE_ENABLED);
		if (!StringUtils.isEmptyOrNull(isEnabledString)) {
			return Boolean.parseBoolean(isEnabledString);
		} else {
			return true;
		}
	}
}
