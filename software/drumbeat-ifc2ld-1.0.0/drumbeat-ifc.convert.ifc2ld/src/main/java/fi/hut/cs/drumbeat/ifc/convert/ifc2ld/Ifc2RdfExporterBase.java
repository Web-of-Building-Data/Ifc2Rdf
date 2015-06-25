package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEnumerationTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLiteralTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLogicalTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


/**
 * 
 * @author vuhoan1
 *
 */
public abstract class Ifc2RdfExporterBase {
	
	RdfExportAdapter rdfExportAdapter;
	private Model jenaModel;
	private Ifc2RdfConversionContext context;
//	private String ontologyNamespacePrefix;
	private String ontologyNamespaceUri;
	private String modelNamespacePrefix;
	private String modelNamespaceUri;
	private Resource typeForDoubleValues;

	protected Ifc2RdfExporterBase(Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		this.rdfExportAdapter = rdfExportAdapter;
		this.jenaModel = rdfExportAdapter.getInternalJenaModel();
		this.context = context;
	}
	
	protected Model getJenaModel() {
		return jenaModel;
	}
	
//	protected void setOntologyNamespacePrefix(String prefix) {
//		ontologyNamespacePrefix = prefix;
//	}
//	
//	protected String getOntologyNamespacePrefix() {
//		return ontologyNamespacePrefix;
//	}

	protected String getOntologyNamespaceUri() {
		return ontologyNamespaceUri;
	}
	
	protected void setOntologyNamespaceUri(String uri) {
		ontologyNamespaceUri = uri;
	}
	
	protected void setModelNamespacePrefix(String prefix) {
		modelNamespacePrefix = prefix;
	}
	
	protected String getModelNamespacePrefix() {
		return modelNamespacePrefix;
	}

	protected String getModelNamespaceUri() {
		return modelNamespaceUri;
	}
	
	protected void setModelNamespaceUri(String uri) {
		modelNamespaceUri = uri;
	}
	
	protected Ifc2RdfConversionContext getContext() {
		return context;
	}
	
	protected Resource createUriResource(String uri) {
		return jenaModel.createResource(uri);
	}	
	
	protected Resource createAnonResource() {
		return jenaModel.createResource();
	}
	
	protected Resource createAnonResource(String anonId) {
		return jenaModel.createResource(new AnonId(anonId));
	}
	
	protected RDFList createList(Collection<? extends RDFNode> resources) {
		return jenaModel.createList(resources.iterator());
	}
	
	protected Resource createOntologyResource(String uri, String version, String comment) {
		
		Resource ontology = jenaModel.createResource(uri);
		ontology.addProperty(RDF.type, OWL.Ontology);
		ontology.addProperty(OWL.versionInfo, String.format("\"v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS\"", version, new Date()));
		if (comment != null) {
			ontology.addProperty(RDFS.comment, String.format("\"\"\"%s\"\"\"", comment));
		}
		return ontology;
		
	}
	
	public Resource getXsdDataType(IfcLiteralTypeInfo literalTypeInfo) {
		switch (literalTypeInfo.getName()) {
		
		case IfcVocabulary.TypeNames.BINARY:			
//			return XSD.nonNegativeInteger;
			return XSD.hexBinary;
			
		case IfcVocabulary.TypeNames.INTEGER:
			return XSD.integer;
			
		case IfcVocabulary.TypeNames.BOOLEAN:
		case IfcVocabulary.TypeNames.LOGICAL:
			return context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertBooleanValuesToString) ?
					XSD.xstring : XSD.xboolean;
			
		case IfcVocabulary.TypeNames.NUMBER:
		case IfcVocabulary.TypeNames.REAL:			
			return XSD.decimal;
//			return XSD.xdouble;			
			
		case IfcVocabulary.TypeNames.DATETIME:
			return XSD.dateTime;
			
		// TODO: Remove GUID
		case IfcVocabulary.TypeNames.GUID:
//			return XSD.NMTOKEN;
			return XSD.xstring;
			
		case IfcVocabulary.TypeNames.STRING:
		default:
			return XSD.xstring;
			
		}
	}
	
	protected static Property getHasProperty(IfcTypeInfo baseTypeInfo) {
		IfcTypeEnum valueType = baseTypeInfo.getValueTypes().iterator().next();
		String valueTypeName = valueType.toString();			
		String propertyName = String.format("has%s%s", valueTypeName.substring(0, 1).toUpperCase(), valueTypeName.substring(1).toLowerCase());
		return RdfVocabulary.DEFAULT_MODEL.createProperty(Ifc2RdfVocabulary.EXPRESS.getBaseUri() + propertyName);
	}
	
	protected Resource getXsdTypeForDoubleValues() {
		
		if (typeForDoubleValues == null) {

			List<Resource> types = new ArrayList<>();
		
			if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.UseSpecificDoubleTypes)) {
				types.add(XSD.xdouble);
				types.add(RdfVocabulary.OWL.real);
			}
			types.add(XSD.decimal);
			
			typeForDoubleValues = context.getOwlProfileList().getFirstSupportedType(types);
		}
		return typeForDoubleValues;
	}
	
	public Resource convertLiteralToNode(IfcLiteralValue literalValue) {
		
		Resource resource = createAnonResource();

		IfcTypeInfo type = literalValue.getType();
		assert(type != null) : literalValue;
		if (type instanceof IfcDefinedTypeInfo || type instanceof IfcLiteralTypeInfo) {
			
			RDFNode valueNode;			
			IfcTypeEnum valueType = type.getValueTypes().iterator().next();
			if (valueType == IfcTypeEnum.STRING) {
				valueNode = jenaModel.createTypedLiteral((String)literalValue.getValue());				
			} else if (valueType == IfcTypeEnum.GUID) {				
				valueNode = jenaModel.createTypedLiteral(literalValue.getValue().toString());
			} else if (valueType == IfcTypeEnum.REAL) {				
				valueNode = jenaModel.createTypedLiteral(literalValue.getValue(), getXsdTypeForDoubleValues().getURI());				
			} else if (valueType == IfcTypeEnum.NUMBER) {				
				valueNode = jenaModel.createTypedLiteral((double)literalValue.getValue());				
			} else if (valueType == IfcTypeEnum.INTEGER) {				
				valueNode = jenaModel.createTypedLiteral((long)literalValue.getValue());				
			} else if (valueType == IfcTypeEnum.LOGICAL) {				
				valueNode = createUriResource(formatOntologyName((String)literalValue.getValue()));				
			} else {
				assert (valueType == IfcTypeEnum.DATETIME) : "Expected: valueType == IfcTypeEnum.DATETIME. Actual: valueType = " + valueType + ", " + type;
				valueNode = jenaModel.createTypedLiteral((Calendar)literalValue.getValue());				
			}

			rdfExportAdapter.exportTriple(resource, RDF.type, createUriResource(formatTypeName(type)));
			
			Property property = getHasProperty(type);			
			rdfExportAdapter.exportTriple(resource, property, valueNode);			
			
		} else if (type instanceof IfcEnumerationTypeInfo) {
			
			resource = createUriResource(formatOntologyName((String)literalValue.getValue()));
			
//			adapter.exportTriple(resource, RDF.type, createUriResource(formatTypeName(type)));
//			adapter.exportTriple(resource, Ifc2RdfVocabulary.EXPRESS.value, createUriResource(formatOntologyName((String)literalValue.getValue())));
			
		} else if (type instanceof IfcLogicalTypeInfo) {
			
			resource = createUriResource(formatOntologyName((String)literalValue.getValue()));
			
//			adapter.exportTriple(resource, RDF.type, createUriResource(formatExpressOntologyName(type.getName())));
//			adapter.exportTriple(resource, Ifc2RdfVocabulary.EXPRESS.value, createUriResource(formatExpressOntologyName((String)literalValue.getValue())));
			
		} else {			
			throw new RuntimeException(String.format("Invalid literal value type: %s (%s)", type, type.getClass()));			
		}
		
		return resource;			
	}
	
	
	
	protected String formatExpressOntologyName(String name) {
		return Ifc2RdfVocabulary.EXPRESS.getBaseUri() + name;
	}

	protected String formatOntologyName(String name) {
		return ontologyNamespaceUri + name;
	}
	
	protected String formatTypeName(IfcTypeInfo typeInfo) {
		if (typeInfo instanceof IfcLiteralTypeInfo || typeInfo instanceof IfcLogicalTypeInfo) {
			return formatExpressOntologyName(typeInfo.getName());			
		} else {
			return formatOntologyName(typeInfo.getName());
		}
	}
	
	protected String formatAttributeName(IfcAttributeInfo attributeInfo) {
		return formatOntologyName(attributeInfo.getUniqueName()); 
	}
	
	public String formatModelName(String name) {
		return modelNamespaceUri + name;
	}
	
	protected String formatSlotClassName(String className) {
		return className + "_Slot";
	}

}
