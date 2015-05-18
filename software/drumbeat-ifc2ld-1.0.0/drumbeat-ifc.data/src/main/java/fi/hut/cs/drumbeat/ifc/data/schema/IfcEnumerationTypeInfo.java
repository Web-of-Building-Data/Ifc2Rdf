package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;


public class IfcEnumerationTypeInfo extends IfcNonEntityTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private List<String> values = new ArrayList<String>();
	
	public IfcEnumerationTypeInfo(IfcSchema schema, String name, List<String> values) {
		super(schema, name);
		this.values = values;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	@Override
	public boolean isCollectionType() {
		return false;
	}

//	@Override
//	public boolean isEntityOrSelectType() {
//		return false;
//	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return IfcTypeEnum.STRING
	 */
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return EnumSet.of(IfcTypeEnum.ENUM);
	}	
	
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return IfcVocabulary.ExpressFormat.ENUMERATION;
	}

	@Override
	public boolean isLiteralContainerType() {
		return true;
	}

}
