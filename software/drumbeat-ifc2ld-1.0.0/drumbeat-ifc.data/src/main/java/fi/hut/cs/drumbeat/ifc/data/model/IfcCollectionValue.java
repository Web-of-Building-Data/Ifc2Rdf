package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

import fi.hut.cs.drumbeat.common.collections.IteratorComparer;
import fi.hut.cs.drumbeat.common.string.StringUtils;

//import fi.aalto.cse.dsg.rdf.IRdfLink;
//import fi.aalto.cse.dsg.rdf.IRdfList;
//import fi.aalto.cse.dsg.rdf.IRdfNode;
//import fi.aalto.cse.dsg.rdf.RdfBlankNode;
//import fi.aalto.cse.dsg.rdf.RdfNodeTypeEnum;
//import fi.aalto.cse.dsg.rdf.RdfPredicate;
//import fi.aalto.cse.dsg.rdf.RdfProperty;
//import fi.aalto.cse.dsg.rdf.RdfUri;
//import fi.aalto.cse.dsg.rdf.RdfUriNode;
//import fi.aalto.cse.dsg.rdf.RdfVocabulary;

public abstract class IfcCollectionValue<V extends IfcSingleValue> extends IfcValue { // implements IRdfList {
	
	private static final long serialVersionUID = 1L;
	
	private final List<V> values;
//	private List<IRdfLink> links;
	
	public IfcCollectionValue() {
		values = new ArrayList<>();
	}

	public IfcCollectionValue(List<V> values) {
		this();
		values.addAll(values);
	}
	
	public void add(V value) {
		values.add(value);
	}

	public List<V> getSingleValues() {
		return values;
	}
	
//	@Override
//	public RdfNodeTypeEnum getRdfNodeType() {
//		return RdfNodeTypeEnum.List;
//	}
	
//	@Override
//	public RdfUri toRdfUri() {
//		return !values.isEmpty() ? RdfVocabulary.RDF_LIST : RdfVocabulary.RDF_NIL;
//	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<IRdfLink> getRdfLinks() {
//		if (links == null) {
//			links = new ArrayList<>();
//			
//			RdfPredicate rdfFirstPredicate = new RdfPredicate(RdfVocabulary.RDF_FIRST);
//			RdfPredicate rdfRestPredicate = new RdfPredicate(RdfVocabulary.RDF_REST);
//
//			List<IRdfLink> currentLinks = links;			
//			Iterator<? extends IfcSingleValue> iterator = getSingleValues().iterator();			
//			
//			while (iterator.hasNext()) {
//				currentLinks.add(new RdfProperty(rdfFirstPredicate, iterator.next()));
//				if (iterator.hasNext()) {
//					IRdfNode nextNode = new RdfBlankNode();
//					currentLinks.add(new RdfProperty(rdfRestPredicate, nextNode));
//					currentLinks = (List<IRdfLink>) nextNode.getRdfLinks();
//				}
//			}
//			
//			currentLinks.add(new RdfProperty(
//					rdfRestPredicate, new RdfUriNode(RdfVocabulary.RDF_NIL)));			
//		}
//		
//		return links;		
//	}
	
	@Override
	public String toString() {
		return StringUtils.collectionToString(
				values,
				StringUtils.OPENING_ROUND_BRACKET,
				StringUtils.CLOSING_ROUND_BRACKET,
				null,
				StringUtils.COMMA);
	}	
	
	
	@Override
	public boolean isNullOrAny() {
		return false;
	}
	
//	@Override
//	public List<? extends IRdfNode> getRdfListNodes() {
//		return values;
//	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof IfcCollectionValue<?>) {
			return IteratorComparer.areEqual(this.getSingleValues(), ((IfcCollectionValue<?>) other).getSingleValues());
		} else {
			return false;
		}
	}
	

}
