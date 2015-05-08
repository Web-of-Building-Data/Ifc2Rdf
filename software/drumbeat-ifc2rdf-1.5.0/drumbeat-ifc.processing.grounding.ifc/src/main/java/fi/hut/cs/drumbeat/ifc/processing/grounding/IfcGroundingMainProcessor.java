package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.lang.reflect.Constructor;
import java.util.*;

import org.apache.log4j.Logger;

import fi.hut.cs.drumbeat.common.config.ProcessorConfiguration;
import fi.hut.cs.drumbeat.common.digest.EncoderTypeEnum;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.IfcModelAnalyser;


public class IfcGroundingMainProcessor {
	
	private static final Logger logger = Logger.getLogger(IfcGroundingMainProcessor.class);	
	
	private IfcModelAnalyser analyser;
	private IfcModel model;
	private List<IfcEntity> namedEntities;
	private Map<String, IfcEntity> temporaryNamedEntities;
	private List<IfcEntity> recentlyNamedEntities;
	
	public IfcGroundingMainProcessor(IfcModelAnalyser analyser) {
		this.analyser = analyser;		
		model = analyser.getModel();
		namedEntities = analyser.getNamedEntities();
	}
	
	public IfcModelAnalyser getAnalyzer() {
		return analyser;
	}
	
	public void process(Collection<ProcessorConfiguration> groundingProcessorConfigurations) throws IfcAnalyserException {
		
		List<IfcGroundingProcessor> processors = new ArrayList<>();
		
		for (ProcessorConfiguration processorConfiguration : groundingProcessorConfigurations) {
			
			if (processorConfiguration.isEnabled()) {
			
				String processorClassName = processorConfiguration.getClassName();
				
				try {
					Class<? extends IfcGroundingProcessor> processorClass = Class.forName(processorClassName).asSubclass(IfcGroundingProcessor.class);
					Constructor<? extends IfcGroundingProcessor> constructor = processorClass.getConstructor(IfcGroundingMainProcessor.class, Properties.class);
					IfcGroundingProcessor processor = constructor.newInstance(this, processorConfiguration.getProperties());
					processor.setName(processorConfiguration.getName());
					processor.initialize();
					processors.add(processor);
				} catch (Exception e) {
					throw new IfcAnalyserException(String.format("Cannot initialize instance of class '%s'.", processorClassName), e);
				}
				
			}
			
		}	
		
		
		int numberOfAllEntities = model.getEntities().size();
		int numberOfNamedEntities = namedEntities.size();
		
		try {
			
			for (IfcGroundingProcessor processor : processors) {				
				
				reset();
				processor.preProcess();
				
				logger.debug(String.format("Running grounding processor '%s'", processor.getName()));
				if (processor.getInputType() == IfcGroundingProcessor.InputTypeEnum.Any) {
					for (IfcEntity entity : model.getEntities()) {
						processor.process(entity);
					}
					processor.preCommit();
					commit();
				} else if (processor.getInputType() == IfcGroundingProcessor.InputTypeEnum.UngroundedEntity) {					
					for (IfcEntity entity : model.getEntities()) {
						if (!entity.hasName()) {
							processor.process(entity);
						}
					}	
					processor.preCommit();
					commit();
				} else if (processor.getInputType() == IfcGroundingProcessor.InputTypeEnum.GroundedEntity) {
					for (IfcEntity entity : namedEntities) {
						processor.process(entity);
					}
					processor.preCommit();
					commit();
					
					int index = 0;
					
					while (index < recentlyNamedEntities.size()) {
					
						for (; index < recentlyNamedEntities.size(); ++index) {
							IfcEntity entity = recentlyNamedEntities.get(index);
							processor.process(entity);
						}
						
						processor.preCommit();
						commit();						
					}
				}
				
				processor.postProcess();

				int numberOfRecentlyNamedEntities = namedEntities.size() - numberOfNamedEntities;
				logger.debug(String.format("Result: %,d (%.2f%%) entities grounded",
						numberOfRecentlyNamedEntities, 100.0f * numberOfRecentlyNamedEntities / numberOfAllEntities));				

				numberOfNamedEntities = namedEntities.size();
			}
			
			logger.debug(String.format("Totally: %,d (%.2f%%) entities grounded",
					numberOfNamedEntities, 100.0f * numberOfNamedEntities / numberOfAllEntities));				

		} catch (Exception e) {
			rollback();
			logger.error(e);
			throw e;
		}

	}
	
	public void trySetEntityName(
			IfcEntity entity,
			String rawName,
			EncoderTypeEnum encoderType,
			boolean checkNameDuplication,
			boolean allowNameDuplication) throws IfcNameConflictException {
		if (entity.hasName()) {
			String message = String.format("Entity '%s' already has name '%s' (new name is '%s')", entity, entity.getRawName(), rawName);
			throw new IfcNameConflictException(message);
		}
		
		String name = EncoderTypeEnum.encode(encoderType, rawName);
		
		entity.setName(name);
		if (!rawName.equals(name)) {
			entity.setRawName(rawName);
		}

		if (temporaryNamedEntities == null) {
			temporaryNamedEntities = new TreeMap<>();
		} else if (checkNameDuplication && temporaryNamedEntities.containsKey(name)) {
			IfcEntity otherEntity = temporaryNamedEntities.get(name);
			if (allowNameDuplication && entity.isIdenticalTo(otherEntity)) {
				entity.setSameAs(otherEntity);
			} else {
				String message = String.format(
						"Entity '%s' (raw name is '%s') already has the same name '%s'\r\n(unnamed entity is '%s', raw name '%s')",
						otherEntity,
						otherEntity.getRawName(),
						name,
						entity,
						entity.getRawName());
				throw new IfcNameConflictException(message);
			}
			return;
		} 
		
		temporaryNamedEntities.put(name, entity);
	}
	
	private void reset() {
		recentlyNamedEntities = new ArrayList<>();
	}
	
	
	private void commit() {		
	
		if (temporaryNamedEntities != null) {			
			namedEntities.addAll(temporaryNamedEntities.values());
			recentlyNamedEntities.addAll(temporaryNamedEntities.values());
			temporaryNamedEntities.clear();
		}

	}
	
	private void rollback() {
		
		if (temporaryNamedEntities != null) {			
			for (IfcEntity entity : temporaryNamedEntities.values()) {
				entity.setRawName(null);
				entity.setName(null);
			}
			temporaryNamedEntities.clear();
		}
		
	}

}
