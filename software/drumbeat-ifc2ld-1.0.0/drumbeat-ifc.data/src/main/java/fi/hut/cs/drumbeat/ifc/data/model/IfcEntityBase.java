package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;

public abstract class IfcEntityBase extends IfcSingleValue {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Boolean isLiteralType() {
		return Boolean.FALSE;
	}
	
	public abstract IfcTypeInfo getTypeInfo();	
	
	public abstract boolean equals(Object o);
	
	public abstract int hashCode();

	public abstract String toString();
	
	public abstract boolean isIdenticalTo(IfcEntityBase other);

}
