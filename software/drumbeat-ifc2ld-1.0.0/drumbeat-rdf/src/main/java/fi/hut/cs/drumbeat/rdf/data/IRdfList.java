package fi.hut.cs.drumbeat.rdf.data;

import java.util.List;

public interface IRdfList extends IRdfNode {
	
	List<? extends IRdfNode> getRdfListNodes();

}
