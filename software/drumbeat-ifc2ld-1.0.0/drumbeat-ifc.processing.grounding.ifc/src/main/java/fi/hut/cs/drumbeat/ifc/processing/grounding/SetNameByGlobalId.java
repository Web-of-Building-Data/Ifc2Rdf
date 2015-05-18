package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.Properties;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.common.guid.GuidCompressor;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcGuidValue;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralAttribute;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;


/**
 * Processor that sets entity name by its global IDs (if any).
 * 
 *  Sample syntax:
 *  
 *		<processor name="SetNameByGlobalId" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.SetNameByGlobalId</class>
 *			<params>
 *				<param name="entityNamePattern" value="GUID_$Entity.GlobalId$" />
 *				<param name="encoderType" value="None" />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class SetNameByGlobalId extends IfcGroundingProcessor {
	
	public static final String FORMAT_VARIABLE_ENTITY_STANDARD_GLOBAL_ID = Matcher.quoteReplacement("$Entity.StandardGlobalId$");
	public static final String FORMAT_VARIABLE_ENTITY_SHORT_GLOBAL_ID = Matcher.quoteReplacement("$Entity.ShortGlobalId$");
	
	private static IfcAttributeInfo globalIdAttributeInfo;
	
	private String entityNamePattern;

	private IfcEntityTypeInfo ifcRootEntityTypeInfo;

	public SetNameByGlobalId(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);		
	}

	@Override
	void initialize() throws IfcAnalyserException {		
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_ENTITY_SHORT_GLOBAL_ID, Matcher.quoteReplacement("%1$s"))
				.replaceAll(FORMAT_VARIABLE_ENTITY_STANDARD_GLOBAL_ID, Matcher.quoteReplacement("%2$s"));
		
		try {
			IfcSchema schema = getMainProcessor().getAnalyzer().getSchema();
			ifcRootEntityTypeInfo = schema.getEntityTypeInfo(IfcVocabulary.TypeNames.IFC_ROOT);
			
			globalIdAttributeInfo = ifcRootEntityTypeInfo.getAttributeInfo(IfcVocabulary.AttributeNames.GLOBAL_ID);
		} catch (IfcNotFoundException e) {
		}
		assert(globalIdAttributeInfo != null);
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		if (entity.isInstanceOf(ifcRootEntityTypeInfo)) {			
			IfcLiteralAttribute guidAttribute = entity.getLiteralAttributes().selectFirst(globalIdAttributeInfo);
			IfcLiteralValue guidValue = (IfcLiteralValue)guidAttribute.getValue();
			
			String shortGuid = (String)guidValue.getValue();
			String longGuid = GuidCompressor.uncompressGuidString(shortGuid);
			
			String rawName = String.format(entityNamePattern, shortGuid, longGuid);
//			entity.setGlobalId(rawName);
			trySetEntityName(entity, rawName);
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
		return false;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}

}
