package net.linkedbuildingdata.ifc.util.grounding.config;

import java.util.Map;

import net.linkedbuildingdata.common.config.ProcessorConfiguration;
import net.linkedbuildingdata.common.config.document.ComplexProcessorPoolConfigurationSection;
import net.linkedbuildingdata.common.config.document.ConfigurationDocument;
import net.linkedbuildingdata.common.config.document.ConfigurationParserException;

import org.w3c.dom.Element;


public class IfcGroundingProcessorPoolConfigurationSection extends ComplexProcessorPoolConfigurationSection {

	private static IfcGroundingProcessorPoolConfigurationSection instance;

	public IfcGroundingProcessorPoolConfigurationSection(Element parentElement, Map<String, ProcessorConfiguration> configurationMap) throws ConfigurationParserException {
		super(parentElement, IfcGroundingProcessorMapConfigurationSection.PROCESSOR_TYPE, configurationMap, true);
	}
	
	public static IfcGroundingProcessorPoolConfigurationSection getInstance() throws ConfigurationParserException {
		if (instance == null) {
			Map<String, ProcessorConfiguration> configurationMap = 
					IfcGroundingProcessorMapConfigurationSection.getInstance().getConfigurationMap();
			instance = new IfcGroundingProcessorPoolConfigurationSection(
					ConfigurationDocument.getInstance().getDocument().getDocumentElement(),
					configurationMap);
		}
		return instance;
	}	

}
