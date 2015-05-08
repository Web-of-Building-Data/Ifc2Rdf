package fi.hut.cs.drumbeat.rdf.export;

import java.util.Date;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class JenaModelExportAdapter implements RdfExportAdapter {
	
	private Model jenaModel;

	public JenaModelExportAdapter(Model jenaModel) {
		this.jenaModel = jenaModel;
	}

	@Override
	public Model getInternalJenaModel() {
		return jenaModel;
	}	
	
	@Override
	public void exportOntologyHeader(String uri, String version, String comment) {
		Resource ontology = jenaModel.createResource(uri);
		ontology.addProperty(RDF.type, OWL.Ontology);
		ontology.addProperty(OWL.versionInfo, String.format("v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS", version, new Date()));
		if (comment != null) {
			//ontology.addProperty(RDFS.comment, String.format("\"\"\"%s\"\"\"", comment));
			ontology.addProperty(RDFS.comment, comment);
		}
	}

	@Override
	public void setNamespacePrefix(String prefix, String uri) {
		jenaModel.setNsPrefix(prefix, uri);
	}

	@Override
	public void startExport() {
	}

	@Override
	public void exportTriple(Resource subject, Property predicate, RDFNode object) {
		jenaModel.add(subject, predicate, object);
	}

	@Override
	public void endExport() {
	}

	@Override
	public void exportEmptyLine() {
	}

	@Override
	public void startSection(String string) {
	}

	@Override
	public void endSection() {
	}

}
