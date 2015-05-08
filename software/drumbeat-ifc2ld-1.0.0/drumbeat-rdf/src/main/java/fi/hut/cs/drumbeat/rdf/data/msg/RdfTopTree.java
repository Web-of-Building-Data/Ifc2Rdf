package fi.hut.cs.drumbeat.rdf.data.msg;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import fi.hut.cs.drumbeat.rdf.data.RdfNodeTypeEnum;


//import fi.aalto.cse.dsg.rdf.IRdfLink;
//import fi.aalto.cse.dsg.rdf.IRdfNode;

public class RdfTopTree extends RdfTree {

	private static final long serialVersionUID = 1L;
	private int depth;
	
	public RdfTopTree(Resource headNode) {
		super(headNode);
	}
	
	public boolean isSingle() {
		if (this.size() == 1) {
			Statement statement = this.firstKey();
			return RdfNodeTypeEnum.isTerminated(statement.getObject()) &&
					RdfNodeTypeEnum.isTerminated(this.getHeadNode());  
		}
		return false;
	}
	
	@Override
	public int getDepth() {
		if (depth == 0) {
			depth = super.getDepth();
		}
		return depth;
	}

}
