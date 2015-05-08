package fi.hut.cs.drumbeat.rdf.data;

import com.hp.hpl.jena.rdf.model.RDFNode;

public enum RdfNodeTypeEnum {
	Uri,
	BlankNode,
	Literal,
	List;
	
	public boolean isUri() {
		return this == Uri;
	}
	
	public boolean isBlankNode() {
		return this == BlankNode || this == List;
	}

	public boolean isLiteral() {
		return this == Literal;
	}
	
	public boolean isList() {
		return this == List;
	}
	
	public boolean isTerminated() {
		return this == Uri || this == Literal;
	}

	public static RdfNodeTypeEnum getType(RDFNode node) {
		if (node.isLiteral()) {
			return RdfNodeTypeEnum.Literal;
		} else if (node.isURIResource()) {
			return RdfNodeTypeEnum.Uri;
		} else {
			return RdfNodeTypeEnum.BlankNode;
		}
	}
	
	public static boolean isTerminated(RDFNode node) {
		return node.isLiteral() || node.isURIResource();
	}
	
}