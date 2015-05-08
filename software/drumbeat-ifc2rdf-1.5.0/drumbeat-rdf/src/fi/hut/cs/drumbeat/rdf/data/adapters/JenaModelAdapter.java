package fi.hut.cs.drumbeat.rdf.data.adapters;

import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.hut.cs.drumbeat.rdf.data.IRdfModel;


public class JenaModelAdapter implements IRdfModel {
	
	private Model jenaModel;

	public JenaModelAdapter(Model jenaModel) {
		this.jenaModel = jenaModel;
	}

	@Override
	public void add(Resource subject, Property predicate, RDFNode object) {
		jenaModel.add(subject, predicate, object);
	}

	@Override
	public void remove(Resource subject, Property predicate, RDFNode object) {
		jenaModel.remove(subject, predicate, object);
	}

	@Override
	public void removeAll(Resource subject, Property predicate, RDFNode object) {
		jenaModel.removeAll(subject, predicate, object);
	}

	@Override
	public Iterator<Statement> getIncomingStatements(RDFNode object) {
		return jenaModel.listStatements(null, null, object);
	}
	
	@Override
	public Iterator<Statement> getOutgoingStatements(Resource subject) {
		return jenaModel.listStatements(subject, null, (RDFNode)null);
	}
	
	@Override
	public Iterator<Statement> getAllStatements() {
		return jenaModel.listStatements();
	}
	

	@Override
	public Iterator<Resource> getAllSubjects() {
		return jenaModel.listSubjects();
	}
	
	@Override
	public Resource getType(Resource node) {
		return node.getPropertyResourceValue(RDF.type);
	}
	
}
