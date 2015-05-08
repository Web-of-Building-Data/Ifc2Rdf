package fi.hut.cs.drumbeat.ifc.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;


public abstract class IfcAttribute implements Comparable<IfcAttribute>, Serializable { // IRdfLink {
	
	private static final long serialVersionUID = 1L;
	
	private final IfcAttributeInfo attributeInfo;
	private final int index;
	private final IfcValue value;
	
	public IfcAttribute(IfcAttributeInfo attributeInfo, int attributeIndex, IfcValue value) {
		this.attributeInfo = attributeInfo;
		this.index = attributeIndex;
		this.value = value;
	}

	public IfcAttributeInfo getAttributeInfo() {
		return attributeInfo;
	}

	public int getIndex() {
		return index;
	}
	
	public IfcValue getValue() {
		return value;
	}	

	@Override
	public int compareTo(IfcAttribute o) {
		if (this == o) {
			return 0;
		} else {
			return attributeInfo.compareTo(o.attributeInfo);
		}
	}
	
//	@Override
//	public IRdfPredicate getRdfPredicate() {
//		return attributeInfo;
//	}
//
//	@Override
//	public IRdfNode getRdfObject() {
//		return value;
//	}
	
	@SuppressWarnings("unchecked")
	public <T extends IfcSingleValue> List<T> getSingleValues() {
		if (value instanceof IfcSingleValue) {
			List<T> singleValues = new ArrayList<>(1);
			singleValues.add((T)value);
			return singleValues;
		} else {
			return ((IfcCollectionValue<T>)value).getSingleValues();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof IfcAttribute)
				&& this.getAttributeInfo().equals(((IfcAttribute)other).getAttributeInfo())
				&& this.getValue().equals(((IfcAttribute)other).getValue());
	}	

	@Override
	public String toString() {
		return value.toString();		
	}

	public abstract boolean isLiteralType();	
}
