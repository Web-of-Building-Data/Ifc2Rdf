package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.common.string.RegexUtils;
import fi.hut.cs.drumbeat.common.string.StrBuilderWrapper;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcHelper;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcUniqueKeyInfo;

/**
 * Processor that sets (and encodes) entity name based on specified unique keys.
 * 
 *  Sample syntax:
 *  
 *		<processor name="SetNameByUniqueKeyValues" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameByUniqueKeyValues</class>
 *			<params>
 *				<param name="propertyPattern" value="$Property.Name$:'$Property.Value$';" />
 *				<param name="entityNamePattern" value="$Entity.Type${$propertyPattern$}" />
 *				<param name="encoderType" value="MD5" />
 *				<param name="additionalUniqueKeys"
 *					value="{IfcOrganization: Id, Name}
 *							{IfcOwnerHistory: CreationDate}							
 *							{IfcPresentationLayerAssignment: Name}" />							
 *			</params>
 *		</processor>
 *  
 * 
 * @author vuhoan1
 *
 */

class SetNameByUniqueKeyValues extends IfcGroundingProcessor {
	
	private static final String PARAM_PROPERTY_PATTERN = "propertyPattern";
	private static final String PARAM_ADDITIONAL_UNIQUE_KEYS = "additionalUniqueKeys";
	
	private static final String FORMAT_VARIABLE_TYPE = Matcher.quoteReplacement("$Entity.Type$");
	private static final String FORMAT_VARIABLE_PROPERTY = Matcher.quoteReplacement(String.format("$%s$", PARAM_PROPERTY_PATTERN));
	private static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	private static final String FORMAT_VARIABLE_PROPERTY_VALUE = Matcher.quoteReplacement("$Property.Value$");
	
	private String entityNamePattern;
	private String propertyPattern;
	private Map<IfcEntityTypeInfo, IfcUniqueKeyInfo> additionalUniqueKeyInfos;
	
	public SetNameByUniqueKeyValues(IfcGroundingMainProcessor processor,
			Properties properties) throws IfcNotFoundException {
		super(processor, properties);
	}	
	
	@Override
	void initialize() throws IfcAnalyserException {		
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_TYPE, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY, Matcher.quoteReplacement("%2$s"));
		
		propertyPattern = getProperties().getProperty(PARAM_PROPERTY_PATTERN)
				.replaceAll(FORMAT_VARIABLE_PROPERTY_NAME, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_PROPERTY_VALUE, Matcher.quoteReplacement("%2$s"));
		
		String additionalUniqueKeysString = getProperties().getProperty(PARAM_ADDITIONAL_UNIQUE_KEYS);
		if (additionalUniqueKeysString != null) {
			
			try {
			
				additionalUniqueKeyInfos = new TreeMap<>();
				
				IfcSchema schema = getMainProcessor().getAnalyzer().getModel().getSchema();
				
				StrBuilderWrapper strBuilderWrapper = new StrBuilderWrapper(additionalUniqueKeysString);
				
				String s;
				while ((s = strBuilderWrapper.trimLeft().getStringBetweenCurlyBrackets()) != null) {
					
					String[] tokens = RegexUtils.split2(s, RegexUtils.COLON);
					if (tokens.length != 2) {
						throw new InvalidParameterException("Expected ':'");				
					}
					
					String entityTypeName = tokens[0].trim();
					IfcEntityTypeInfo entityTypeInfo = schema.getEntityTypeInfo(entityTypeName);
					
					tokens = RegexUtils.splitAll(tokens[1], RegexUtils.COMMA);
					IfcUniqueKeyInfo uniqueKeyInfo = new IfcUniqueKeyInfo();
					for (int i = 0; i < tokens.length; ++i) {
						String attributeName = IfcHelper.getFormattedAttributeName(tokens[i].trim());
						IfcAttributeInfo attributeInfo = entityTypeInfo.getAttributeInfo(attributeName);
						uniqueKeyInfo.addAttributeInfo(attributeInfo);
					}
					
					additionalUniqueKeyInfos.put(entityTypeInfo, uniqueKeyInfo);				
				}
				
				if (!strBuilderWrapper.isEmpty()) {
					throw new InvalidParameterException(String.format("Invalid string: '%s'", strBuilderWrapper.toString()));				
				}
			} catch (Exception e) {
				throw new IfcAnalyserException(String.format("Error processing additional unique keys: %s", e.getMessage()), e);
			}			
		}
	}

	@Override
	public boolean process(IfcEntity entity) throws IfcAnalyserException {
		
		List<IfcUniqueKeyValue> uniqueKeyValues = entity.getUniqueKeyValues();
		
		IfcUniqueKeyValue uniqueKeyValue = null;
		
		if (uniqueKeyValues != null && !uniqueKeyValues.isEmpty()) {
			
			uniqueKeyValue = uniqueKeyValues.get(0);
			
		} else {
			
			for (Entry<IfcEntityTypeInfo, IfcUniqueKeyInfo> entry : additionalUniqueKeyInfos.entrySet()) {				
				if (entity.isInstanceOf(entry.getKey())) {
					IfcUniqueKeyInfo uniqueKeyInfo = entry.getValue();
					uniqueKeyValue = entity.getUniqueKeyValue(uniqueKeyInfo);
					break;					
				}				
			}
			
			if (uniqueKeyValue == null || uniqueKeyValue.isEmpty()) {
				return false;
			}
			
		}
		
		String propertyString = StringUtils.EMPTY;
		
		for (Entry<IfcAttributeInfo, IfcValue> entry : uniqueKeyValue.getValueMap().entrySet()) {
			propertyString += String.format(propertyPattern, entry.getKey().getName(), entry.getValue());
		}
		
		String rawName = String.format(entityNamePattern, entity.getTypeInfo().getName(), propertyString);			
		trySetEntityName(entity, rawName);
		
		addEffectedEntityInfoForDebugging(entity);
		
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
		return false;
	}
	
}
