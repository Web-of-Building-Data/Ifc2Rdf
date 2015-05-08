package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcMetaModel;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcStepFileDescription;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcStepFileName;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


public class Ifc2RdfMetaModelExporter extends Ifc2RdfExporterBase {
	
	public static final String STEP_PREFIX = "step"; 
	public static final String STEP_URI = "http://drumbeat.cs.hut.fi/owl/step#"; 

	private String metaDataSetUri;
	private IfcSchema ifcSchema;
//	private IfcModel ifcModel;
	private IfcMetaModel metaModel;
	private Model jenaModel;
	
	private Ifc2RdfConversionContext context;
	private RdfExportAdapter adapter;
	
	public Ifc2RdfMetaModelExporter(String metaDataSetUri, IfcModel ifcModel, Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		super(context, rdfExportAdapter);
		
		this.metaDataSetUri = metaDataSetUri;
//		this.ifcModel = ifcModel;
		this.metaModel = ifcModel.getMetaModel();
		this.ifcSchema = ifcModel.getSchema();
		this.context = context;
		this.jenaModel = getJenaModel();
		adapter = rdfExportAdapter;
		
		String ontologyNamespacePrefix = context.getOntologyPrefix();
		String ontologyNamespaceUri = String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName());

		String modelNamespacePrefix = context.getModelPrefix();
		String modelNamespaceUri = String.format(context.getModelNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		
		super.setOntologyNamespacePrefix(ontologyNamespacePrefix);
		super.setOntologyNamespaceUri(ontologyNamespaceUri);
		super.setModelNamespacePrefix(modelNamespacePrefix);
		super.setModelNamespaceUri(modelNamespaceUri);		
	}
	
	public Model export() throws IfcException {
		
		//
		// write header and prefixes
		//
		adapter.startExport();		
		
		adapter.setNamespacePrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());	
		adapter.setNamespacePrefix(RdfVocabulary.VOID.BASE_PREFIX, RdfVocabulary.VOID.BASE_URI);
		adapter.setNamespacePrefix(RdfVocabulary.DCTERMS.BASE_PREFIX, RdfVocabulary.DCTERMS.BASE_URI);
		adapter.setNamespacePrefix(STEP_PREFIX, STEP_URI);
		
		
		adapter.setNamespacePrefix(getOntologyNamespacePrefix(), getOntologyNamespaceUri());
		adapter.exportEmptyLine();
		
		Resource dataSetResource = super.createUriResource(metaDataSetUri);

		adapter.setNamespacePrefix(getModelNamespacePrefix(), getModelNamespaceUri());
		adapter.exportEmptyLine();

		adapter.exportTriple(dataSetResource, RDF.type, RdfVocabulary.VOID.DataSet);

		String conversionOptionsString = context.getConversionOptions().toString();
//				.replaceFirst("\\[", "[\r\n\t\t\t ")
//				.replaceFirst("\\]", "\r\n\t\t]")
//				.replaceAll(",", "\r\n\t\t\t");		
		conversionOptionsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				context.getOwlProfileId(),
				conversionOptionsString); 
		adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.description, jenaModel.createTypedLiteral(conversionOptionsString));		

		IfcStepFileDescription stepFileDescription = metaModel.getFileDescription();
		stepFileDescription.getDescriptions().forEach(x ->
			adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.description, jenaModel.createTypedLiteral(x))
		);
		
		IfcStepFileName stepFileName = metaModel.getFileName();		
		adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.title, jenaModel.createTypedLiteral(stepFileName.getName()));		

		stepFileName.getAuthors().forEach(x ->
			adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.creator, jenaModel.createTypedLiteral(x))
		);
		
		stepFileName.getOrganizations().forEach(x ->
			adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.publisher, jenaModel.createTypedLiteral(x))
		);
		
		adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.created, jenaModel.createTypedLiteral(stepFileName.getTimeStamp()));	
		
		
		adapter.exportTriple(dataSetResource, RdfVocabulary.DCTERMS.hasVersion, jenaModel.createTypedLiteral(stepFileName.getPreprocessorVersion(), XSD.date.toString()));
		
		Resource fileDescriptionResource = jenaModel.createResource();
		Resource fileNameResource = jenaModel.createResource();
		Resource fileSchemaResource = jenaModel.createResource();
		
		adapter.exportTriple(dataSetResource, createProperty(STEP_URI + "fileDescription"), fileDescriptionResource);
		adapter.exportTriple(dataSetResource, createProperty(STEP_URI + "fileName"), fileNameResource);
		adapter.exportTriple(dataSetResource, createProperty(STEP_URI + "fileSchema"), fileSchemaResource);
		
		stepFileDescription.getDescriptions().forEach(x ->
			adapter.exportTriple(fileDescriptionResource, createProperty(STEP_URI + "description"), jenaModel.createTypedLiteral(x))
		);
		adapter.exportTriple(fileDescriptionResource, createProperty(STEP_URI + "implementation_level"), jenaModel.createTypedLiteral(stepFileDescription.getImplementationLevel()));
		
		adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "name"), jenaModel.createTypedLiteral(stepFileName.getName()));
		adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "time_stamp"), jenaModel.createTypedLiteral(stepFileName.getTimeStamp()));
		stepFileName.getAuthors().forEach(x ->
			adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "author"), jenaModel.createTypedLiteral(x))
		);

		stepFileName.getOrganizations().forEach(x ->
			adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "organization"), jenaModel.createTypedLiteral(x))
		);
		
		adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "preprocessor_version"), jenaModel.createTypedLiteral(stepFileName.getPreprocessorVersion()));
		adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "originating_system"), jenaModel.createTypedLiteral(stepFileName.getOriginatingSystem()));
		adapter.exportTriple(fileNameResource, createProperty(STEP_URI + "authorization"), jenaModel.createTypedLiteral(stepFileName.getAuthorization()));
		
		metaModel.getFileSchema().getSchemas().forEach(x ->
			adapter.exportTriple(fileSchemaResource, createProperty(STEP_URI + "schema_identifiers"), jenaModel.createTypedLiteral(x))
		);
		
		//		IfcEntity ownerHistory = ifcModel.getFirstEntityByType(IfcVocabulary.TypeNames.IFC_OWNER_HISTORY);
//		if (ownerHistory != null) {
//			
//		}
		
		
		
		adapter.endExport();
		
		return jenaModel;
	}
	
	private static Property createProperty(String uri) {
		return RdfVocabulary.DEFAULT_MODEL.createProperty(uri);
	}
	
}
