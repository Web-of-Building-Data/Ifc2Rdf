package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;


public class IfcSelectTypeInfo extends IfcNonEntityTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private List<String> selectTypeInfoNames;
	private List<IfcTypeInfo> selectTypeInfos;
	private EnumSet<IfcTypeEnum> valueTypes;
	private Boolean isLiteralContainerType;	
	
	public IfcSelectTypeInfo(IfcSchema schema, String typeName, List<String> selectTypeInfoNames) {
		super(schema, typeName);
		this.selectTypeInfoNames = selectTypeInfoNames;
	}
	
	
	/**
	 * @return the selectTypeInfoNames
	 */
	public List<String> getSelectTypeInfoNames() {
		return selectTypeInfoNames;
	}

	/**
	 * @return the selectTypeInfos
	 */
	public List<IfcTypeInfo> getSelectTypeInfos() {
		if (selectTypeInfos == null) {
			selectTypeInfos = new ArrayList<IfcTypeInfo>();
			for (String selectTypeInfoName : selectTypeInfoNames) {
				try {
					selectTypeInfos.add(getSchema().getTypeInfo(selectTypeInfoName));
				} catch (IfcNotFoundException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}			
		}
		return selectTypeInfos;
	}
	

	@Override
	public boolean isCollectionType() {
		return false;
	}
	

//	@Override
//	public boolean isEntityOrSelectType() {
//		return true;
//	}


	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return The value type of the first select type
	 * @throws IfcValueTypeConflictException 
	 */
	public EnumSet<IfcTypeEnum> getValueTypes() {
		if (valueTypes == null) {
			assert !getSelectTypeInfos().isEmpty() : "!getSelectTypeInfos().isEmpty()";
			valueTypes = EnumSet.noneOf(IfcTypeEnum.class);
			
			for (IfcTypeInfo selectTypeInfo : getSelectTypeInfos()) {
				valueTypes.addAll(selectTypeInfo.getValueTypes());
			}
			
			assert !valueTypes.isEmpty() : "!valueTypes.isEmpty()";
		}
		return valueTypes;
	}
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return StringUtils.collectionToString(selectTypeInfoNames, IfcVocabulary.ExpressFormat.SELECT + "(", ")", typeNameFormat, null);
	}


	@Override
	public boolean isLiteralContainerType() {
		if (isLiteralContainerType == null) {
			isLiteralContainerType = true;
			for (IfcTypeInfo typeInfo : getSelectTypeInfos()) {
				if (!typeInfo.isLiteralContainerType()) {
					isLiteralContainerType = false;
					break;
				}
			}
		}
		return isLiteralContainerType.booleanValue();
	}

}
