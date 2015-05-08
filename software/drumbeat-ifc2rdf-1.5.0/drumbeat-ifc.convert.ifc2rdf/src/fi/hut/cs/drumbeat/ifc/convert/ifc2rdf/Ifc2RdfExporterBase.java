package fi.hut.cs.drumbeat.ifc.convert.ifc2rdf;

import java.util.Collection;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLiteralTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


public abstract class Ifc2RdfExporterBase {
	
	private Model jenaModel;
	private Ifc2RdfConversionContext context;
	private String ontologyNamespacePrefix;
	private String ontologyNamespaceUri;
	private String modelNamespacePrefix;
	private String modelNamespaceUri;

	protected Ifc2RdfExporterBase(Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		this.jenaModel = rdfExportAdapter.getInternalJenaModel();
		this.context = context;
	}
	
	protected Model getJenaModel() {
		return jenaModel;
	}
	
	protected void setOntologyNamespacePrefix(String prefix) {
		ontologyNamespacePrefix = prefix;
	}
	
	protected String getOntologyNamespacePrefix() {
		return ontologyNamespacePrefix;
	}

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
		
		case IfcVocabulary.TypeNames.BINARY32:
			return XSD.nonNegativeInteger;
			
		case IfcVocabulary.TypeNames.INTEGER:
			return XSD.integer;
			
		case IfcVocabulary.TypeNames.BOOLEAN:
		case IfcVocabulary.TypeNames.LOGICAL:
			return context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertBooleanValuesToString) ?
					XSD.xstring : XSD.xboolean;
			
		case IfcVocabulary.TypeNames.NUMBER:
		case IfcVocabulary.TypeNames.REAL:
			return XSD.decimal;
			
		case IfcVocabulary.TypeNames.DATETIME:
			return XSD.dateTime;
			
		case IfcVocabulary.TypeNames.GUID:
			return XSD.NMTOKEN;
			
		case IfcVocabulary.TypeNames.STRING:
		default:
			return XSD.xstring;
			
		}
	}
	
	protected String formatOntologyName(String name) {
		return ontologyNamespaceUri + name;
	}
	
	protected String formatTypeName(IfcTypeInfo typeInfo) {
		return ontologyNamespaceUri + typeInfo.getName();
	}
	
	protected String formatAttributeName(IfcAttributeInfo attributeInfo) {
		return ontologyNamespaceUri + attributeInfo.getUniqueName();
	}
	
	public String formatModelName(String name) {
		return modelNamespaceUri + name;
	}
	
	protected String formatSlotClassName(String className) {
		return className + "_Slot";
	}

}
