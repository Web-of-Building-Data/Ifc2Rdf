package net.linkedbuildingdata.common.config.document;

import java.util.Map;

import net.linkedbuildingdata.common.config.ComplexProcessorConfigurationPool;
import net.linkedbuildingdata.common.config.ProcessorConfiguration;

import org.w3c.dom.Element;


public class ComplexProcessorPoolConfigurationSection extends ConfigurationSection {
	
	private Map<String, ProcessorConfiguration> processorConfigurationMap;
	private ComplexProcessorConfigurationPool complexProcessorConfigurationPool;

	public ComplexProcessorPoolConfigurationSection(Element parentElement, String processorTypeName, Map<String, ProcessorConfiguration> processorConfigurationMap, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, processorTypeName, false, isMandatory);
		this.processorConfigurationMap = processorConfigurationMap;
		parse(parentElement);
	}

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_COMPLEX_PROCESSOR_POOL;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {
		
		ComplexProcessorConfigurationSection complexProcessorConfigurationSection =
				new ComplexProcessorConfigurationSection(element, null, processorConfigurationMap, true);
		if (complexProcessorConfigurationSection.getInitialized() > 0) {
			complexProcessorConfigurationPool = new ComplexProcessorConfigurationPool();
			complexProcessorConfigurationPool.addAll(complexProcessorConfigurationSection.getComplexProcessorConfigurations());
			return true;
		} else {
			return false;
		}		
	}

	public ComplexProcessorConfigurationPool getComplexProcessorConfigurationPool() {
		return complexProcessorConfigurationPool;
	}

}
