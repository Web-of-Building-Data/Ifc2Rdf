package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import fi.hut.cs.drumbeat.common.digest.EncoderTypeEnum;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;


abstract class IfcGroundingProcessor {
	
	protected static final Logger logger = Logger.getLogger(IfcGroundingProcessor.class);	
	
	public enum InputTypeEnum {
		UngroundedEntity,
		GroundedEntity,
		Any;
	}
	
	protected static final String PARAM_ENTITY_NAME_PATTERN = "entityNamePattern";
	protected static final String PARAM_ENCODER_TYPE = "encoderType";
	protected static final String PARAM_MIN_NUMBER_OF_INCOMING_LINKS = "minNumberOfIncomingLinks";
	
	private IfcGroundingMainProcessor mainProcessor;
	private String name; 
	private Properties properties;
	private EncoderTypeEnum encoderType;
	private Map<IfcEntityTypeInfo, Integer> entityCountersForDebugging = null;	

	public IfcGroundingProcessor(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		this.mainProcessor = mainProcessor;
		this.properties = properties;
		if (properties != null) {
			encoderType = Enum.valueOf(EncoderTypeEnum.class, properties.getProperty(PARAM_ENCODER_TYPE, EncoderTypeEnum.None.toString()));			
		} else {
			encoderType = EncoderTypeEnum.None;
		}
	}	
	
	abstract public InputTypeEnum getInputType();
	abstract void initialize() throws IfcAnalyserException;
	abstract boolean process(IfcEntity entity) throws IfcAnalyserException;	
	abstract boolean checkNameDuplication();
	abstract boolean allowNameDuplication();
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	protected void trySetEntityName(IfcEntity entity, String rawName) throws IfcNameConflictException {
		mainProcessor.trySetEntityName(entity, rawName, encoderType, checkNameDuplication(), allowNameDuplication());
	}

	public IfcGroundingMainProcessor getMainProcessor() {
		return mainProcessor;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void preCommit() throws IfcNameConflictException {		
	}	
	
	public void preProcess() {		
		if (logger.isDebugEnabled()) {
			entityCountersForDebugging = new TreeMap<>();
		}		
	}
	
	public void postProcess() {		
		if (entityCountersForDebugging != null) {
			for (Map.Entry<IfcEntityTypeInfo, Integer> counter : entityCountersForDebugging.entrySet()) {
				logger.debug(String.format("\t\tNumber of effected entities: %2$d of %1$s", counter.getKey(), counter.getValue()));
			}
		}
	}
	
	protected void addEffectedEntityInfoForDebugging(IfcEntity entity) {
		if (entityCountersForDebugging != null) {
			IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();
			Integer counter = entityCountersForDebugging.get(entityTypeInfo);
			counter = counter != null ? counter + 1 : 1;
			entityCountersForDebugging.put(entityTypeInfo, counter);					
		}		
		
	}
	
}
