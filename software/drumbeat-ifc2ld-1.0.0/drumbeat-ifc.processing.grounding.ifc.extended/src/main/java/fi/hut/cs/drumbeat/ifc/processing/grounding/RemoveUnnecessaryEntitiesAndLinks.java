package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.*;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Processor that removes all unnecessary entities and links.
 * 
 *  Sample syntax:
 *  
 *		<processor name="RemoveUnnecessaryEntitiesAndLinks" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.RemoveUnnecessaryEntitiesAndLinks</class>
 *			<params>
 *				<param name="entityTypeNames" value="type1, type2, ..." />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class RemoveUnnecessaryEntitiesAndLinks extends IfcGroundingProcessor {
	
	private static final String PARAM_ENTITY_TYPE_NAMES = "entityTypeNames";
	
	private List<IfcEntityTypeInfo> entityTypeInfos;
	private List<IfcEntity> entitiesToRemove;
	private int numberOfRemovedEntities;
	private int numberOfRemovedLinks;

	public RemoveUnnecessaryEntitiesAndLinks(IfcGroundingMainProcessor mainProcessor, Properties properties) {
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
				entitiesToRemove.add(entity);
				return true;
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
		entitiesToRemove = new ArrayList<>();
		super.preProcess();
	}

	@Override
	public void postProcess() {
		numberOfRemovedEntities = 0;
		numberOfRemovedLinks = 0;

		IfcModel model = getMainProcessor().getAnalyzer().getModel();
		
		for (IfcEntity entity : entitiesToRemove) {
			removeEntity(model, entity, true);
		}
		
		logger.debug(String.format("\tTotal number of removed entities: %,d", numberOfRemovedEntities));
		logger.debug(String.format("\tTotal number of removed links: %,d", numberOfRemovedLinks));
		
		super.postProcess();
		
	}
	
	private void removeEntity(IfcModel model, IfcEntity entity, boolean forceRemovingIncomingLinks) {
		
		if (forceRemovingIncomingLinks) {
			
			for (IfcLink incomingLink : entity.getIncomingLinks()) {
				
				IfcEntity source = incomingLink.getSource();
				IfcLinkInfo linkInfo = incomingLink.getLinkInfo();				
				source.getOutgoingLinks().remove(linkInfo, entity);
				
				++numberOfRemovedLinks;
				
			}

		} else {
			assert (entity.getIncomingLinks().isEmpty()) : "Expected: entity.getIncomingLinks().isEmpty()";
		}		
	
		for (IfcLink outgoingLink : entity.getOutgoingLinks()) {
			
			for (IfcEntityBase d : outgoingLink.getDestinations()) {				
				if (d instanceof IfcEntity) {
					IfcEntity destination = (IfcEntity)d;
					List<IfcLink> incomingLinks = destination.getIncomingLinks();
					for (int i = 0; i < incomingLinks.size(); ++i) {
						IfcLink incomingLink = incomingLinks.get(i);
						if (incomingLink.getSource().equals(entity) && incomingLink.getLinkInfo().equals(outgoingLink.getLinkInfo())) {
							incomingLinks.remove(i);
							++numberOfRemovedLinks;
							break;
						}
					}
					
					if (incomingLinks.isEmpty()) {
						removeEntity(model, destination, false);
					}
				}
			}
			
		}			
		
		model.removeEntity(entity);
		
		++numberOfRemovedEntities;
		
		addEffectedEntityInfoForDebugging(entity);		
	}

}
