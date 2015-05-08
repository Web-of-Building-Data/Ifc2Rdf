package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;

public class IfcLiteralAttribute extends IfcAttribute {
	
	private static final long serialVersionUID = 1L;

	public IfcLiteralAttribute(IfcAttributeInfo attributeInfo, int attributeIndex, IfcValue value) {
		super(attributeInfo, attributeIndex, value);
	}

	@Override
	public boolean isLiteralType() {
		return true;
	}	

}
