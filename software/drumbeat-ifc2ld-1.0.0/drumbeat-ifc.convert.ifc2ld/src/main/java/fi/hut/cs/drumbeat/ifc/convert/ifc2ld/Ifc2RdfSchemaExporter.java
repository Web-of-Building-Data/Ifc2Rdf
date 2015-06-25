package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

/**
 * Followed:
 *    [1] OWL template of Jyrki Oraskari (version 11.04.2012)
 *    [2] Allemang, Dean; Hendler, Jim. Semantic Web for the working ontologies: effective modeling in RDFS and OWL. - 2nd ed., 2011   
 */

import java.io.IOException;
import java.util.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.Cardinality;
import fi.hut.cs.drumbeat.ifc.data.schema.*;
import fi.hut.cs.drumbeat.rdf.OwlProfile;
import fi.hut.cs.drumbeat.rdf.OwlProfileList;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.OwlProfile.RdfTripleObjectTypeEnum;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


/**
 * Exports IFC schema to RDF file
 * 
 * @author Nam
 * 
 */
public class Ifc2RdfSchemaExporter extends Ifc2RdfExporterBase {
	
	private IfcSchema ifcSchema;
	private Ifc2RdfConversionContext context;
	private OwlProfileList owlProfileList;
	private RdfExportAdapter adapter;
	private Model jenaModel;

	private Map<String, IfcCollectionTypeInfo> additionalCollectionTypeDictionary = new HashMap<>();
	
	public Ifc2RdfSchemaExporter(IfcSchema ifcSchema, Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		super(context, rdfExportAdapter);
		this.ifcSchema = ifcSchema;
		this.context = context;
		this.owlProfileList = context.getOwlProfileList();
		this.adapter = rdfExportAdapter;
		this.jenaModel = rdfExportAdapter.getInternalJenaModel();
		
		super.setOntologyNamespaceUri(
				String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName()));
	}

	public Model export() throws IfcException, IOException {
		
		//
		// write header and prefixes
		//
		adapter.startExport();
		
		adapter.setNamespacePrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());
		
		adapter.setNamespacePrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX, Ifc2RdfVocabulary.EXPRESS.getBaseUri());		
		adapter.setNamespacePrefix(Ifc2RdfVocabulary.IFC.BASE_PREFIX, getOntologyNamespaceUri());
		
		adapter.exportEmptyLine();
		
		String conversionOptionsString = context.getConversionOptions().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		conversionOptionsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				owlProfileList.getOwlProfileIds(),
				conversionOptionsString); 
		
		adapter.exportOntologyHeader(getOntologyNamespaceUri(), "1.0", conversionOptionsString);
		
		exportExpressOntology();
		
		
		//
		// write non-entity types section
		//
		Collection<IfcNonEntityTypeInfo> nonEntityTypeInfos = ifcSchema.getNonEntityTypeInfos(); 

		// simple types
		adapter.startSection("SIMPLE TYPES");
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcLiteralTypeInfo) {
				exportLiteralTypeInfo((IfcLiteralTypeInfo)nonEntityTypeInfo);
				adapter.exportEmptyLine();
			} else if (nonEntityTypeInfo instanceof IfcLogicalTypeInfo) {
				exportLogicalTypeInfo((IfcLogicalTypeInfo)nonEntityTypeInfo);				
			}
		}
		adapter.endSection();
		
		// enumeration types
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreEnumerationTypes)) {
			adapter.startSection("ENUMERATION TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcEnumerationTypeInfo) {
					exportEnumerationTypeInfo((IfcEnumerationTypeInfo)nonEntityTypeInfo);
					adapter.exportEmptyLine();
				} 
			}
			adapter.endSection();
		}
		
		// defined types
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreDefinedTypes)) {
			adapter.startSection("DEFINED TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcDefinedTypeInfo) {
					exportDefinedTypeInfo((IfcDefinedTypeInfo)nonEntityTypeInfo);
					adapter.exportEmptyLine();
				}
			}
			adapter.endSection();
		}

		

		// select types
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreSelectTypes)) {
			adapter.startSection("SELECT TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcSelectTypeInfo) {
					exportSelectTypeInfo((IfcSelectTypeInfo)nonEntityTypeInfo);
					adapter.exportEmptyLine();
				} 
			}
			adapter.endSection();
		}

		// collection types
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreCollectionTypes)) {
			adapter.startSection("COLLECTION TYPES");			
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcCollectionTypeInfo) {
					exportCollectionTypeInfo((IfcCollectionTypeInfo)nonEntityTypeInfo);
					adapter.exportEmptyLine();
				} 
			}			
			adapter.endSection();		
		}
		

		//
		// write entity types section
		//
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreEntityTypes)) {
			adapter.startSection("ENTITY TYPES");
			for (IfcEntityTypeInfo entityTypeInfo : ifcSchema.getEntityTypeInfos()) {
				exportEntityTypeInfo(entityTypeInfo);
				adapter.exportEmptyLine();
			}
			adapter.endSection();
		}

		//
		// write entity types section
		//
		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.IgnoreCollectionTypes)) {
			adapter.startSection("ADDITIONAL COLLECTION TYPES");
			for (IfcCollectionTypeInfo collectionTypeInfo : additionalCollectionTypeDictionary.values()) {
				exportCollectionTypeInfo(collectionTypeInfo);
				adapter.exportEmptyLine();
			}
			adapter.endSection();
		}

		adapter.endExport();
		
		return super.getJenaModel();

	}
	
	private void exportExpressOntology() {
		
		boolean declareFunctionalProperties = owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.LOGICAL, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.TRUE, RDF.type, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.FALSE, RDF.type, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.UNKNOWN, RDF.type, Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Enum, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Defined, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Select, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Entity, RDF.type, OWL.Class);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Collection, RDF.type, OWL.Class);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.slot, RDF.type, OWL.ObjectProperty);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type, OWL.FunctionalProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.range, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);

		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.size, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {		
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.size, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.size, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.size, RDFS.range, XSD.integer);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {		
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.range, XSD.integer);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.range, XSD.integer);		
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.List, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.List, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Array, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Array, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Set, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Set, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Bag, RDF.type, OWL.Class);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Bag, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.Slot, RDF.type, OWL.Class);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.item, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.item, RDF.type, OWL.FunctionalProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.item, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.index, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.index, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.index, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.index, RDFS.range, XSD.integer);		
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type, OWL.FunctionalProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.next, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.next, RDF.type, OWL.FunctionalProperty);
		}
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.next, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.next, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDF.type, OWL.Class);
//		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDFS.subClassOf, OWL.ObjectProperty);

//		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDF.type, OWL.DatatypeProperty);
//		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.EntityProperty);
//		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDFS.range, XSD.integer);
		
		adapter.exportTriple(Ifc2RdfVocabulary.EXPRESS.hasValue, RDF.type, OWL.DatatypeProperty);

	}
	
	private void exportLiteralTypeInfo(IfcLiteralTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(formatTypeName(typeInfo)); 
//		adapter.exportTriple(typeResource, RDF.type, RDFS.Datatype);
//		adapter.exportTriple(typeResource, OWL.sameAs, super.getXsdDataType(typeInfo));
		
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		
		Resource xsdDataType = super.getXsdDataType(typeInfo);
		Property property = getHasProperty(typeInfo);		
		adapter.exportTriple(property, RDF.type, OWL.DatatypeProperty);
		if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
			adapter.exportTriple(property, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		adapter.exportTriple(property, RDFS.subPropertyOf, Ifc2RdfVocabulary.EXPRESS.hasValue);
		adapter.exportTriple(property, RDFS.domain, typeResource);
		adapter.exportTriple(property, RDFS.range, xsdDataType);
		
//		if (owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {		
//			
//			writePropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.value, OWL.allValuesFrom, super.getXsdDataType((IfcLiteralTypeInfo)baseTypeInfo));
//		
//		}
		
	}
	
	private void exportLogicalTypeInfo(IfcLogicalTypeInfo typeInfo) {
		
		String typeUri = super.formatExpressOntologyName(typeInfo.getName());
		Resource typeResource = createUriResource(typeUri);
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (String value : enumValues) {
			enumValueNodes.add(super.createUriResource(super.formatExpressOntologyName(value)));
		}
		
		if (owlProfileList.supportsRdfProperty(OWL.oneOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			RDFList rdfList = super.createList(enumValueNodes);			
			adapter.exportTriple(typeResource, OWL.oneOf, rdfList);
		} else { // if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertLogicalValuesToString)) {
			enumValueNodes.stream().forEach(node ->
					adapter.exportTriple((Resource)node, RDF.type, typeResource));			
		}		
	}	

	private void exportEnumerationTypeInfo(IfcEnumerationTypeInfo typeInfo) {
		
		String typeUri = super.formatTypeName(typeInfo);
		Resource typeResource = createUriResource(typeUri);
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Enum);		

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (String value : enumValues) {
			enumValueNodes.add(super.createUriResource(super.formatOntologyName(value)));
		}		
		
		if (owlProfileList.supportsRdfProperty(OWL.oneOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			Resource equivalentTypeResource = super.createAnonResource();
			adapter.exportTriple(typeResource, OWL.equivalentClass, equivalentTypeResource);

			RDFList rdfList = super.createList(enumValueNodes);			
			adapter.exportTriple(equivalentTypeResource, OWL.oneOf, rdfList);
		} else { // if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertEnumerationValuesToString)) {
			enumValueNodes.stream().forEach(node ->
					adapter.exportTriple((Resource)node, RDF.type, typeResource));			
		}	
		
	}

	private void exportSelectTypeInfo(IfcSelectTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Select);
		

		List<String> subTypeNames = typeInfo.getSelectTypeInfoNames();
		List<Resource> subTypeResources = new ArrayList<>();
		for (String typeName : subTypeNames) {
			subTypeResources.add(super.createUriResource(super.formatOntologyName(typeName)));
		}
		
		if (owlProfileList.supportsRdfProperty(OWL.unionOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany)) && subTypeResources.size() > 1) {			
			RDFList rdfList = super.createList(subTypeResources);			
			// See samples: [2, p.250]
			adapter.exportTriple(typeResource, OWL.unionOf, rdfList);
		} else {
			subTypeResources.stream().forEach(subTypeResource ->
					adapter.exportTriple((Resource)subTypeResource, RDFS.subClassOf, typeResource));			
		}
		
	}
	
	private void exportDefinedTypeInfo(IfcDefinedTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo));
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Defined);			
		
		IfcTypeInfo baseTypeInfo = typeInfo.getSuperTypeInfo();
		assert(baseTypeInfo != null);
		
		if (baseTypeInfo instanceof IfcLiteralTypeInfo) {
			
			Resource superTypeResource = super.createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));
			adapter.exportTriple(typeResource, RDFS.subClassOf, superTypeResource);			
			
		} else if (baseTypeInfo instanceof IfcLogicalTypeInfo) {
			
			Resource superTypeResource = super.createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));

			Property property = getHasProperty(baseTypeInfo);		
			adapter.exportTriple(property, RDF.type, OWL.ObjectProperty);
			if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
				adapter.exportTriple(property, RDF.type, OWL.FunctionalProperty);
			}
			adapter.exportTriple(property, RDFS.domain, typeResource);
			adapter.exportTriple(property, RDFS.range, superTypeResource);
			
			
		} else {
			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(baseTypeInfo)));			

		}		
		
	}

	private void exportCollectionTypeInfo(IfcCollectionTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		
		IfcCollectionKindEnum collectionKind = typeInfo.getCollectionKind();
		IfcCollectionTypeInfo superCollectionTypeWithItemTypeAndNoCardinalities = typeInfo.getSuperCollectionTypeWithItemTypeAndNoCardinalities();
		IfcCollectionTypeInfo superCollectionTypeWithCardinalititesAndNoItemType = typeInfo.getSuperCollectionTypeWithCardinalitiesAndNoItemType();
		
		if (superCollectionTypeWithItemTypeAndNoCardinalities == null) {
			
			if (superCollectionTypeWithCardinalititesAndNoItemType == null) { // both supertypes are null			
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.getCollectionClass(collectionKind));
			}			
			
			Resource slotClassResource = createUriResource(super.formatOntologyName(super.formatSlotClassName(typeInfo.getName())));
			adapter.exportTriple(slotClassResource, RDF.type, OWL.Class);
			adapter.exportTriple(slotClassResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Slot);
			
			//
			// write restriction on type of property olo:slot
			//
			if (typeInfo.getItemTypeInfo() != null && owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {
	
				exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.allValuesFrom, slotClassResource);
				
				adapter.exportEmptyLine();
			
				exportPropertyRestriction(slotClassResource, Ifc2RdfVocabulary.EXPRESS.item, OWL.allValuesFrom,
						createUriResource(super.formatTypeName(typeInfo.getItemTypeInfo())));
			}
			
		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithItemTypeAndNoCardinalities);			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			additionalCollectionTypeDictionary.put(superTypeName, superCollectionTypeWithItemTypeAndNoCardinalities);
			
		}
		
		if (superCollectionTypeWithCardinalititesAndNoItemType == null) {
			
			//
			// write restriction on type of property olo:slot
			//
			Cardinality cardinality = typeInfo.getCardinality();
			if (cardinality != null &&
					context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportPropertyCardinalities) &&
					owlProfileList.supportsRdfProperty(OWL.cardinality, null)) {
				

				int minCardinality = cardinality.getMinCardinality();
				int maxCardinality = cardinality.getMaxCardinality();				

				if (minCardinality != maxCardinality) {
					
					if (minCardinality != Cardinality.UNBOUNDED) {
						
						exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.minCardinality,
								getJenaModel().createTypedLiteral(cardinality.getMinCardinality()));
						
						
					}
					
					if (maxCardinality != Cardinality.UNBOUNDED) {
						
						exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.maxCardinality,
								getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));						
						
					}
					
				} else {
					
					exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.cardinality,
							getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));					
					
				}
				
				
				adapter.exportEmptyLine();
			
			}

		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithCardinalititesAndNoItemType);			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			additionalCollectionTypeDictionary.put(superTypeName, superCollectionTypeWithCardinalititesAndNoItemType);

		}		

	}
	
//	private void writeAdditionalListTypeInfos() {
//		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
//			for (Entry<Resource, Cardinality> entry : collectionSuperTypes.entrySet()) {
//				Resource oloCardinalityClassResource = entry.getKey();
//				Cardinality cardinality = entry.getValue();
//				
//				adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Class);
//				
//				if (cardinality != null && context.supportsRdfProperty(OWL.oneOf, OwlProfile.RdfTripleObjectTypeEnum.DataList)) {
//					
//					int min = cardinality.getMin();
//					int max = cardinality.getMax();
//				
//					if (max != Cardinality.UNBOUNDED) {
//						//
//						// write restriction on length of list as [ owl:oneOf (min, min + 1, ..., max) ]
//						//
//						Resource blankNode1 = super.createAnonResource();
//						adapter.exportTriple(blankNode1, RDF.type, RDFS.Datatype);
//						adapter.exportTriple(blankNode1, OWL.oneOf, getCardinalityValueList(min, max));
//	
//						adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Restriction);
//						adapter.exportTriple(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);		
//						adapter.exportTriple(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
//						
//					} else if (min != Cardinality.ZERO && context.supportsRdfProperty(OWL.complementOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
//						
//						//
//						// write restriction on length of list as [ owl:compelmentOf [ owl:oneOf (0, 1, ..., min - 1) ]
//						//
//						Resource blankNode2 = super.createAnonResource();
//						adapter.exportTriple(blankNode2, RDF.type, RDFS.Datatype);
//						adapter.exportTriple(blankNode2, OWL.oneOf, getCardinalityValueList(Cardinality.ZERO, min - 1));
//	
//						Resource blankNode1 = super.createAnonResource();
//						adapter.exportTriple(blankNode1, RDF.type, OWL.Class);
//						adapter.exportTriple(blankNode1, OWL.complementOf, blankNode2); 
//	
//						adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Restriction);
//						adapter.exportTriple(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);
//						adapter.exportTriple(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
//						
//					}
//					
//					adapter.exportEmptyLine();					
//					
//				} // for
//				
//			}
//		} else { // !context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ConvertRdfListToOloOrderedList)
//
//			// define ifc:EmptyList
//			Resource emptyListTypeUri = createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.EMPTY_LIST)); 
//			adapter.exportTriple(emptyListTypeUri, RDF.type, OWL.Class);
//			adapter.exportTriple(emptyListTypeUri, RDFS.subClassOf, RDF.List);
//			adapter.exportEmptyLine();
//
//		}
//	}

	private void exportEntityTypeInfo(IfcEntityTypeInfo typeInfo) throws IOException, IfcException {
		
		Resource typeResource = createUriResource(super.formatTypeName(typeInfo));
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);

		//
		// OWL2 supports owl:disjointUnionOf
		// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F1:_DisjointUnion
		//
		List<IfcEntityTypeInfo> disjointClasses = null;
		if (typeInfo.isAbstractSuperType() && owlProfileList.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
			List<IfcEntityTypeInfo> allSubtypeInfos = typeInfo.getSubTypeInfos();
			if (allSubtypeInfos.size() > 1) { // OWL2.disjointUnionOf requires at least two members
				List<RDFNode> allSubtypeResources = new ArrayList<>(allSubtypeInfos.size());
				for (IfcEntityTypeInfo subTypeInfo : allSubtypeInfos) {
					allSubtypeResources.add(createUriResource(super.formatTypeName(subTypeInfo)));
				}
				adapter.exportTriple(typeResource, OWL2.disjointUnionOf, super.createList(allSubtypeResources));
			}
		}

		//
		// write entity info
		//
		IfcEntityTypeInfo superTypeInfo = typeInfo.getSuperTypeInfo();
		if (superTypeInfo != null) {
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(superTypeInfo)));
			
			if (!superTypeInfo.isAbstractSuperType() || !owlProfileList.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
				
				List<IfcEntityTypeInfo> allSubtypeInfos = superTypeInfo.getSubTypeInfos();
				
				if (allSubtypeInfos.size() > 1) {
					
					int indexOfCurrentType = allSubtypeInfos.indexOf(typeInfo);
					
					if (allSubtypeInfos.size() > 2 && owlProfileList.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
						//
						// OWL2 allow object of property "owl:disjointWith" to be rdf:list
						// All classes will be pairwise disjoint
						// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
						//
						disjointClasses = allSubtypeInfos;
						
					} else if (owlProfileList.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) { // context.getOwlVersion() < OwlProfile.OWL_VERSION_2_0
						
						//
						// OWL1 doesn't allow object of property "owl:disjointWith" to be rdf:list
						// See: http://www.w3.org/TR/owl-ref/#disjointWith-def
						//
						if (indexOfCurrentType + 1 < allSubtypeInfos.size()) {

							for (int i = indexOfCurrentType + 1; i < allSubtypeInfos.size(); ++i) {
								adapter.exportTriple(typeResource, OWL.disjointWith, createUriResource(super.formatTypeName(allSubtypeInfos.get(i))));
							}
							
						}
					}
					
				}
				
			}
			
		} else {
			adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Entity);			
		}
		
		//
		// write unique keys
		//
		List<IfcUniqueKeyInfo> uniqueKeyInfos = typeInfo.getUniqueKeyInfos();
		if (uniqueKeyInfos != null && owlProfileList.supportsRdfProperty(OWL2.hasKey, OwlProfile.RdfTripleObjectTypeEnum.ANY)) {
			for (IfcUniqueKeyInfo uniqueKeyInfo : uniqueKeyInfos) {
				List<Resource> attributeResources = new ArrayList<>();
				for (IfcAttributeInfo attributeInfo : uniqueKeyInfo.getAttributeInfos()) {
					attributeResources.add(super.createUriResource(super.formatAttributeName(attributeInfo)));
				}
				adapter.exportTriple(typeResource, OWL2.hasKey, super.createList(attributeResources));				
			}
		}
		
		//
		// add attribute info to attribute info map
		//
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportProperties)) {
			List<IfcAttributeInfo> attributeInfos = typeInfo.getAttributeInfos();
			if (attributeInfos != null) {
				for (IfcAttributeInfo attributeInfo : attributeInfos) {
					Resource attributeResource = createUriResource(super.formatAttributeName(attributeInfo));
					
					adapter.exportTriple(attributeResource, RDF.type, OWL.ObjectProperty);						
					adapter.exportTriple(attributeResource, RDF.type, Ifc2RdfVocabulary.EXPRESS.EntityProperty);						
//					adapter.exportTriple(attributeResource, Ifc2RdfVocabulary.EXPRESS.propertyIndex, jenaModel.createTypedLiteral(attributeInfo.getAttributeIndex(), XSD.integer.getURI()) );						

					if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
						adapter.exportTriple(attributeResource, RDF.type, OWL.FunctionalProperty);
					}
					
					if (owlProfileList.supportsRdfProperty(OWL.Restriction, null)) {

						//
						// write constraint about property type
						//
						if (owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {
							exportPropertyRestriction(typeResource, attributeResource, OWL.allValuesFrom,
									createUriResource(super.formatTypeName(attributeInfo.getAttributeTypeInfo())));
						}
						
						if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportPropertyCardinalities) &&
								owlProfileList.supportsRdfProperty(OWL.cardinality, null)) {
							
							// write attribute cardinality
	//							if (attributeInfo.isOptional()) {
	//								writePropertyRestriction(typeResource, attributeResource, OWL.minCardinality,
	//										getJenaModel().createTypedLiteral(0));
						
								exportPropertyRestriction(typeResource, attributeResource, OWL.maxCardinality,
										getJenaModel().createTypedLiteral(1));								
						
	//							} else {								
	//								writePropertyRestriction(typeResource, attributeResource, OWL.cardinality,
	//										getJenaModel().createTypedLiteral(1));
	//							}
							
						}
					}
					
				}
			}
		}
		
		
		if (disjointClasses != null) {
			//
			// OWL2 allow object of property "owl:disjointWith" to be rdf:list
			// All classes will be pairwise disjoint
			// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
			//
			
			adapter.exportEmptyLine();
			
			Resource blankNode = super.createAnonResource();
			adapter.exportTriple(blankNode, RDF.type, OWL2.AllDisjointClasses);
			
			List<Resource> disjointClassResources = new ArrayList<>();
			for (IfcTypeInfo disjointClassTypeInfo : disjointClasses) {
				disjointClassResources.add(super.createUriResource(super.formatTypeName(disjointClassTypeInfo)));
			}
			
			adapter.exportTriple(blankNode, OWL2.members, super.createList(disjointClassResources));			
		}

	}
	
//	private void writeAttributeConstraint(Resource typeResource, Resource attributeResource, Property constraintKindProperty, RDFNode constraintValueTypeResource) {		
//		Resource blankNode = super.createAnonResource();
//		adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
//		adapter.exportTriple(blankNode, OWL.onProperty, attributeResource);
//		adapter.exportTriple(blankNode, constraintKindProperty, constraintValueTypeResource);
//		adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);		
//	}

//	private void writeAttributeInfo(String attributeName, List<IfcAttributeInfo> attributeInfoList) {
//
//		Set<String> domainTypeNames = new TreeSet<>();
//		Set<String> rangeTypeNames = new TreeSet<>();
//		EnumSet<IfcTypeEnum> valueTypes = EnumSet.noneOf(IfcTypeEnum.class);
//		boolean isFunctionalProperty = true;
//		boolean isInverseFunctionalProperty = true;
//		
//		if (!context.supportsRdfProperty(OWL.FunctionalProperty, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
//			isFunctionalProperty = false;
//			isInverseFunctionalProperty = false;
//		}
//		
//		for (IfcAttributeInfo attributeInfo : attributeInfoList) {
//			IfcEntityTypeInfo domainTypeInfo = attributeInfo.getEntityTypeInfo();
//			domainTypeNames.add(super.formatTypeName(domainTypeInfo));
//			
//			IfcTypeInfo rangeTypeInfo = attributeInfo.getAttributeTypeInfo(); 
//			rangeTypeNames.add(super.formatTypeName(rangeTypeInfo));
//			valueTypes.addAll(rangeTypeInfo.getValueTypes());
//			
//			isFunctionalProperty = isFunctionalProperty && attributeInfo.isFunctional();
//			isInverseFunctionalProperty = isInverseFunctionalProperty && attributeInfo.isInverseFunctional();			
//		}
//		
//		//
//		// owl:DataProperty, owl:ObjectProperty, or rdf:Property
//		//
//		Resource attributeResource = createUriResource(super.formatOntologyName(attributeName));
//		if (valueTypes.size() == 1 && !valueTypes.contains(IfcTypeEnum.ENTITY)) {
//			adapter.exportTriple(attributeResource, RDF.type, OWL.DatatypeProperty);
//		} else {
//			adapter.exportTriple(attributeResource, RDF.type, OWL.ObjectProperty);
//		}
//
//		//
//		// owl:FunctionalProperty
//		//
//		if (isFunctionalProperty) {
//			adapter.exportTriple(attributeResource, RDF.type, OWL.FunctionalProperty);			
//		}
//		
//		//
//		// owl:InverseFunctionalProperty
//		//
//		if (isInverseFunctionalProperty) {
//			adapter.exportTriple(attributeResource, RDF.type, OWL.InverseFunctionalProperty);
//		}
//		
//		//
//		// rdfs:domain, rdfs:range
//		//
//		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.PrintPropertyDomainAndRange)) {
//		
//			if (domainTypeNames.size() == 1) {
//				adapter.exportTriple(attributeResource, RDFS.domain, createUriResource(domainTypeNames.iterator().next()));
//			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
//				//
//				// In OWL Lite the value of rdfs:domain must be a class identifier. 
//				// See: http://www.w3.org/TR/owl-ref/#domain-def
//				//
//				
//				List<RDFNode> domainTypeResources = new ArrayList<>();
//				for (String domainTypeName : domainTypeNames) {
//					domainTypeResources.add(createUriResource(domainTypeName));
//				}				
//				
//				Resource domainTypeResource = super.createAnonResource();
//				adapter.exportTriple(domainTypeResource, RDF.type, OWL.Class);
//				adapter.exportTriple(domainTypeResource, OWL.unionOf, createList(domainTypeResources));	
//				adapter.exportTriple(attributeResource, RDFS.domain, domainTypeResource);			
//			}
//	
//			//
//			// Caution: rdfs:range should be used with care
//			// See: http://www.w3.org/TR/owl-ref/#range-def
//			//
//			if (rangeTypeNames.size() == 1) {
//				adapter.exportTriple(attributeResource, RDFS.range, createUriResource(rangeTypeNames.iterator().next()));
//			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
//				//
//				// In OWL Lite the only type of class descriptions allowed as objects of rdfs:range are class names. 
//				// See: http://www.w3.org/TR/owl-ref/#range-def
//				//				
//				List<RDFNode> rangeTypeResources = new ArrayList<>();
//				for (String rangeTypeName : rangeTypeNames) {
//					rangeTypeResources.add(createUriResource(rangeTypeName));
//				}				
//				
//				Resource rangeTypeResource = super.createAnonResource();
//				adapter.exportTriple(rangeTypeResource, RDF.type, OWL.Class);
//				adapter.exportTriple(rangeTypeResource, OWL.unionOf, createList(rangeTypeResources));	
//				adapter.exportTriple(attributeResource, RDFS.range, rangeTypeResource);				
//			}
//			
//		}
//		
//		if (attributeInfoList.size() == 1) {
//			//
//			// write owl:inverseOf
//			// See: http://www.w3.org/TR/owl-ref/#inverseOf-def
//			//
//			if (attributeInfoList.get(0) instanceof IfcInverseLinkInfo && context.supportsRdfProperty(OWL.inverseOf, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
//				IfcInverseLinkInfo inverseLinkInfo = (IfcInverseLinkInfo)attributeInfoList.get(0);
//				adapter.exportTriple(attributeResource, OWL.inverseOf, createUriResource(super.formatAttributeName(inverseLinkInfo.getOutgoingLinkInfo())));
//			}
//		} else {
//			if (avoidDuplicationOfPropertyNames) {
//				for (IfcAttributeInfo subAttributeInfo : attributeInfoList) {
//					adapter.exportEmptyLine();
//					List<IfcAttributeInfo> attributeInfoSubList = new ArrayList<>();
//					attributeInfoSubList.add(subAttributeInfo);
//					String subAttributeName = subAttributeInfo.getUniqueName();
//					writeAttributeInfo(subAttributeName, attributeInfoSubList);
//					adapter.exportTriple(createUriResource(super.formatOntologyName(subAttributeName)), RDFS.subPropertyOf, attributeResource);
//				}
//			}
//		}
//	}
	
//	private RDFList getCardinalityValueList(int min, int max) {
//		List<Literal> literals = new ArrayList<>();
//		for (int i = min; i <= max; ++i) {
//			literals.add(getJenaModel().createTypedLiteral(i));
//		}
//		return super.createList(literals);
//	}	
	
	private void exportPropertyRestriction(Resource classResource, Resource propertyResource, Property restrictionProperty, RDFNode propertyValue) {
		Resource baseTypeResource = super.createAnonResource();
		adapter.exportTriple(baseTypeResource, RDF.type, OWL.Restriction);
		adapter.exportTriple(baseTypeResource, OWL.onProperty, propertyResource);
		adapter.exportTriple(baseTypeResource, restrictionProperty, propertyValue);
		
		adapter.exportTriple(classResource, RDFS.subClassOf, baseTypeResource);		
	}	

	
	
}