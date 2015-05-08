package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;

public class IfcLiteralValue extends IfcSingleValue {
	
	private static final long serialVersionUID = 1L;

	private final Object value;
	private final IfcTypeEnum type;
	
	public IfcLiteralValue(Object value, IfcTypeEnum type) {
		this.value = value;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Boolean isLiteralType() {
		return Boolean.TRUE;
	}
	
	public Object getValue() {
		return value;
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
