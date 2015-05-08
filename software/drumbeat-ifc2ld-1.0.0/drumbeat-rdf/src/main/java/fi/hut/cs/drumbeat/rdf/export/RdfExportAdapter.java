package fi.hut.cs.drumbeat.rdf.export;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public interface RdfExportAdapter {
	
	Model getInternalJenaModel();
	
	void exportOntologyHeader(String uri, String version, String comment);
	
	void setNamespacePrefix(String prefix, String uri);

	void startExport();

	void endExport();

	void exportEmptyLine();

	void startSection(String string);

	void endSection();

	void exportTriple(Resource subject, Property property, RDFNode object);

}
