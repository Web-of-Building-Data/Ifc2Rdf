package fi.hut.cs.drumbeat.ifc.processing.grounding.config;

import java.util.Map;

import org.w3c.dom.Element;

import fi.hut.cs.drumbeat.common.config.ProcessorConfiguration;
import fi.hut.cs.drumbeat.common.config.document.ComplexProcessorPoolConfigurationSection;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;


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
