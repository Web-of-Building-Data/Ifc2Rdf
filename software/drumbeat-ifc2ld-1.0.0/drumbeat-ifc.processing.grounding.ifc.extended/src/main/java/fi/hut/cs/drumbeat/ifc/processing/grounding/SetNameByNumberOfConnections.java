package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcNameConflictException;
import fi.hut.cs.drumbeat.ifc.data.model.IfcAttributeList;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;

/**
 * Processor that sets (and encodes) entity name based on number of specified links.
 * 
 * Sample syntax:
 * 
 *		<processor name="SetNameByNumberOfConnections" enabled="false">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameByNumberOfConnections</class>
 *			<params>
 *				<param name="entityNamePattern" value="$Entity.Type$_[$Property.Name$_$Property.Type$_$Property.SizeOrderIndex$]" />
 *				<param name="threshold" value="10" />
 *				<param name="encoderType" value="MD5" />
 *				<param name="checkOutgoingLinks" value="true" />
 *				<param name="checkIncomingLinks" value="false" />
 *			</params>			
 *		</processor>
 *
 * @deprecated Many blank entities may have the same number of links
 * 
 * @author vuhoan1
 *
 */
public class SetNameByNumberOfConnections extends IfcGroundingProcessor {
	
	public static final String FORMAT_VARIABLE_ENTITY_TYPE = Matcher.quoteReplacement("$Entity.Type$");
	public static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	public static final String FORMAT_VARIABLE_PROPERTY_TYPE = Matcher.quoteReplacement("$Property.Type$");
	public static final String FORMAT_VARIABLE_PROPERTY_SIZE_ORDER_INDEX = Matcher.quoteReplacement("$Property.SizeOrderIndex$");
	private static final String PARAM_CHECK_INCOMING_LINKS = "checkIncomingLinks";
	private static final String PARAM_CHECK_OUTGOING_LINKS = "checkOutgoingLinks";
	
	private String entityNamePattern;
	private int threshold;
	private boolean checkIncomingLinks;
	private boolean checkOutgoingLinks;
	
	private Map<IfcAttributeInfo, Map<IfcEntity, Integer>> temporaryNamedEntitiesByOutputLinks;
	private Map<IfcAttributeInfo, Map<IfcEntity, Integer>> temporaryNamedEntitiesByInputLinks;

	public SetNameByNumberOfConnections(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		Properties properties = getProperties(); 
		entityNamePattern = properties.getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_ENTITY_TYPE, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY_NAME, Matcher.quoteReplacement("%2$s"))				
				.replaceAll(FORMAT_VARIABLE_PROPERTY_TYPE, Matcher.quoteReplacement("%3$s"))				
				.replaceAll(FORMAT_VARIABLE_PROPERTY_SIZE_ORDER_INDEX, Matcher.quoteReplacement("%4$d"));
		
		threshold = Integer.parseInt(getProperties().getProperty(PARAM_MIN_NUMBER_OF_INCOMING_LINKS));
		
		checkIncomingLinks = Boolean.parseBoolean(properties.getProperty(PARAM_CHECK_INCOMING_LINKS));
		checkOutgoingLinks = Boolean.parseBoolean(properties.getProperty(PARAM_CHECK_OUTGOING_LINKS));
		
		temporaryNamedEntitiesByOutputLinks = new TreeMap<>();
		temporaryNamedEntitiesByInputLinks = new TreeMap<>();
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		if (!entity.hasName()) {
			
			if (checkOutgoingLinks && tryProcess(entity, true)) {
				return true;
			} else if (checkIncomingLinks) {
				return tryProcess(entity, false);
			}			
		}
		
		return false;
	}
	
	
	private boolean tryProcess(IfcEntity entity, boolean outgoingLinkType) {
		
		IfcAttributeList<IfcLink> attributeList = outgoingLinkType ? entity.getOutgoingLinks() : entity.getIncomingLinks();
		if (attributeList.size() > threshold) {
			
			Map<IfcAttributeInfo, IfcAttributeList<IfcLink>> attributeMap = attributeList.toMap();
			
			int maxSize = 0;
			IfcAttributeInfo maxAttributeInfo = null;
			
			for (Entry<IfcAttributeInfo, IfcAttributeList<IfcLink>> entry : attributeMap.entrySet()) {
				int size = entry.getValue().size();						
				if (size > threshold && size > maxSize) {							
					maxSize = size;
					maxAttributeInfo = entry.getKey();
				}
			}			
			
			if (maxAttributeInfo != null) {
				
				Map<IfcAttributeInfo, Map<IfcEntity, Integer>> temporaryNamedEntities = outgoingLinkType ?
						temporaryNamedEntitiesByOutputLinks : temporaryNamedEntitiesByInputLinks;
				
				Map<IfcEntity, Integer> map = temporaryNamedEntities.get(maxAttributeInfo);
				if (map == null) {
					map = new TreeMap<>();
					temporaryNamedEntities.put(maxAttributeInfo, map);
				}
				map.put(entity, new Integer(maxSize));							
				return true;
				
			}

		} 
		
		return false;
		
	}
	
	@Override
	public void preCommit() throws IfcNameConflictException {		
		setEntityNames(temporaryNamedEntitiesByOutputLinks, true);
		setEntityNames(temporaryNamedEntitiesByInputLinks, false);		
	}
	
	private void setEntityNames(Map<IfcAttributeInfo, Map<IfcEntity, Integer>> temporaryNamedEntities, boolean outgoingLinkType) throws IfcNameConflictException {
		for (Entry<IfcAttributeInfo, Map<IfcEntity, Integer>> entry : temporaryNamedEntities.entrySet()) {
			IfcAttributeInfo attributeInfo = entry.getKey();
			Map<IfcEntity, Integer> map = entry.getValue();
			
			for (int index = 1; !map.isEmpty(); ++index) {
				
				int maxSize = 0;
				IfcEntity entityWithMaxSize = null;
				
				for (Entry<IfcEntity, Integer> entry2 : map.entrySet()) {
					int size = entry2.getValue();
					if (maxSize < size) {
						maxSize = size;
						entityWithMaxSize = entry2.getKey();
					}
				}				
				
				String rawName = String.format(
						entityNamePattern,
						entityWithMaxSize.getTypeInfo().getName(),
						attributeInfo.getName(),
						outgoingLinkType ? "OUT" : "IN",
						index);
				
				logger.info(String.format("%s --> %s (%d links)", entityWithMaxSize, rawName, maxSize));
				
				
				trySetEntityName(entityWithMaxSize, rawName);
				map.remove(entityWithMaxSize);
			}
			
		}	
		
	}
	
	

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	boolean checkNameDuplication() {
		return false;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}

}
