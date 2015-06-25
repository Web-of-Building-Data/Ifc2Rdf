package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.Properties;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;


/**
 * Processor that sets (and encodes) entities names based on any one-to-one incoming links from another named entity.
 * 
 * Sample syntax:
 * 
 *		<processor name="SetNameByOneToOneLinks" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameByOneToOneLinks</class>
 *			<params>
 *				<param name="entityNamePattern" value="$Entity.Name$-->$Property.Name$" />
 *				<param name="encoderType" value="MD5" />
 *			</params>
 *		</processor>
 * 
 * 
 * @author vuhoan1
 *
 */
public class SetNameByOneToOneLinks extends IfcGroundingProcessor {
	
	public static final String FORMAT_VARIABLE_ENTITY_NAME = Matcher.quoteReplacement("$Entity.Name$");
	public static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	
	private String entityNamePattern;

	public SetNameByOneToOneLinks(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {		
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_ENTITY_NAME, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY_NAME, Matcher.quoteReplacement("%2$s"));
	}

	@Override
	boolean process(IfcEntity source) throws IfcAnalyserException {
		
		if (source.isLiteralValueContainer()) {
			return false;
		}
		
		boolean result = false;

		for (IfcLink link : source.getOutgoingLinks()) {			
			
			// check if direct link cardinality is single
			IfcLinkInfo linkInfo = link.getLinkInfo();
			if (linkInfo.isFunctional() && linkInfo.isInverseFunctional()) {
				
				if (!linkInfo.isSortedList()) {
					
					String sourceRawName = source.getRawName();
					String linkName = linkInfo.getName();
					String destinationRawName = String.format(entityNamePattern, sourceRawName, linkName);
	
					assert link.getDestinations().size() == 1 : "Expected: link.getDestinations().size() == 1";
					IfcEntity destination = link.getSingleDestination();
					if (!destination.hasName()) {
						trySetEntityName(destination, destinationRawName);
						
						addEffectedEntityInfoForDebugging(destination);
						
						result = true;
					}
					
				} else {					
					
					int index = 0;
					for (IfcEntityBase destination : link.getDestinations()) {
						if (destination instanceof IfcEntity) {
							IfcEntity destinationEntity = (IfcEntity)destination; 
							if (!destinationEntity.hasName()) {
								String sourceRawName = source.getRawName();
								String linkName = linkInfo.getName();
								String destinationRawName = String.format(entityNamePattern, sourceRawName, linkName + index);
								trySetEntityName(destinationEntity, destinationRawName);
								
								addEffectedEntityInfoForDebugging(destinationEntity);
								
								result = true;
							}
							++index;
						}
					}					
					
				}
				
			}
			
		}

		return result;

	}
	
	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.GroundedEntity;
	}

	@Override
	boolean checkNameDuplication() {
		return true;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}
	
}
