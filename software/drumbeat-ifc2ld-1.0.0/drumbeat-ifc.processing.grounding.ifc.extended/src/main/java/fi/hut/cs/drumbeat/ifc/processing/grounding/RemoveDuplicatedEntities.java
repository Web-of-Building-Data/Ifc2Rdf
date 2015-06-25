package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.*;
import java.util.Map.Entry;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Processor that removes all duplicated entities.
 * 
 *  Sample syntax:
 *  
		<processor name="RemoveDuplicatedEntities" enabled="true">
			<class>fi.hut.cs.drumbeat.ifc.util.grounding.RemoveDuplicatedEntities</class>
			<params>
				<param name="entityTypeNames" value="type1, type2" />
			</params>
		</processor>
 *  
 * @author vuhoan1
 *
 */
public class RemoveDuplicatedEntities extends IfcGroundingProcessor {
	
	private static final String PARAM_ENTITY_TYPE_NAMES = "entityTypeNames";
	
	private List<IfcEntityTypeInfo> entityTypeInfos;
	private Map<IfcEntityTypeInfo, Map<IfcEntity, List<IfcEntity>>> mapOfDuplicatedEntities;
	private int totalNumberOfDuplicatedEntities;

	public RemoveDuplicatedEntities(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		IfcSchema schema = getMainProcessor().getAnalyzer().getSchema();
		entityTypeInfos = new ArrayList<>();
		String entityTypeNamesString = getProperties().getProperty(PARAM_ENTITY_TYPE_NAMES);
		if (entityTypeNamesString != null) {
			String[] tokens = entityTypeNamesString.split(StringUtils.COMMA);
			try {
				for (String token : tokens) {
					entityTypeInfos.add(schema.getEntityTypeInfo(token.trim()));
				}
			} catch (IfcNotFoundException e) {
				throw new IfcAnalyserException(e.getMessage(), e);
			}
		} else {
			throw new IfcAnalyserException(String.format("Parameter %s is undefined", PARAM_ENTITY_TYPE_NAMES));
		}
	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		for (IfcEntityTypeInfo entityTypeInfo : entityTypeInfos) {
			if (entity.isInstanceOf(entityTypeInfo)) {
				
				Map<IfcEntity, List<IfcEntity>> duplicatedEntityMap = mapOfDuplicatedEntities.get(entityTypeInfo);
				if (duplicatedEntityMap == null) {
					duplicatedEntityMap = new HashMap<>();
					mapOfDuplicatedEntities.put(entityTypeInfo, duplicatedEntityMap);
				}
				
				for (Entry<IfcEntity, List<IfcEntity>> entry : duplicatedEntityMap.entrySet()) {					
					if (entity.isIdenticalTo(entry.getKey())) {						
						List<IfcEntity> duplicatedEntityList =  entry.getValue();
						duplicatedEntityList.add(entity);
						return true;
					}
				}
				
				duplicatedEntityMap.put(entity, new ArrayList<IfcEntity>());
				return false;
			}
		}
		return false;
	}

	@Override
	boolean checkNameDuplication() {
		return false;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}
	
	public void preProcess() {
		mapOfDuplicatedEntities = new HashMap<>();
		super.preProcess();
	}

	@Override
	public void postProcess() {		
		
		totalNumberOfDuplicatedEntities = 0;

		for (IfcEntityTypeInfo entityTypeInfo : entityTypeInfos) {
			
			int numberOfUniqueEntities = 0;
			int numberOfDuplicatedEntities = 0;
			
			Map<IfcEntity, List<IfcEntity>> duplicatedEntityMap = mapOfDuplicatedEntities.get(entityTypeInfo);
			
			if (duplicatedEntityMap != null) {
				numberOfUniqueEntities = duplicatedEntityMap.size();
				for (Entry<IfcEntity, List<IfcEntity>> entry : duplicatedEntityMap.entrySet()) {
					List<IfcEntity> duplicatedEntitiList = entry.getValue();
					if (!duplicatedEntitiList.isEmpty()) {
						IfcEntity entity = entry.getKey();
						for (IfcEntity otherEntity : duplicatedEntitiList) {
							otherEntity.setSameAs(entity);
							++numberOfDuplicatedEntities;
						}
					}
				}				
			}			
			
			logger.debug(String.format("\tNumber of duplicated entities with type %s: %,d (%,d unique entities)",
					entityTypeInfo, numberOfDuplicatedEntities + numberOfUniqueEntities, numberOfUniqueEntities));
			
			totalNumberOfDuplicatedEntities += numberOfDuplicatedEntities;
		}
		

		logger.debug(String.format("\tTotal number of duplicated entities: %,d", totalNumberOfDuplicatedEntities));
		
		super.postProcess();
		
	}

}
