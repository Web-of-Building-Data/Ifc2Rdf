package fi.hut.cs.drumbeat.common.config;

import java.util.ArrayList;
import java.util.List;

public class ComplexProcessorConfiguration extends ConfigurationItem {

	private List<ProcessorConfiguration> processorConfigurations;
	
	public ComplexProcessorConfiguration() {
		processorConfigurations = new ArrayList<>();
	}
	
	public List<ProcessorConfiguration> getProcessorConfigurations() {
		return processorConfigurations;
	}
	
	public void add(ProcessorConfiguration configuration) {
		processorConfigurations.add(configuration);
	}
	
	public int size() {
		return processorConfigurations.size();
	}

}
