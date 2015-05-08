package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.ArrayList;
import java.util.List;
//import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import fi.hut.cs.drumbeat.common.collections.SortedList;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;


public class IfcAttributeList<T extends IfcAttribute> extends SortedList<T> {

	/**
	 * Needed for serialization
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Selects attribute by a specified {@link IfcAttributeInfo} and {@link IfcValue}.
	 * @param attributeInfo
	 * @param value
	 * @return
	 */
	public T select(IfcAttributeInfo attributeInfo, IfcValue value) {
		
		for (T t : this) {
			if (t.getAttributeInfo().equals(attributeInfo) && t.getValue().equals(value)) {
				return t;
			}
		}
		
		return null;
	}
	

	/**
	 * Selects attributes by a specified {@link IfcAttributeInfo}.
	 * @param attributeInfo
	 * @return
	 */
	public T selectFirst(IfcAttributeInfo attributeInfo) {
		
		for (T t : this) {
			if (t.getAttributeInfo().equals(attributeInfo)) {
				return t;
			}
		}
		
		return null;
	}	
	
	/**
	 * Selects attributes by a specified {@link IfcAttributeInfo}.
	 * @param attributeInfo
	 * @return
	 */
	public T selectFirstByName(String attributeName) {
		
		for (T t : this) {
			if (t.getAttributeInfo().getName().equalsIgnoreCase(attributeName)) {
				return t;
			}
		}
		
		return null;
	}	

	
	/**
	 * Selects attributes by a specified {@link IfcAttributeInfo}.
	 * @param attributeInfo
	 * @return
	 */
	public List<T> selectAllByName(String attributeName) {		
		return super.stream()
				.filter(t -> t.getAttributeInfo().getName().equalsIgnoreCase(attributeName))
				.collect(Collectors.toList());		
	}	

	/**
	 * Selects attributes by a specified {@link IfcAttributeInfo}.
	 * @param attributeInfo
	 * @return
	 */
	public List<T> selectAll(IfcAttributeInfo attributeInfo) {
		
//		if (!attributeInfo.isMultiple()) {
			
			//
			// single attribute
			//			
			T attribute = selectFirst(attributeInfo);
			if (attribute != null) {
				List<T> attributes = new ArrayList<T>(); 
				attributes.add(attribute);
				return attributes;				
			} else {
				return null;
			}
			
//		} else {
//			
//			//
//			// multiple attribute (set of attributes)
//			//			
//			for (ListIterator<T> it = this.listIterator(); it.hasNext();) {
//				
//				//
//				// find the first appropriate attribute
//				//
//				T attribute = it.next();
//				if (attribute.getAttributeInfo().equals(attributeInfo)) {
//					List<T> attributes = new ArrayList<T>(); 			
//					attributes.add(attribute);
//					
//					//
//					// find all other appropriate attributes (they should stand in a row)
//					//
//					while (it.hasNext()) {
//						attribute = it.next();
//						if (attribute.getAttributeInfo().equals(attributeInfo)) {
//							attributes.add(attribute);
//						} else {
//							break;
//						}
//					}
//					
//					return attributes;
//				}
//			}
//			
//			return null;
//		}		
	}	
	
	/**
	 * Selects attribute by a specified {@link IfcAttributeInfo} and {@link IfcValue}.
	 * @param attributeInfo
	 * @param value
	 * @return
	 */
	public T remove(IfcAttributeInfo attributeInfo, IfcValue value) {
		
		for (T t : this) {
			if (t.getAttributeInfo().equals(attributeInfo) && t.getValue().equals(value)) {
				remove(t);
				return t;
			}
		}
		
		return null;
	}
	
	
	public Map<IfcAttributeInfo, IfcAttributeList<T>> toMap() {
		Map<IfcAttributeInfo, IfcAttributeList<T>> map = new TreeMap<>();
		for (T t : this) {
			IfcAttributeInfo attributeInfo = t.getAttributeInfo();
			IfcAttributeList<T> list = map.get(attributeInfo);
			if (list == null) {
				list = new IfcAttributeList<>();
				map.put(attributeInfo, list);				
			}
			list.add(t);
		}
		return map;
	}

}
