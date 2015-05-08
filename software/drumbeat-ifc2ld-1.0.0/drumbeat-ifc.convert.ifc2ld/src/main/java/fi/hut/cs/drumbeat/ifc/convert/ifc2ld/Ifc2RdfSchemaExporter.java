package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

/**
 * Followed:
 *    [1] OWL template of Jyrki Oraskari (version 11.04.2012)
 *    [2] Allemang, Dean; Hendler, Jim. Semantic Web for the working ontologies: effective modeling in RDFS and OWL. - 2nd ed., 2011   
 */

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Literal;
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

import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.Cardinality;
import fi.hut.cs.drumbeat.ifc.data.schema.*;
import fi.hut.cs.drumbeat.rdf.OwlProfile;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.OwlProfile.RdfTripleObjectTypeEnum;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary.OLO;
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
	private RdfExportAdapter adapter;

//	private Map<IfcLiteralTypeInfo, Resource> literalTypeInfomap;
	
	private Map<String, List<IfcAttributeInfo>> attributeInfoMap = new HashMap<>();
	private boolean avoidDuplicationOfPropertyNames;
	private Map<String, IfcCollectionTypeInfo> collectionSuperTypes = new HashMap<>();
	
	private static final IfcEntityTypeInfo IFC_ENTITY = new IfcEntityTypeInfo(null, "ENTITY");
	
	public Ifc2RdfSchemaExporter(IfcSchema ifcSchema, Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		super(context, rdfExportAdapter);
		this.ifcSchema = ifcSchema;
		this.context = context;
		this.adapter = rdfExportAdapter;
		
		super.setOntologyNamespacePrefix(context.getOntologyPrefix());
		
		super.setOntologyNamespaceUri(
				String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName()));
	}

	public Model export() throws IfcException, IOException {
		
		//
		// write header and prefixes
		//
		adapter.startExport();
		
		adapter.setNamespacePrefix(RdfVocabulary.OWL.BASE_PREFIX.replace(StringUtils.COLON, ""), OWL.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDF.BASE_PREFIX.replace(StringUtils.COLON, ""), RDF.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDFS.BASE_PREFIX.replace(StringUtils.COLON, ""), RDFS.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.XSD.BASE_PREFIX.replace(StringUtils.COLON, ""), XSD.getURI());		
		
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
			adapter.setNamespacePrefix(RdfVocabulary.OLO.BASE_PREFIX, RdfVocabulary.OLO.BASE_URI);
		}
		
		adapter.setNamespacePrefix(getOntologyNamespacePrefix(), getOntologyNamespaceUri());
		adapter.exportEmptyLine();
		
		String conversionOptionsString = context.getConversionOptions().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		conversionOptionsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				context.getOwlProfileId(),
				conversionOptionsString); 
		
		adapter.exportOntologyHeader(getOntologyNamespaceUri(), "1.0", conversionOptionsString);		
		
		
		//
		// write defined types section
		//
		Collection<IfcDefinedTypeInfo> definedTypeInfos = ifcSchema.getDefinedTypeInfos(); 

		// simple types
		adapter.startSection("SIMPLE TYPES");
		for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
			if (definedTypeInfo instanceof IfcLiteralTypeInfo) {
				exportLiteralTypeInfo((IfcLiteralTypeInfo)definedTypeInfo);
				adapter.exportEmptyLine();
			} 
		}
		adapter.endSection();

		// enumeration types
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportEnumerationTypes)) {
			adapter.startSection("ENUMERATION TYPES");
			for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
				if (definedTypeInfo instanceof IfcEnumerationTypeInfo) {
					exportEnumerationTypeInfo((IfcEnumerationTypeInfo)definedTypeInfo);
					adapter.exportEmptyLine();
				} 
			}
			adapter.endSection();
		}

		// redirect types
		adapter.startSection("REDIRECT TYPES");
		for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
			if (definedTypeInfo instanceof IfcRedirectTypeInfo) {
				writeRedirectTypeInfo((IfcRedirectTypeInfo)definedTypeInfo);
				adapter.exportEmptyLine();
			}
		}
		adapter.endSection();

		// select types
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportSelectTypes)) {
			adapter.startSection("SELECT TYPES");
			for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
				if (definedTypeInfo instanceof IfcSelectTypeInfo) {
					exportSelectTypeInfo((IfcSelectTypeInfo)definedTypeInfo);
					adapter.exportEmptyLine();
				} 
			}
			adapter.endSection();
		}

		// collection types
		adapter.startSection("COLLECTION TYPES");
		
		for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
			if (definedTypeInfo instanceof IfcCollectionTypeInfo) {
				writeCollectionTypeInfo((IfcCollectionTypeInfo)definedTypeInfo);
				adapter.exportEmptyLine();
			} 
		}
		
//		for (IfcCollectionTypeInfo superTypeInfo : collectionSuperTypes.values()) {
//			writeCollectionTypeInfo((IfcCollectionTypeInfo)superTypeInfo);
//			adapter.exportEmptyLine();			
//		}
		
//		writeAdditionalListTypeInfos();		
		adapter.endSection();
		
		//
		// build attribute info map
		//
		// Added 20140929 - Nam: to check if export properties is needed
		// 
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportProperties)) {
			for (IfcEntityTypeInfo entityTypeInfo : ifcSchema.getEntityTypeInfos()) {
				for (IfcAttributeInfo attributeInfo : entityTypeInfo.getAttributeInfos()) {
					String attributeName = attributeInfo.getName();
					List<IfcAttributeInfo> attributeInfoList = attributeInfoMap.get(attributeName);
					if (attributeInfoList == null) {
						attributeInfoList = new ArrayList<>();
						attributeInfoMap.put(attributeName, attributeInfoList);
					}
					attributeInfoList.add(attributeInfo);
				}
				
				for (IfcAttributeInfo attributeInfo : entityTypeInfo.getInverseLinkInfos()) {
					String attributeName = attributeInfo.getName();
					List<IfcAttributeInfo> attributeInfoList = attributeInfoMap.get(attributeName);
					if (attributeInfoList == null) {
						attributeInfoList = new ArrayList<>();
						attributeInfoMap.put(attributeName, attributeInfoList);
					}
					attributeInfoList.add(attributeInfo);
				}
			}

			//
			// avoid duplication of property names
			//
			avoidDuplicationOfPropertyNames = context.isEnabledOption(Ifc2RdfConversionOptionsEnum.AvoidDuplicationOfPropertyNames);		
			if (avoidDuplicationOfPropertyNames) {
				for (Entry<String, List<IfcAttributeInfo>> entry : attributeInfoMap.entrySet()) {
					if (entry.getValue().size() > 1) {
						for (IfcAttributeInfo attributeInfo : entry.getValue()) {
							attributeInfo.setUniqueName(String.format("%s_of_%s", attributeInfo.getName(), attributeInfo.getEntityTypeInfo().getName()));
						}
					}
				}
			}
		
		}
		

		//
		// write entity types section
		//
		adapter.startSection("ENTITY TYPES");
		Resource entityTypeResource = createUriResource(super.formatTypeName(IFC_ENTITY));
		adapter.exportTriple(entityTypeResource, RDF.type, OWL.Class);
		adapter.exportTriple(entityTypeResource, RDFS.subClassOf, OWL.Thing);
		adapter.exportEmptyLine();

		for (IfcEntityTypeInfo entityTypeInfo : ifcSchema.getEntityTypeInfos()) {
			writeEntityTypeInfoSection(entityTypeInfo);
			adapter.exportEmptyLine();
		}
		adapter.endSection();

		//
		// write collection types used for entity attributes
		//
		adapter.startSection("ENTITY ATTRIBUTES");
		for (Entry<String, List<IfcAttributeInfo>> entry : attributeInfoMap.entrySet()) {
			writeAttributeInfo(entry.getKey(), entry.getValue());
			adapter.exportEmptyLine();
		}
		adapter.endSection();
		
//		adapter.writeSectionHead("PREDEFINED VALUES");
//		adapter.writeRdfTriple(converter.getNull(), RDF.type, RdfVocabulary.OWL_NOTHING);
		adapter.endSection();

		adapter.endExport();
		
		return super.getJenaModel();

	}
	
	private void exportLiteralTypeInfo(IfcLiteralTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(formatExpressOntologyName(typeInfo.getName())); 
		adapter.exportTriple(typeResource, RDF.type, RDFS.Datatype);
		adapter.exportTriple(typeResource, OWL.sameAs, super.getXsdDataType(typeInfo));
		
	}	

	private void exportEnumerationTypeInfo(IfcEnumerationTypeInfo typeInfo) {
		
		String typeUri = super.formatTypeName(typeInfo);
		Resource typeResource = createUriResource(typeUri);
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.EnumerationClass);

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertEnumerationValuesToString)) {
			for (String value : enumValues) {
				enumValueNodes.add(super.createUriResource(super.formatOntologyName(value)));
			}
		} else {
			for (String value : enumValues) {
				enumValueNodes.add(getJenaModel().createTypedLiteral(value));
			}
		}
		
		if (context.supportsRdfProperty(OWL.oneOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			RDFList rdfList = super.createList(enumValueNodes);			
			adapter.exportTriple(typeResource, OWL.oneOf, rdfList);
		} else if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertEnumerationValuesToString)) {
			enumValueNodes.stream().forEach(node ->
					adapter.exportTriple((Resource)node, RDF.type, typeResource));			
		}	
		
	}

	private void exportSelectTypeInfo(IfcSelectTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.SelectClass);
		

		List<String> subTypeNames = typeInfo.getSelectTypeInfoNames();
		List<Resource> subTypeResources = new ArrayList<>();
		for (String typeName : subTypeNames) {
			subTypeResources.add(super.createUriResource(super.formatOntologyName(typeName)));
		}
		
		if (context.supportsRdfProperty(OWL.unionOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			RDFList rdfList = super.createList(subTypeResources);			
			// See samples: [2, p.250]
			adapter.exportTriple(typeResource, OWL.unionOf, rdfList);
		} else {
			subTypeResources.stream().forEach(enumValueResource ->
					adapter.exportTriple((Resource)enumValueResource, RDF.type, typeResource));			
		}
		
	}
	
	private Resource createOwlRestrictionClass(Property onProperty, Property restriction, Resource valueType) {
		Resource baseTypeResource = super.createAnonResource();
		adapter.exportTriple(baseTypeResource, RDF.type, OWL.Restriction);
		adapter.exportTriple(baseTypeResource, OWL.onProperty, onProperty);
		adapter.exportTriple(baseTypeResource, restriction, valueType);
		return baseTypeResource;
	}	

	private void writeRedirectTypeInfo(IfcRedirectTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo));
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.DefinedClass);
		
		IfcTypeInfo baseTypeInfo = typeInfo.getRedirectTypeInfo();
		if (!(baseTypeInfo instanceof IfcLiteralTypeInfo)) {
			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(baseTypeInfo)));			
			
		} else if (context.supportsRdfProperty(OWL.allValuesFrom, null)) {
			
			Resource baseTypeResource = createOwlRestrictionClass(RDF.value, OWL.allValuesFrom,
					super.getXsdDataType((IfcLiteralTypeInfo)baseTypeInfo));
			
			adapter.exportTriple(typeResource, RDFS.subClassOf, baseTypeResource);			
		}		
		
	}

	private void writeCollectionTypeInfo(IfcCollectionTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		
		Cardinality cardinality = typeInfo.getCardinality();
		
		IfcCollectionTypeInfo superCollectionTypeWithoutCardinalities = typeInfo.getSuperCollectionTypeWithoutCardinalities();
		if (superCollectionTypeWithoutCardinalities == null) {
			
			IfcCollectionKindEnum collectionKind = typeInfo.getCollectionKind();
			if (collectionKind == IfcCollectionKindEnum.List) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.ListClass);				
			} else if (collectionKind == IfcCollectionKindEnum.Set) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.SetClass);				
			} else if (collectionKind == IfcCollectionKindEnum.Array) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.ArrayClass);				
			} else {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.BagClass);				
			}			

			Resource slotClassResource = createUriResource(super.formatOntologyName(super.formatSlotClassName(typeInfo.getName())));
			adapter.exportTriple(slotClassResource, RDF.type, OWL.Class);
			adapter.exportTriple(slotClassResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.CollectionSlotClass);
			
			//
			// write restriction on type of property olo:slot
			//
			if (typeInfo.getItemTypeInfo() != null && context.supportsRdfProperty(OWL.allValuesFrom, null)) {
				Resource blankNode = super.createAnonResource();
				adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
				adapter.exportTriple(blankNode, OWL.onProperty, Ifc2RdfVocabulary.EXPRESS.slot);
				adapter.exportTriple(blankNode, OWL.allValuesFrom, slotClassResource);			
	
				adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);
				
				adapter.exportEmptyLine();
			
				Resource oloItemClassNode = super.createAnonResource();
				adapter.exportTriple(oloItemClassNode, RDF.type, OWL.Restriction);
				adapter.exportTriple(oloItemClassNode, OWL.onProperty, Ifc2RdfVocabulary.EXPRESS.item);
				adapter.exportTriple(oloItemClassNode, OWL.allValuesFrom, createUriResource(super.formatTypeName(typeInfo.getItemTypeInfo())));
	
				adapter.exportTriple(slotClassResource, RDFS.subClassOf, oloItemClassNode);
			}
			
		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithoutCardinalities);			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			collectionSuperTypes.put(superTypeName, superCollectionTypeWithoutCardinalities);
			
		}
		
		IfcCollectionTypeInfo superCollectionTypeWithoutItemType = typeInfo.getSuperCollectionTypeWithoutItemType();
		if (superCollectionTypeWithoutItemType == null) {
			assert (cardinality != null);
			
			IfcCollectionKindEnum collectionKind = typeInfo.getCollectionKind();
			if (collectionKind == IfcCollectionKindEnum.List) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.ListClass);				
			} else if (collectionKind == IfcCollectionKindEnum.Set) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.SetClass);				
			} else if (collectionKind == IfcCollectionKindEnum.Array) {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.ArrayClass);				
			} else {
				adapter.exportTriple(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.BagClass);				
			}
			
			//
			// write restriction on type of property olo:slot
			//
			if (context.supportsRdfProperty(OWL.cardinality, null)) {
				Resource blankNode = super.createAnonResource();
				
				int minCardinality = cardinality.getMin();
				int maxCardinality = cardinality.getMax();
				
				if (minCardinality != maxCardinality) {
					
					if (minCardinality != Cardinality.UNBOUNDED) {
						
						adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
						adapter.exportTriple(blankNode, OWL.onProperty, Ifc2RdfVocabulary.EXPRESS.slot);
						adapter.exportTriple(blankNode, OWL.minCardinality, getJenaModel().createTypedLiteral(minCardinality));			
		
						adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);
						
					}
					
					if (maxCardinality != Cardinality.UNBOUNDED) {
						
						adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
						adapter.exportTriple(blankNode, OWL.onProperty, Ifc2RdfVocabulary.EXPRESS.slot);
						adapter.exportTriple(blankNode, OWL.maxCardinality, getJenaModel().createTypedLiteral(maxCardinality));			
		
						adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);
						
					}
					
				} else {
					
					if (minCardinality != Cardinality.UNBOUNDED) {				
						adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
						adapter.exportTriple(blankNode, OWL.onProperty, Ifc2RdfVocabulary.EXPRESS.slot);
						adapter.exportTriple(blankNode, OWL.cardinality, getJenaModel().createTypedLiteral(minCardinality));			
		
						adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);
					}					
					
				}
				
				
				adapter.exportEmptyLine();
			
			}

		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithoutItemType);			
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			collectionSuperTypes.put(superTypeName, superCollectionTypeWithoutItemType);

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

	private void writeEntityTypeInfoSection(IfcEntityTypeInfo typeInfo) throws IOException, IfcException {
		
		List<IfcEntityTypeInfo> disjointClasses = null;
		
		Resource typeResource = createUriResource(super.formatTypeName(typeInfo));

		adapter.exportTriple(typeResource, RDF.type, OWL.Class);

		//
		// OWL2 supports owl:disjointUnionOf
		// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F1:_DisjointUnion
		//
		if (typeInfo.isAbstractSuperType() && context.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
			List<IfcEntityTypeInfo> allSubtypeInfos = typeInfo.getSubTypeInfos();
			List<RDFNode> allSubtypeResources = new ArrayList<>(allSubtypeInfos.size());
			for (IfcEntityTypeInfo subTypeInfo : allSubtypeInfos) {
				allSubtypeResources.add(createUriResource(super.formatTypeName(subTypeInfo)));
			}
			adapter.exportTriple(typeResource, OWL2.disjointUnionOf, super.createList(allSubtypeResources));
		}

		//
		// write entity info
		//
		IfcEntityTypeInfo superTypeInfo = typeInfo.getSuperTypeInfo();
		if (superTypeInfo != null) {
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(superTypeInfo)));
			
			if (!superTypeInfo.isAbstractSuperType() || !context.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
				
				List<IfcEntityTypeInfo> allSubtypeInfos = superTypeInfo.getSubTypeInfos();
				
				if (allSubtypeInfos.size() > 1) {
					
					int indexOfCurrentType = allSubtypeInfos.indexOf(typeInfo);
					
					if (allSubtypeInfos.size() > 2 && context.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
						//
						// OWL2 allow object of property "owl:disjointWith" to be rdf:list
						// All classes will be pairwise disjoint
						// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
						//
						disjointClasses = allSubtypeInfos;
						
					} else if (context.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) { // context.getOwlVersion() < OwlProfile.OWL_VERSION_2_0
						
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
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(IFC_ENTITY)));			
		}
		
		//
		// write unique keys
		//
		List<IfcUniqueKeyInfo> uniqueKeyInfos = typeInfo.getUniqueKeyInfos();
		if (uniqueKeyInfos != null && context.supportsRdfProperty(OWL2.hasKey, OwlProfile.RdfTripleObjectTypeEnum.ANY)) {
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
				if (context.supportsRdfProperty(OWL.Restriction, null)) {
					for (IfcAttributeInfo attributeInfo : attributeInfos) {
						// TODO: Write attribute infos
//						Resource attributeResource = createUriResource(super.formatAttributeName(attributeInfo));
//						Cardinality attributeCardinality = attributeInfo.getCardinality();
//						
//						//
//						// write constraint about property type
//						//
//						writeAttributeConstraint(typeResource, attributeResource, OWL.allValuesFrom,
//								createUriResource(super.formatTypeName(attributeInfo.getAttributeTypeInfo())));
//						
//						if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.PrintPropertyCardinality)) {
//							//
//							// write constraint about property cardinality
//							//
//							int min = attributeInfo.isOptional() ? 0 : attributeCardinality.getMin();
//							int max = attributeCardinality.getMax();
//							boolean supportPropertyCardinalityConstraintsGreaterThanOne = 
//									context.supportsRdfProperty(OWL.cardinality, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany));
//							
//							if (min == max) {
//								if (min <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne) { 
//									writeAttributeConstraint(typeResource, attributeResource, OWL.cardinality,
//											getJenaModel().createTypedLiteral(min));
//								}
//							} else {
//								if (min <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne) { 
//									writeAttributeConstraint(typeResource, attributeResource, OWL.minCardinality,
//											getJenaModel().createTypedLiteral(min));
//								}
//								
//								if (max != Cardinality.UNBOUNDED &&
//									(max <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne)) {
//									writeAttributeConstraint(typeResource, attributeResource, OWL.maxCardinality,
//											getJenaModel().createTypedLiteral(max));
//								}
//							}						
//						}
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
	
	private void writeAttributeConstraint(Resource typeResource, Resource attributeResource, Property constraintKindProperty, RDFNode constraintValueTypeResource) {		
		Resource blankNode = super.createAnonResource();
		adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
		adapter.exportTriple(blankNode, OWL.onProperty, attributeResource);
		adapter.exportTriple(blankNode, constraintKindProperty, constraintValueTypeResource);
		adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);		
	}

	private void writeAttributeInfo(String attributeName, List<IfcAttributeInfo> attributeInfoList) {

		Set<String> domainTypeNames = new TreeSet<>();
		Set<String> rangeTypeNames = new TreeSet<>();
		EnumSet<IfcTypeEnum> valueTypes = EnumSet.noneOf(IfcTypeEnum.class);
		boolean isFunctionalProperty = true;
		boolean isInverseFunctionalProperty = true;
		
		if (!context.supportsRdfProperty(OWL.FunctionalProperty, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
			isFunctionalProperty = false;
			isInverseFunctionalProperty = false;
		}
		
		for (IfcAttributeInfo attributeInfo : attributeInfoList) {
			IfcEntityTypeInfo domainTypeInfo = attributeInfo.getEntityTypeInfo();
			domainTypeNames.add(super.formatTypeName(domainTypeInfo));
			
			IfcTypeInfo rangeTypeInfo = attributeInfo.getAttributeTypeInfo(); 
			rangeTypeNames.add(super.formatTypeName(rangeTypeInfo));
			valueTypes.addAll(rangeTypeInfo.getValueTypes());
			
			isFunctionalProperty = isFunctionalProperty && attributeInfo.isFunctional();
			isInverseFunctionalProperty = isInverseFunctionalProperty && attributeInfo.isInverseFunctional();			
		}
		
		//
		// owl:DataProperty, owl:ObjectProperty, or rdf:Property
		//
		Resource attributeResource = createUriResource(super.formatOntologyName(attributeName));
		if (valueTypes.size() == 1 && !valueTypes.contains(IfcTypeEnum.ENTITY)) {
			adapter.exportTriple(attributeResource, RDF.type, OWL.DatatypeProperty);
		} else {
			adapter.exportTriple(attributeResource, RDF.type, OWL.ObjectProperty);
		}

		//
		// owl:FunctionalProperty
		//
		if (isFunctionalProperty) {
			adapter.exportTriple(attributeResource, RDF.type, OWL.FunctionalProperty);			
		}
		
		//
		// owl:InverseFunctionalProperty
		//
		if (isInverseFunctionalProperty) {
			adapter.exportTriple(attributeResource, RDF.type, OWL.InverseFunctionalProperty);
		}
		
		//
		// rdfs:domain, rdfs:range
		//
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.PrintPropertyDomainAndRange)) {
		
			if (domainTypeNames.size() == 1) {
				adapter.exportTriple(attributeResource, RDFS.domain, createUriResource(domainTypeNames.iterator().next()));
			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
				//
				// In OWL Lite the value of rdfs:domain must be a class identifier. 
				// See: http://www.w3.org/TR/owl-ref/#domain-def
				//
				
				List<RDFNode> domainTypeResources = new ArrayList<>();
				for (String domainTypeName : domainTypeNames) {
					domainTypeResources.add(createUriResource(domainTypeName));
				}				
				
				Resource domainTypeResource = super.createAnonResource();
				adapter.exportTriple(domainTypeResource, RDF.type, OWL.Class);
				adapter.exportTriple(domainTypeResource, OWL.unionOf, createList(domainTypeResources));	
				adapter.exportTriple(attributeResource, RDFS.domain, domainTypeResource);			
			}
	
			//
			// Caution: rdfs:range should be used with care
			// See: http://www.w3.org/TR/owl-ref/#range-def
			//
			if (rangeTypeNames.size() == 1) {
				adapter.exportTriple(attributeResource, RDFS.range, createUriResource(rangeTypeNames.iterator().next()));
			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
				//
				// In OWL Lite the only type of class descriptions allowed as objects of rdfs:range are class names. 
				// See: http://www.w3.org/TR/owl-ref/#range-def
				//				
				List<RDFNode> rangeTypeResources = new ArrayList<>();
				for (String rangeTypeName : rangeTypeNames) {
					rangeTypeResources.add(createUriResource(rangeTypeName));
				}				
				
				Resource rangeTypeResource = super.createAnonResource();
				adapter.exportTriple(rangeTypeResource, RDF.type, OWL.Class);
				adapter.exportTriple(rangeTypeResource, OWL.unionOf, createList(rangeTypeResources));	
				adapter.exportTriple(attributeResource, RDFS.range, rangeTypeResource);				
			}
			
		}
		
		if (attributeInfoList.size() == 1) {
			//
			// write owl:inverseOf
			// See: http://www.w3.org/TR/owl-ref/#inverseOf-def
			//
			if (attributeInfoList.get(0) instanceof IfcInverseLinkInfo && context.supportsRdfProperty(OWL.inverseOf, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
				IfcInverseLinkInfo inverseLinkInfo = (IfcInverseLinkInfo)attributeInfoList.get(0);
				adapter.exportTriple(attributeResource, OWL.inverseOf, createUriResource(super.formatAttributeName(inverseLinkInfo.getOutgoingLinkInfo())));
			}
		} else {
			if (avoidDuplicationOfPropertyNames) {
				for (IfcAttributeInfo subAttributeInfo : attributeInfoList) {
					adapter.exportEmptyLine();
					List<IfcAttributeInfo> attributeInfoSubList = new ArrayList<>();
					attributeInfoSubList.add(subAttributeInfo);
					String subAttributeName = subAttributeInfo.getUniqueName();
					writeAttributeInfo(subAttributeName, attributeInfoSubList);
					adapter.exportTriple(createUriResource(super.formatOntologyName(subAttributeName)), RDFS.subPropertyOf, attributeResource);
				}
			}
		}
	}
	
	private RDFList getCardinalityValueList(int min, int max) {
		List<Literal> literals = new ArrayList<>();
		for (int i = min; i <= max; ++i) {
			literals.add(getJenaModel().createTypedLiteral(i));
		}
		return super.createList(literals);
	}	
	
	
	
}