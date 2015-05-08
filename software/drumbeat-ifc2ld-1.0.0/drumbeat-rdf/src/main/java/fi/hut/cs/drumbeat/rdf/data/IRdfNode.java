package fi.hut.cs.drumbeat.rdf.data;


import com.hp.hpl.jena.rdf.model.RDFNode;

public interface IRdfNode {
	
	RdfNodeTypeEnum getRdfNodeType();
	
	RDFNode getNode();
	
	void setNode(RDFNode node);
	
}
