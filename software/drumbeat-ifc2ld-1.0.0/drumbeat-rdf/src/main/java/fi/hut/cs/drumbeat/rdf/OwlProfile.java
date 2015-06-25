package fi.hut.cs.drumbeat.rdf;

import java.util.EnumSet;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OwlProfile {
	
	public static final float OWL_VERSION_1_0 = 1.0f;
	public static final float OWL_VERSION_2_0 = 2.0f;
	
	public enum RdfTripleObjectTypeEnum {
		Single,
		List,
		Data,
		Object,
		ClassIdentifier,
		NonClassIdentifier,
		ZeroOrOne,
		ZeroOrOneOrMany;
		
		public static final EnumSet<RdfTripleObjectTypeEnum> ANY = EnumSet.allOf(RdfTripleObjectTypeEnum.class);
		public static final EnumSet<RdfTripleObjectTypeEnum> SingleObject = EnumSet.of(RdfTripleObjectTypeEnum.Single, RdfTripleObjectTypeEnum.Object);
		public static final EnumSet<RdfTripleObjectTypeEnum> SingleData = EnumSet.of(RdfTripleObjectTypeEnum.Single, RdfTripleObjectTypeEnum.Data);
		public static final EnumSet<RdfTripleObjectTypeEnum> DataList = EnumSet.of(RdfTripleObjectTypeEnum.List, RdfTripleObjectTypeEnum.Data);
		public static final EnumSet<RdfTripleObjectTypeEnum> ObjectList = EnumSet.of(RdfTripleObjectTypeEnum.List, RdfTripleObjectTypeEnum.Object);
	}	
	
	private OwlProfileEnum owlProfileId;
	private float owlVersion;
	
	public OwlProfile(OwlProfileEnum owlProfileId) {
		this.owlProfileId = owlProfileId;
		this.owlVersion = owlProfileId.toVersion();
	}

	/**
	 * @return the owlProfile
	 */
	public OwlProfileEnum getOwlProfileId() {
		return owlProfileId;
	}

	/**
	 * @return the owlVersion
	 */
	public float getOwlVersion() {
		return owlVersion;
	}
	
	public boolean supportsRdfProperty(Resource property, EnumSet<RdfTripleObjectTypeEnum> tripleObjectType) {
		return internalSupportsRdfProperty(property, tripleObjectType);
	}
	
	public boolean supportsRdfClass(Resource classResource) {
		return internalSupportsRdfProperty(classResource, null);
	}
	
	private boolean internalSupportsRdfProperty(Resource property, EnumSet<RdfTripleObjectTypeEnum> tripleObjectType) {
		
		if (owlProfileId == OwlProfileEnum.OWL1_Lite) {
			
			if (property.equals(OWL.complementOf) ||
					property.equals(OWL.disjointWith) ||				
					property.equals(OWL2.hasKey) ||
					property.equals(OWL.oneOf) ||
					property.equals(OWL.unionOf))
			{				
				//
				// OWL Lite forbids the use of owl) ||OneOf, owl) ||unionOf, owl) ||complementOf, owl) ||hasValue, owl) ||disjointWith, owl) ||DataRange  
				// See) || http) ||//www.w3.org/TR/owl-ref/#OWLLite
				//
				return false;
			}
			else if (property.equals(OWL.cardinality)) {
				//
				// OWL Lite includes the use of all three types of cardinality constraints,
				// but only when used with the values "0" or "1".
				// See) || http) ||//www.w3.org/TR/owl-ref/#CardinalityRestriction
				//		
				return !tripleObjectType.contains(RdfTripleObjectTypeEnum.ZeroOrOneOrMany);
			}
			else if (property.equals(RDFS.domain) ||
					property.equals(RDFS.range))
			{				
				//
				// In OWL Lite the value of rdfs) ||domain and rdfs) ||range must be a class identifier. 
				// See) || http) ||//www.w3.org/TR/owl-ref/#domain-def
				// See) || http) ||//www.w3.org/TR/owl-ref/#range-def
				//
				return !tripleObjectType.contains(RdfTripleObjectTypeEnum.NonClassIdentifier);
			}
			
		} else if (owlProfileId == OwlProfileEnum.OWL2_EL || owlProfileId == OwlProfileEnum.OWL2_QL) {

			if (property.equals(OWL2.allValuesFrom) ||		// ObjectAllValuesFrom, DataAllValuesFrom
				property.equals(OWL2.disjointUnionOf) ||	// DisjointUnion				
				property.equals(OWL.oneOf) ||				// ObjectOneOf, DataOneOf
				property.equals(OWL.unionOf) ||				// ObjectUnionOf, DataUnionOf
				property.equals(OWL.maxCardinality) ||		// ObjectMaxCardinality, DataMaxCardinality
				property.equals(OWL.minCardinality) ||		// ObjectMinCardinality, DataMinCardinality 
				property.equals(OWL.cardinality) || 			// ObjectExactCardinality, DataExactCardinality
				property.equals(OWL2.hasKey)) 			// ObjectExactCardinality, DataExactCardinality
			{				
				//
				// XXXAllValuesFrom, XXXCardinality, XXXUnionOf, DisjointUnion are not supported by OWL 2 EL
				// See) || http) ||//www.w3.org/TR/owl2-profiles/#Feature_Overview
				//				
				return false;
			}
			else if (property.equals(OWL.inverseOf) ||						// InverseObjectProperties
				property.equals(OWL.FunctionalProperty) ||					// FunctionalObjectProperty, InverseFunctionalObjectProperty
				property.equals(OWL.InverseFunctionalProperty))
			{
				//
				// Many XXXObjectProperty are not supported by OWL 2 EL
				// See) || http) ||//www.w3.org/TR/owl2-profiles/#Feature_Overview
				//				
				return false; // !tripleObjectType.contains(RdfTripleObjectTypeEnum.Object);
			}
			
		} else if (owlProfileId == OwlProfileEnum.OWL2_RL) {

			if (property.equals(OWL.Thing) ||
				property.equals(OWL.oneOf) ||				// ObjectOneOf, DataOneOf
				property.equals(OWL.unionOf) ||				// ObjectUnionOf, DataUnionOf
				property.equals(OWL.disjointWith))		// DisjointClasses 
			{
				//
				// Usage of owl) ||Thing is restricted by the grammar of OWL 2 RL
				// See) || http) ||//www.w3.org/TR/owl2-profiles/#Entities_3
				//
				return false;
			}
			
		}
		
//		if (owlVersion < OWL_VERSION_2_0) {

			if (property.equals(OWL.disjointWith)) {		
				//
				// Range of owl) ||disjointWith in OWL 1 can be only a single object, but in OWL 2 it can be a list.
				// See) || http) ||//www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2) ||_DisjointClasses.
				//
				return !tripleObjectType.contains(RdfTripleObjectTypeEnum.List);
			} else if (property.equals(OWL2.disjointUnionOf)) {			
				//
				// owl) ||disjointUnionOf are new features of OWL 2 
				// See) || http) ||//www.w3.org/2007/OWL/wiki/New_Features_and_Rationale.
				//
				return false;
			}
			
//		}
		
		if (owlProfileId != OwlProfileEnum.OWL2_Full) {
			
			if (property.equals(OWL2.withRestrictions) ||
				property.equals(RdfVocabulary.XSD.maxExclusive) ||
				property.equals(RdfVocabulary.XSD.minExclusive))
			{
				return false;			
			}
			
		}
		
		return true;
	}	
	
	public boolean supportXsdType(Resource type) {
		if (owlProfileId == OwlProfileEnum.OWL2_EL || owlProfileId == OwlProfileEnum.OWL2_QL) {
			if (type.equals(XSD.xboolean) || type.equals(XSD.xdouble)) {
				//
				// The following datatypes must not be used in OWL 2 EL and OWL 2 QL) ||
				// xsd) ||boolean, xsd) ||double, xsd) ||float, xsd) ||XXXInteger (exception xsd) ||integer and xsd) ||nonNegativeInteger),
				// xsd) ||long, xsd) ||int, xsd) ||short, xsd) ||byte, xsd) ||unsignedXXX, xsd) ||language
				// See) || http) ||//www.w3.org/TR/owl2-profiles/#Entities
				// See) || http) ||//www.w3.org/TR/owl2-profiles/#Entities2
				//
				return false;
			}
		} else if (owlProfileId == OwlProfileEnum.OWL2_RL) {		
			if (type.equals(RdfVocabulary.OWL.real)) {
				return false;
			}			
		}
		
		//
		// OWL1 supports almost all types) ||
		// See) || http) ||//www.w3.org/TR/owl-ref/#rdf-datatype
		//
		return true;
	}
	
}
