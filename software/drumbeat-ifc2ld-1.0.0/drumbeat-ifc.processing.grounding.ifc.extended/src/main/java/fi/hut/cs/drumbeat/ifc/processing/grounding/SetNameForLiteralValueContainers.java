package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.Properties;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;


/**
 * Processor that sets (and encodes) entity names for all literal-value-containers. Naming is based on their literal values.
 * 
 * 		<processor name="SetNameForLiteralValueContainers" enabled="true">
			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameForLiteralValueContainers</class>
			<params>
				<param name="propertyPattern" value="$Property.Name$:'$Property.Value$';" />
				<param name="entityNamePattern" value="$Entity.Type${$propertyPattern$}" />
				<param name="encoderType" value="MD5" />
			</params>
		</processor>

 * 
 * @author vuhoan1
 *
 */
class SetNameForLiteralValueContainers extends IfcGroundingProcessor {
	
	public static final String PARAM_PROPERTY_PATTERN = "propertyPattern";
	public static final String FORMAT_VARIABLE_TYPE = Matcher.quoteReplacement("$Entity.Type$");
	public static final String FORMAT_VARIABLE_PROPERTY = Matcher.quoteReplacement(String.format("$%s$", PARAM_PROPERTY_PATTERN));
	public static final String FORMAT_VARIABLE_PROPERTY_NAME = Matcher.quoteReplacement("$Property.Name$");
	public static final String FORMAT_VARIABLE_PROPERTY_VALUE = Matcher.quoteReplacement("$Property.Value$");
	
	private String entityNamePattern;
	private String propertyPattern;	
	
	public SetNameForLiteralValueContainers(IfcGroundingMainProcessor processor,
			Properties properties) {
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
	}

	@Override
	public boolean process(IfcEntity entity) throws IfcAnalyserException {
		
		return internalProcess(entity, false);
		
	}
	
	private boolean internalProcess(IfcEntity entity, boolean isAttributeOfSharedBlankNode) throws IfcAnalyserException {
		
		if (entity.hasName()) {
			return true;
		}
		
		if (entity.isLiteralValueContainer()) {
			
			StringBuilder propertyStringBuilder = new StringBuilder();
			
			for (IfcLiteralAttribute literalAttribute : entity.getLiteralAttributes()) {
				propertyStringBuilder.append(String.format(propertyPattern, literalAttribute.getAttributeInfo().getName(), literalAttribute));
			}

			IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();			
			String rawName = String.format(entityNamePattern, entityTypeInfo.getName(), propertyStringBuilder);			
			trySetEntityName(entity, rawName);
			
			addEffectedEntityInfoForDebugging(entity);
			
			return true;
			
		}
		
		return false;		
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
