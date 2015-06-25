package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.*;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralAttribute;
import fi.hut.cs.drumbeat.ifc.data.model.IfcShortEntity;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;


/**
 * Processor that sets (and encodes) names for blank entities that have more than a specified number of incoming links.
 * Naming is based on its underlying tree-like structure.
 * 
 * Sample syntax:
 * 
 *		<processor name="SetNameForSharedBlankNodesWithManyIncomingLinks" enabled="false">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameForSharedBlankNodesWithManyIncomingLinks</class>
 *			<params>
 *				<param name="propertyPattern" value="$Property.Name$:'$Property.Value$';" />
 *				<param name="entityNamePattern" value="$Entity.Type${$propertyPattern$}" />
 *				<param name="threshold" value="3" />
 *				<param name="encoderType" value="MD5" />
 *			</params>			
 *		</processor>
 *  
 * 
 * @author vuhoan1
 *
 */
public class SetNameForSharedBlankNodesWithManyIncomingLinks extends IfcGroundingProcessor {

	public static final String PARAM_PROPERTY_PATTERN = "propertyPattern";
	public static final String FORMAT_VARIABLE_TYPE = Matcher.quoteReplacement("$Entity.Type$");
	public static final String FORMAT_VARIABLE_PROPERTY = Matcher.quoteReplacement(String.format("$%s$", PARAM_PROPERTY_PATTERN));
	public static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	public static final String FORMAT_VARIABLE_PROPERTY_VALUE = Matcher.quoteReplacement("$Property.Value$");
	
	private String entityNamePattern;
	private String propertyPattern;	
	private int minNumberOfIncomingLinks;
	
	public SetNameForSharedBlankNodesWithManyIncomingLinks(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_TYPE, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY, Matcher.quoteReplacement("%2$s"));
		
		propertyPattern = getProperties().getProperty(PARAM_PROPERTY_PATTERN)
				.replaceAll(FORMAT_VARIABLE_PROPERTY_NAME, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY_VALUE, Matcher.quoteReplacement("%2$s"));		

		minNumberOfIncomingLinks = Integer.parseInt(getProperties().getProperty(PARAM_MIN_NUMBER_OF_INCOMING_LINKS));
	}
	
	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		assert(!entity.hasName());
		return internalProcess(entity, false);
	}
	
	private boolean internalProcess(IfcEntity entity, boolean isAttributeOfSharedBlankNode) throws IfcAnalyserException {
		if (entity.hasName()) {
			return true;
		}
		
		boolean isSharedBlankNode = entity.getIncomingLinks().size() >= minNumberOfIncomingLinks;
		entity.setSharedBlankNode(isSharedBlankNode);
		
		String rawName = entity.getRawName();
		
		if (rawName == null) {
		
			if (!entity.getTypeInfo().isLiteralContainerType() || (!isSharedBlankNode && !isAttributeOfSharedBlankNode)) {
				return false;
			}
			
			
			StringBuilder propertyStringBuilder = new StringBuilder();
			
			for (IfcLiteralAttribute literalAttribute : entity.getLiteralAttributes()) {
				propertyStringBuilder.append(String.format(propertyPattern, literalAttribute.getAttributeInfo().getName(), literalAttribute));
			}

			for (IfcLink link : entity.getOutgoingLinks()) {
				for (IfcEntityBase destination : link.getDestinations()) {
					if (destination instanceof IfcShortEntity) {
						propertyStringBuilder.append(String.format(propertyPattern, link.getAttributeInfo().getName(), destination.toString()));
					} else if (internalProcess((IfcEntity)destination, true)) {
						propertyStringBuilder.append(String.format(propertyPattern, link.getAttributeInfo().getName(), ((IfcEntity)destination).getRawName()));						
					} else {
						throw new IfcAnalyserException(String.format("Error grounding a literal-container entity: %s", destination));
					}
				}
			}

			IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();			
			rawName = String.format(entityNamePattern, entityTypeInfo.getName(), propertyStringBuilder);
			
			if (isSharedBlankNode) {
				trySetEntityName(entity, rawName);			
				addEffectedEntityInfoForDebugging(entity);
			} else {
				entity.setRawName(rawName);
			}
			
		}
		
		return true;
	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	boolean checkNameDuplication() {
		return true;
	}

	@Override
	boolean allowNameDuplication() {
		return true;
	}
	
}
