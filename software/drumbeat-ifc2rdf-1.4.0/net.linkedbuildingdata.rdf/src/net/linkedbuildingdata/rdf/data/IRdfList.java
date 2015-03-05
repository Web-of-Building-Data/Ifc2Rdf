package net.linkedbuildingdata.rdf.data;

import java.util.List;

public interface IRdfList extends IRdfNode {
	
	List<? extends IRdfNode> getRdfListNodes();

}
