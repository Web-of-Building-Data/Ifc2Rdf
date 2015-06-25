package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.common.collections.Pair;
import fi.hut.cs.drumbeat.common.string.RegexUtils;
import fi.hut.cs.drumbeat.common.string.StrBuilderWrapper;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralAttribute;
import fi.hut.cs.drumbeat.ifc.data.model.IfcShortEntity;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Processor that sets (and encodes) names for blank entities that have more than a specified number of incoming links.
 * Naming is based on its underlying tree-like structure.
 * 
 * Sample syntax:
 * 
 *		<processor name="SetNameForEntitiesContainingOtherNamedEntities" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameForEntitiesContainingOtherNamedEntities</class>
 *			<params>
 *				<param name="propertyPattern" value="$Property.Name$:'$Property.Value$';" />
 *				<param name="entityNamePattern" value="$Entity.Type${$propertyPattern$}" />
 *				<param name="entityTypesAndDepths" value="{* : 1} {IfcFace : *}" />
 *				<param name="encoderType" value="MD5" />
 *			</params>			
 *		</processor>
 *  
 * 
 * @author vuhoan1
 *
 */
public class SetNameForEntitiesContainingOtherNamedEntities extends IfcGroundingProcessor {

	public static final String PARAM_PROPERTY_PATTERN = "propertyPattern";
	public static final String PARAM_DEFAULT_MAX_DEPTH = "defaultMaxDepth";
	public static final String PARAM_ENTITY_TYPES_AND_DEPTHS = "entityTypesAndMaxDepths";
	public static final String FORMAT_VARIABLE_TYPE = Matcher.quoteReplacement("$Entity.Type$");
	public static final String FORMAT_VARIABLE_PROPERTY = Matcher.quoteReplacement(String.format("$%s$", PARAM_PROPERTY_PATTERN));
	public static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	public static final String FORMAT_VARIABLE_PROPERTY_VALUE = Matcher.quoteReplacement("$Property.Value$");
	private static final String ANY = StringUtils.STAR;
	
	private String entityNamePattern;
	private String propertyNamePattern;
	private List<Pair<IfcEntityTypeInfo, Integer>> entityTypesAndMaxDepthsList;
	private int defaultMaxDepth;
	
	public SetNameForEntitiesContainingOtherNamedEntities(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_TYPE, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY, Matcher.quoteReplacement("%2$s"));
		
		propertyNamePattern = getProperties().getProperty(PARAM_PROPERTY_PATTERN)
				.replaceAll(FORMAT_VARIABLE_PROPERTY_NAME, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY_VALUE, Matcher.quoteReplacement("%2$s"));
		
		String entityTypesAndMaxDepthsString = getProperties().getProperty(PARAM_ENTITY_TYPES_AND_DEPTHS);
		
		try {
			
			entityTypesAndMaxDepthsList = new ArrayList<>();
			
			IfcSchema schema = getMainProcessor().getAnalyzer().getModel().getSchema();
			
			StrBuilderWrapper strBuilderWrapper = new StrBuilderWrapper(entityTypesAndMaxDepthsString);
			
			String s;
			while ((s = strBuilderWrapper.trimLeft().getStringBetweenCurlyBrackets()) != null) {
				
				String[] tokens = RegexUtils.split2(s, RegexUtils.COLON);
				if (tokens.length != 2) {
					throw new InvalidParameterException("Expected ':'");				
				}
				
				String entityTypeName = tokens[0].trim();
				IfcEntityTypeInfo entityTypeInfo = schema.getEntityTypeInfo(entityTypeName);
				
				String depthString = tokens[1].trim();
				int depth = depthString.equals(ANY) ? Integer.MAX_VALUE : Integer.parseInt(depthString);
				
				entityTypesAndMaxDepthsList.add(new Pair<IfcEntityTypeInfo, Integer>(entityTypeInfo, depth));				
			}
			
			if (!strBuilderWrapper.isEmpty()) {
				throw new InvalidParameterException(String.format("Invalid string: '%s'", strBuilderWrapper.toString()));				
			}
		} catch (Exception e) {
			throw new IfcAnalyserException(String.format("Error processing additional unique keys: %s", e.getMessage()), e);
		}

		String defaultMaxDepthString = getProperties().getProperty(PARAM_DEFAULT_MAX_DEPTH);
		defaultMaxDepth = defaultMaxDepthString.equals(ANY) ? Integer.MAX_VALUE : Integer.parseInt(defaultMaxDepthString);
	}
	
	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		assert(!entity.hasName());
		
		Integer depth = defaultMaxDepth;
		IfcEntityTypeInfo entityTypeInfo = null;
		
		for (Pair<IfcEntityTypeInfo, Integer> pair : entityTypesAndMaxDepthsList) {
			if (entity.isInstanceOf(pair.getKey())) {
				entityTypeInfo = pair.getKey();
				depth = pair.getValue();
				break;
			}
		}
		
		String rawName = internalProcess(entity, depth); 
		
		if (rawName != null) {
			trySetEntityName(entity, rawName);			
			addEffectedEntityInfoForDebugging(entity);
			entity.appendDebugMessage(String.format("Grounded by %s: Type=%s, Depth=%d", this.getClass().getSimpleName(), entityTypeInfo, depth));
			return true;
		}
		
		return false;
	}
	
	private String internalProcess(IfcEntity entity, int maxDepth) throws IfcAnalyserException {
		if (entity.hasName()) {
			return entity.getName();
		}
		
		if (maxDepth == 0) {
			return null;
		}
		
		StringBuilder propertyStringBuilder = new StringBuilder();
		String childName;
		
		for (IfcLiteralAttribute literalAttribute : entity.getLiteralAttributes()) {
			propertyStringBuilder.append(String.format(propertyNamePattern, literalAttribute.getAttributeInfo().getName(), literalAttribute));
		}

		for (IfcLink link : entity.getOutgoingLinks()) {
			for (IfcEntityBase destination : link.getDestinations()) {
				if (destination instanceof IfcShortEntity) {
					propertyStringBuilder.append(String.format(propertyNamePattern, link.getAttributeInfo().getName(), destination.toString()));
				} else if ((childName = internalProcess((IfcEntity)destination, maxDepth - 1)) != null) {
					propertyStringBuilder.append(String.format(propertyNamePattern, link.getAttributeInfo().getName(), childName));						
				} else {
					return null;
				}
			}
		}

		IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();			
		return String.format(entityNamePattern, entityTypeInfo.getName(), propertyStringBuilder);
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
