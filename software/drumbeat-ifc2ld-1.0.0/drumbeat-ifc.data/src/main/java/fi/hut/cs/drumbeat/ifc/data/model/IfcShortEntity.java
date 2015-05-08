package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;

public class IfcShortEntity extends IfcEntityBase {
	
	private static final long serialVersionUID = 1L;

	private final IfcDefinedTypeInfo typeInfo;
	private final IfcLiteralValue value;
	
	public IfcShortEntity(IfcDefinedTypeInfo typeInfo, IfcLiteralValue value) {
		this.typeInfo = typeInfo;
		this.value = value;
	}

	public IfcDefinedTypeInfo getTypeInfo() {
		return typeInfo;
	}
	
	public IfcLiteralValue getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)", typeInfo, value.toString());		
	}

	@Override
	public boolean equals(Object other) {
		assert other instanceof IfcShortEntity;
		return this.typeInfo.equals(((IfcShortEntity)other).typeInfo) && this.value.equals(((IfcShortEntity)other).value); 
	}

	@Override
	public int hashCode() {
		return typeInfo.hashCode() ^ value.hashCode();
	}

	@Override
	public boolean isIdenticalTo(IfcEntityBase other) {
		return other instanceof IfcShortEntity && 
			typeInfo.equals(other.getTypeInfo()) &&
					value.equals(((IfcShortEntity)other).value);
		
	}

}
