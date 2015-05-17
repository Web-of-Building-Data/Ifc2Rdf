package fi.hut.cs.drumbeat.ifc.data.schema;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;

public enum IfcCollectionKindEnum {
	
	List,
	Array,
	Set,
	Bag;
	
	public boolean isSorted() {
		return this == List || this == Array; 
	}
	
	public boolean allowsDuplicatedItems() {
		return this != Set;
	}
	
	public static IfcCollectionKindEnum parse(String typeHeader) {
		typeHeader = typeHeader.toUpperCase();
		
		switch (typeHeader) {
		case IfcVocabulary.ExpressFormat.LIST:
			return List;
		case IfcVocabulary.ExpressFormat.ARRAY:
			return Array;
		case IfcVocabulary.ExpressFormat.SET:
			return Set;
		case IfcVocabulary.ExpressFormat.BAG:
			return Bag;
		default:
			return null;
		}
		
	}
	
}
