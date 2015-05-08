package fi.hut.cs.drumbeat.common.config.document;

import org.w3c.dom.Element;


public class ClassConfigurationSection extends ConfigurationSection {
	
	private String className;

	public ClassConfigurationSection(Element parentElement, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, null, true, isMandatory);
	}

	public String getClassName() {
		return className;
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_CLASS;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		className = element.getTextContent();
		return true;
	}

}
