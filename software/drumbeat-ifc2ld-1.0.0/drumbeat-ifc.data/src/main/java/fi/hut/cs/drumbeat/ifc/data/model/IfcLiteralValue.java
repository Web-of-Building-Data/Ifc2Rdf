package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcSelectTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;

public class IfcLiteralValue extends IfcSingleValue {
	
	private static final long serialVersionUID = 1L;

	private final IfcTypeInfo typeInfo;
	private final IfcTypeEnum type;
	private Object value;
	
	public IfcLiteralValue(Object value, IfcTypeInfo typeInfo, IfcTypeEnum type) {
		assert(typeInfo != null);
		assert(!(typeInfo instanceof IfcSelectTypeInfo));
		assert(!(value instanceof IfcLiteralValue));
		assert(!value.toString().equals("T"));
		
		this.value = value;
		this.typeInfo = typeInfo;
		this.type = type;
	}
	
	public IfcTypeInfo getType() {
		return typeInfo;
	}
	
	@Override
	public String toString() {
		return value != null ? value.toString() : "null";
	}

	@Override
	public Boolean isLiteralType() {
		return Boolean.TRUE;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public IfcTypeEnum getValueType() {
		return type;
	}

//	@Override
//	public RdfNodeTypeEnum getRdfNodeType() {
//		return RdfNodeTypeEnum.Literal;
//	}
//
//	@Override
//	public RdfUri toRdfUri() {
//		return Ifc2RdfConverter.getDefaultConverter().convertLiteralToRdfUri(this);
//	}
//
//	@Override
//	public List<IRdfLink> getRdfLinks() {
//		return null;
//	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof IfcLiteralValue) {
			return getValue().equals(((IfcLiteralValue) other).getValue())
					&& getValueType().equals(((IfcLiteralValue) other).getValueType());
		}
		return false;
	}
}
