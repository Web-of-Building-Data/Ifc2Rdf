package fi.hut.cs.drumbeat.ifc.data.model;

import java.io.Serializable;
import java.util.*;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;


public class IfcUniqueKeyValue implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private SortedMap<IfcAttributeInfo, IfcValue> valueMap;
	
	public IfcUniqueKeyValue() {
		valueMap = new TreeMap<>();
	}
	
	public SortedMap<IfcAttributeInfo, IfcValue> getValueMap() {
		return valueMap;
	}
	
	public void addValue(IfcAttributeInfo attributeInfo, IfcValue value) {
		valueMap.put(attributeInfo, value);
	}
	
	public int size() {
		return valueMap.size();
	}
	
	public boolean isEmpty() {
		return valueMap.isEmpty();
	}

}
