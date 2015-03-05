package net.linkedbuildingdata.ifc.util.grounding;

import java.util.Properties;
import java.util.regex.Matcher;

import net.linkedbuildingdata.ifc.IfcNotFoundException;
import net.linkedbuildingdata.ifc.IfcVocabulary;
import net.linkedbuildingdata.ifc.common.guid.Guid;
import net.linkedbuildingdata.ifc.common.guid.GuidConverter;
import net.linkedbuildingdata.ifc.data.model.IfcEntity;
import net.linkedbuildingdata.ifc.data.model.IfcLiteralAttribute;
import net.linkedbuildingdata.ifc.data.model.IfcLiteralValue;
import net.linkedbuildingdata.ifc.data.schema.IfcAttributeInfo;
import net.linkedbuildingdata.ifc.data.schema.IfcEntityTypeInfo;
import net.linkedbuildingdata.ifc.data.schema.IfcSchema;
import net.linkedbuildingdata.ifc.util.IfcAnalyserException;


/**
 * Processor that sets entity name by its global IDs (if any).
 * 
 *  Sample syntax:
 *  
 *		<processor name="SetNameByGlobalId" enabled="true">
 *			<class>net.linkedbuildingdata.ifc.util.grounding.SetNameByGlobalId</class>
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
	
	public static final String FORMAT_VARIABLE_ENTITY_GLOBAL_ID = Matcher.quoteReplacement("$Entity.GlobalId$");
	
	private static IfcAttributeInfo globalIdAttributeInfo;
	
	private String entityNamePattern;

	private IfcEntityTypeInfo ifcRootEntityTypeInfo;

	public SetNameByGlobalId(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);		
	}

	@Override
	void initialize() throws IfcAnalyserException {		
		entityNamePattern = getProperties().getProperty(PARAM_ENTITY_NAME_PATTERN)
				.replaceAll(FORMAT_VARIABLE_ENTITY_GLOBAL_ID, Matcher.quoteReplacement("%1$s"));
		
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
			Guid guid = (Guid)guidValue.getValue();
			
			String rawName = String.format(entityNamePattern, GuidConverter.convertGuidToBase64String(guid));
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
