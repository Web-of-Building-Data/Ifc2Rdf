package fi.hut.cs.drumbeat.ifc.data.schema;

import java.io.Serializable;
import java.util.*;

public class IfcUniqueKeyInfo implements Comparable<IfcUniqueKeyInfo>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SortedMap<String, IfcAttributeInfo> attributeInfoMap;
	
	public IfcUniqueKeyInfo() {
		attributeInfoMap = new TreeMap<>();
	}

	public SortedMap<String, IfcAttributeInfo> getAttributeInfoMap() {
		return attributeInfoMap;
	}
	
	public Collection<IfcAttributeInfo> getAttributeInfos() {
		return attributeInfoMap.values();
	}
	
	public void addAttributeInfo(IfcAttributeInfo attributeInfo) {
		attributeInfoMap.put(attributeInfo.getName(), attributeInfo);
	}
	
	public IfcAttributeInfo getAttributeInfo(String attributeName) {
		return attributeInfoMap.get(attributeName);
	}
	
	public IfcAttributeInfo getFirstAttributeInfo() {
		return attributeInfoMap.get(attributeInfoMap.firstKey());
	}

	public int size() {
		return attributeInfoMap.size();
	}

	@Override
	public int compareTo(IfcUniqueKeyInfo o) {
		int result = Integer.compare(attributeInfoMap.size(), o.attributeInfoMap.size());
		
		if (result == 0) {
			result = attributeInfoMap.firstKey().compareTo(o.attributeInfoMap.firstKey());
			assert(result != 0) : "Expected: attributeInfoMap.firstKey().compareTo(o.attributeInfoMap.firstKey()) != 0";
		}
		
		return result;
	}
	
}
