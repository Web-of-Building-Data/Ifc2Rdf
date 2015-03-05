package net.linkedbuildingdata.ifc.convert.ifc2rdf;

/**
 * Followed:
 *    [1] OWL template of Jyrki Oraskari (version 11.04.2012)
 *    [2] Allemang, Dean; Hendler, Jim. Semantic Web for the working ontologies: effective modeling in RDFS and OWL. - 2nd ed., 2011   
 */

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import net.linkedbuildingdata.common.string.StringUtils;
import net.linkedbuildingdata.ifc.*;
import net.linkedbuildingdata.ifc.data.Cardinality;
import net.linkedbuildingdata.ifc.data.schema.*;
import net.linkedbuildingdata.rdf.OwlProfile;
import net.linkedbuildingdata.rdf.RdfVocabulary;
import net.linkedbuildingdata.rdf.OwlProfile.RdfTripleObjectTypeEnum;
import net.linkedbuildingdata.rdf.RdfVocabulary.OLO;
import net.linkedbuildingdata.rdf.export.RdfExportAdapter;

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

	private Map<String, List<IfcAttributeInfo>> attributeInfoMap = new HashMap<>();
	private boolean avoidDuplicationOfPropertyNames;
	private Map<Resource, Cardinality> oloCardinalityClasses = new HashMap<>();
	
	private static final IfcEntityTypeInfo IFC_ENTITY = new IfcEntityTypeInfo(null, "ENTITY");
	
	public Ifc2RdfSchemaExporter(IfcSchema ifcSchema, Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		super(context, rdfExportAdapter);
		this.ifcSchema = ifcSchema;
		this.context = context;
		this.adapter = rdfExportAdapter;
		
		super.setOntologyNamespacePrefix(context.getOntologyPrefix());
		
		super.setOntologyNamespaceUri(
				String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion()));
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

		// literal types
		adapter.startSection("SIMPLE TYPES");
		for (IfcDefinedTypeInfo definedTypeInfo : definedTypeInfos) {
			if (definedTypeInfo instanceof IfcLiteralTypeInfo) {
				writeLiteralTypeInfo((IfcLiteralTypeInfo)definedTypeInfo);
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
			if (definedTypeInfo instanceof IfcListTypeInfo) {
				writeListTypeInfo((IfcListTypeInfo)definedTypeInfo);
				adapter.exportEmptyLine();
			} 
		}
		
		writeAdditionalListTypeInfos();		
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
	
	private void exportEnumerationTypeInfo(IfcEnumerationTypeInfo typeInfo) {
		
		String typeUri = super.formatTypeName(typeInfo);
		Resource typeResource = createUriResource(typeUri);

		if (!context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertEnumerationValuesToString)) {
			adapter.exportTriple(typeResource, RDF.type, OWL.Class);
			List<Resource> nodes = new ArrayList<>();
			List<String> enumValues = typeInfo.getValues(); 
			for (String value : enumValues) {
				nodes.add(super.createUriResource(super.formatOntologyName(value)));
			}
			RDFList rdfList = super.createList(nodes);			
			adapter.exportTriple(typeResource, OWL.oneOf, rdfList);
		} else {
			adapter.exportTriple(typeResource, RDF.type, RDFS.Datatype);
			adapter.exportTriple(typeResource, RDFS.subClassOf, XSD.xstring);
			
//			List<String> values = typeInfo.getValues();
//			String valueString = StringUtils.collectionToString(values, "(", ")", String.format("\"%%s\"^^%s", RdfVocabulary.XSD_STRING.getShortForm()), " "); 
//			adapter.writeRdfTriple(ExportAdapter.CURRENT_SUBJECT, OWL.oneOf, valueString);			
		}
		
	}

	private void exportSelectTypeInfo(IfcSelectTypeInfo typeInfo) {

		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);

		List<String> subTypeNames = typeInfo.getSelectTypeInfoNames();
		List<Resource> nodes = new ArrayList<>();
		for (String typeName : subTypeNames) {
			nodes.add(super.createUriResource(super.formatOntologyName(typeName)));
		}			
		RDFList rdfList = super.createList(nodes);			

		// See samples: [2, p.250]
		adapter.exportTriple(typeResource, OWL.unionOf, rdfList);
		
//		See samples: [2, pp.135-136]
//		for (String subTypeName : subTypeNames) {
//			writeSentence(generateName(subTypeName), RDFS.subClassOf, typeName);
//		}

	}
	
	private void writeLiteralTypeInfo(IfcLiteralTypeInfo typeInfo) {
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, RDFS.Datatype);
		adapter.exportTriple(typeResource, RDFS.subClassOf, super.getXsdDataType(typeInfo));
	}
	
	private void writeRedirectTypeInfo(IfcRedirectTypeInfo typeInfo) {
		
		EnumSet<IfcTypeEnum> valueTypes = typeInfo.getValueTypes();
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		if (IfcTypeEnum.LITERAL.containsAll(valueTypes)) {
			adapter.exportTriple(typeResource, RDF.type, RDFS.Datatype);			
		} else {
			adapter.exportTriple(typeResource, RDF.type, OWL.Class);			
		}

		adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(typeInfo.getRedirectTypeInfo())));
	}

	private void writeListTypeInfo(IfcListTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		adapter.exportTriple(typeResource, RDF.type, OWL.Class);
		
		Cardinality cardinality = typeInfo.getCardinality();
		
		IfcListTypeInfo superTypeInfo = typeInfo.getSuperTypeInfo();
		if (superTypeInfo != null) {
			adapter.exportTriple(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(superTypeInfo)));
		}
		
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
			
			Resource oloSlotClassResource = null;
			
			if (superTypeInfo == null) {

				adapter.exportTriple(typeResource, RDFS.subClassOf, OLO.OrderedList);

				//
				// write restriction on type of property olo:slot
				//
				Resource blankNode = super.createAnonResource();
				adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
				adapter.exportTriple(blankNode, OWL.onProperty, OLO.slot);
				oloSlotClassResource = createUriResource(super.formatOntologyName(super.formatSlotClassName(typeInfo.getName())));
				adapter.exportTriple(blankNode, OWL.allValuesFrom, oloSlotClassResource);			

				adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);				
			}
			
			if (cardinality != null) {
				Resource oloCardinalityClassUri = createUriResource(
						super.formatOntologyName(IfcListTypeInfo.formatListTypeName("OloOrderList_Cardinality", cardinality)));
				adapter.exportTriple(typeResource, RDFS.subClassOf, oloCardinalityClassUri);
				oloCardinalityClasses.put(oloCardinalityClassUri, cardinality);
			}

			if (oloSlotClassResource != null) {
				
				adapter.exportEmptyLine();
				
				Resource oloItemClassNode = super.createAnonResource();
				adapter.exportTriple(oloItemClassNode, RDF.type, OWL.Restriction);
				adapter.exportTriple(oloItemClassNode, OWL.onProperty, OLO.item);
				adapter.exportTriple(oloItemClassNode, OWL.allValuesFrom, createUriResource(super.formatTypeName(typeInfo.getItemTypeInfo())));
	
				adapter.exportTriple(oloSlotClassResource, RDF.type, OWL.Class);
				adapter.exportTriple(oloSlotClassResource, RDFS.subClassOf, OLO.Slot);
				adapter.exportTriple(oloSlotClassResource, RDFS.subClassOf, oloItemClassNode);
				
			}
				
		} else { // !context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ConvertRdfListToOloOrderedList)
			
			if (superTypeInfo == null) {
			
				//
				// See sample: https://answers.semanticweb.com/questions/17321/typed-list-definition-in-owl
				//
				adapter.exportTriple(typeResource, RDFS.subClassOf, RDF.List);			
	
				if (context.supportsRdfProperty(OWL.Restriction, null)) {
					// write restriction on type of property rdf:first
					Resource blankNode = super.createAnonResource();
					adapter.exportTriple(blankNode, RDF.type, OWL.Restriction);
					adapter.exportTriple(blankNode, OWL.onProperty, RDF.first);
					adapter.exportTriple(blankNode, OWL.allValuesFrom, createUriResource(super.formatTypeName(typeInfo.getItemTypeInfo())));			
					adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);
					
					if (context.supportsRdfProperty(OWL.unionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
						// write restriction on type of property rdf:rest
						Resource blankNodeResource = super.createAnonResource();
						adapter.exportTriple(blankNodeResource, RDF.type, OWL.Restriction);
						adapter.exportTriple(blankNodeResource, OWL.onProperty, RDF.rest);
						
						Resource blankNodeResource1 = super.createAnonResource();
						adapter.exportTriple(blankNodeResource1, RDF.type, OWL.Class);
						
						List<RDFNode> typeNames = new ArrayList<>();
						typeNames.add(createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.EMPTY_LIST)));
						typeNames.add(createUriResource(typeInfo.getName()));
						adapter.exportTriple(blankNodeResource1, OWL.unionOf, super.createList(typeNames));
		
						adapter.exportTriple(blankNodeResource, OWL.allValuesFrom, blankNodeResource1);
		
						adapter.exportTriple(typeResource, RDFS.subClassOf, blankNode);			
					}
				}
				
			}
			
		}

	}

	private void writeAdditionalListTypeInfos() {
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
			for (Entry<Resource, Cardinality> entry : oloCardinalityClasses.entrySet()) {
				Resource oloCardinalityClassResource = entry.getKey();
				Cardinality cardinality = entry.getValue();
				
				adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Class);
				
				if (cardinality != null && context.supportsRdfProperty(OWL.oneOf, OwlProfile.RdfTripleObjectTypeEnum.DataList)) {
					
					int min = cardinality.getMin();
					int max = cardinality.getMax();
				
					if (max != Cardinality.UNBOUNDED) {
						//
						// write restriction on length of list as [ owl:oneOf (min, min + 1, ..., max) ]
						//
						Resource blankNode1 = super.createAnonResource();
						adapter.exportTriple(blankNode1, RDF.type, RDFS.Datatype);
						adapter.exportTriple(blankNode1, OWL.oneOf, getCardinalityValueList(min, max));
	
						adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Restriction);
						adapter.exportTriple(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);		
						adapter.exportTriple(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
						
					} else if (min != Cardinality.ZERO && context.supportsRdfProperty(OWL.complementOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
						
						//
						// write restriction on length of list as [ owl:compelmentOf [ owl:oneOf (0, 1, ..., min - 1) ]
						//
						Resource blankNode2 = super.createAnonResource();
						adapter.exportTriple(blankNode2, RDF.type, RDFS.Datatype);
						adapter.exportTriple(blankNode2, OWL.oneOf, getCardinalityValueList(Cardinality.ZERO, min - 1));
	
						Resource blankNode1 = super.createAnonResource();
						adapter.exportTriple(blankNode1, RDF.type, OWL.Class);
						adapter.exportTriple(blankNode1, OWL.complementOf, blankNode2); 
	
						adapter.exportTriple(oloCardinalityClassResource, RDF.type, OWL.Restriction);
						adapter.exportTriple(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);
						adapter.exportTriple(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
						
					}
					
					adapter.exportEmptyLine();					
					
				} // for
				
			}
		} else { // !context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ConvertRdfListToOloOrderedList)

			// define ifc:EmptyList
			Resource emptyListTypeUri = createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.EMPTY_LIST)); 
			adapter.exportTriple(emptyListTypeUri, RDF.type, OWL.Class);
			adapter.exportTriple(emptyListTypeUri, RDFS.subClassOf, RDF.List);
			adapter.exportEmptyLine();

		}
	}

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
						Resource attributeResource = createUriResource(super.formatAttributeName(attributeInfo));
						Cardinality attributeCardinality = attributeInfo.getCardinality();
						
						//
						// write constraint about property type
						//
						writeAttributeConstraint(typeResource, attributeResource, OWL.allValuesFrom,
								createUriResource(super.formatTypeName(attributeInfo.getAttributeTypeInfo())));
						
						if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.PrintPropertyCardinality)) {
							//
							// write constraint about property cardinality
							//
							int min = attributeInfo.isOptional() ? 0 : attributeCardinality.getMin();
							int max = attributeCardinality.getMax();
							boolean supportPropertyCardinalityConstraintsGreaterThanOne = 
									context.supportsRdfProperty(OWL.cardinality, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany));
							
							if (min == max) {
								if (min <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne) { 
									writeAttributeConstraint(typeResource, attributeResource, OWL.cardinality,
											getJenaModel().createTypedLiteral(min));
								}
							} else {
								if (min <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne) { 
									writeAttributeConstraint(typeResource, attributeResource, OWL.minCardinality,
											getJenaModel().createTypedLiteral(min));
								}
								
								if (max != Cardinality.UNBOUNDED &&
									(max <= 1 || supportPropertyCardinalityConstraintsGreaterThanOne)) {
									writeAttributeConstraint(typeResource, attributeResource, OWL.maxCardinality,
											getJenaModel().createTypedLiteral(max));
								}
							}						
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