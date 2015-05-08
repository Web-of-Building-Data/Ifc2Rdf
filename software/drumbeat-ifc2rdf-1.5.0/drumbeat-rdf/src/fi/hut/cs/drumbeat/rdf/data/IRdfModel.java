package fi.hut.cs.drumbeat.rdf.data;

import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public interface IRdfModel {
	
	void add(Resource subject, Property predicate, RDFNode object);
	
	void remove(Resource subject, Property predicate, RDFNode object);
	
	void removeAll(Resource subject, Property predicate, RDFNode object);

	Iterator<Statement> getIncomingStatements(RDFNode object);

	Iterator<Statement> getOutgoingStatements(Resource subject);
	
	Iterator<Resource> getAllSubjects();
	
	Iterator<Statement> getAllStatements();

	Resource getType(Resource node);


}
