package net.linkedbuildingdata.ifc.util.grounding.config;

import net.linkedbuildingdata.common.config.document.ConfigurationDocument;
import net.linkedbuildingdata.common.config.document.ConfigurationParserException;
import net.linkedbuildingdata.common.config.document.ProcessorMapConfigurationSection;

import org.w3c.dom.Element;


public class IfcGroundingProcessorMapConfigurationSection extends ProcessorMapConfigurationSection  {
	
	public static final String PROCESSOR_TYPE = "grounding";
	private static IfcGroundingProcessorMapConfigurationSection instance;

	public IfcGroundingProcessorMapConfigurationSection(Element parentElement) throws ConfigurationParserException {
		super(parentElement, PROCESSOR_TYPE, true);
	}
	
	public static IfcGroundingProcessorMapConfigurationSection getInstance() throws ConfigurationParserException {
		if (instance == null) {
			instance = new IfcGroundingProcessorMapConfigurationSection(
					ConfigurationDocument.getInstance().getDocument().getDocumentElement());
		}
		return instance;
	}
	
}
