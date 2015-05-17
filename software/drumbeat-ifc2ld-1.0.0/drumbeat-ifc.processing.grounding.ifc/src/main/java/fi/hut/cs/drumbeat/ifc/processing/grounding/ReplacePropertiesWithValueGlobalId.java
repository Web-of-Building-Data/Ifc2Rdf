package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.List;
import java.util.Properties;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.guid.GuidCompressor;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralAttribute;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;


/**
 * Replace outgoing links by their inverse ones.
 * 
 *  Sample syntax:
 *  
 *		<processor name="ReplaceByInverseLinks" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.ReplaceByInverseLinks</class>
 *			<params>
 *				<param name="inverseLinkNames" value="type1.link1, type2.link2, ..." />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class ReplacePropertiesWithValueGlobalId extends IfcGroundingProcessor {

	private static final String IFCPROPERTY_TYPE = "IfcPropertySingleValue";
	private static final String IFCPROPERTY_NAME_ATTRIBUTE = "name";
	private static final String IFCPROPERTY_NOMINALVALUE_ATTRIBUTE = "nominalValue";
	
	private IfcEntityTypeInfo entityTypeInfo;
	private IfcAttributeInfo nameAttributeInfo;
	private IfcAttributeInfo valueAttributeInfo;	
	
	public ReplacePropertiesWithValueGlobalId(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	void initialize() throws IfcAnalyserException {

		IfcSchema schema = getMainProcessor().getAnalyzer().getModel().getSchema();		
		
		try {
			entityTypeInfo = schema.getEntityTypeInfo(IFCPROPERTY_TYPE);
			
			final List<IfcAttributeInfo> inheritedAttributeInfos = entityTypeInfo.getInheritedAttributeInfos();
			
			logger.debug("Type: " + entityTypeInfo.getName());

			for (IfcAttributeInfo attributeInfo : inheritedAttributeInfos) {
				String attributeName = attributeInfo.getName();
				
				logger.debug(((attributeInfo instanceof IfcLinkInfo) ?
						"   Link: " : "   Attribute: ") + attributeName);
				if (attributeName.equals(IFCPROPERTY_NAME_ATTRIBUTE)) {
					nameAttributeInfo = attributeInfo;
				} else if (attributeName.equals(IFCPROPERTY_NOMINALVALUE_ATTRIBUTE)) {
					valueAttributeInfo = attributeInfo;
				}
			}
			
			if (nameAttributeInfo == null) {
				throw new IfcNotFoundException(IFCPROPERTY_NAME_ATTRIBUTE);
			}
			
			if (valueAttributeInfo == null) {
				throw new IfcNotFoundException(IFCPROPERTY_NOMINALVALUE_ATTRIBUTE);
			}

		} catch (IfcNotFoundException e) {
			throw new IfcAnalyserException("Couldn't find property type or attribute: " + e.getMessage());
		}
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		
		if (entity.isInstanceOf(entityTypeInfo)) {
			logger.debug("Testing object " + entity);			
			
			IfcLiteralAttribute nameAttribute = entity.getLiteralAttributes().getFirst();
			if (!nameAttribute.getAttributeInfo().equals(nameAttributeInfo)) {
				throw new IfcAnalyserException("Expected attribute: " + nameAttributeInfo.getName() +
						"\tActual attribute: " + nameAttribute.getAttributeInfo().getName());
			}
			
			if (nameAttribute.getValue().toString().toUpperCase().endsWith("GUID")) {
				logger.debug("   name: " + nameAttribute.getValue().toString());
				
				if (!entity.getOutgoingLinks().isEmpty()) {
					IfcLink oldLink = entity.getOutgoingLinks().getFirst();
					if (!oldLink.getAttributeInfo().equals(valueAttributeInfo)) {
						throw new IfcAnalyserException("Expected link: " + valueAttributeInfo.getName());
					}		
					
					IfcLiteralValue value = (IfcLiteralValue)oldLink.getValue();
					String oldGuid = (String)value.getValue();
					String newGuid = GuidCompressor.uncompressGuidString(oldGuid);
					
					value.setValue(newGuid);
					
//					IfcShortEntity newValue = new IfcShortEntity(oldValue.getTypeInfo(),
//							new IfcLiteralValue(newGuid, IfcTypeEnum.STRING));
//					
//					IfcLink newLink = new IfcLink(oldLink.getLinkInfo(), oldLink.getIndex(), oldLink.getSource(), newValue);
//					
//					entity.getOutgoingLinks().set(0, newLink);
					
					logger.debug("Replaced");
				}
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

}
