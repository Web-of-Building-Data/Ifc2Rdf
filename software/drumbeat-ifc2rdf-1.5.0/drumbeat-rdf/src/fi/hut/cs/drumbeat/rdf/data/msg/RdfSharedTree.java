package fi.hut.cs.drumbeat.rdf.data.msg;

import java.util.SortedMap;
import java.util.TreeMap;

import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.rdf.data.RdfLinkPath;


public class RdfSharedTree extends RdfTree {

	private static final long serialVersionUID = 1L;
	private SortedMap<RdfLinkPath, RdfTree> ownerTrees;
	private ByteArray checksum;
	private int depth;

	public RdfSharedTree(Resource headNode) {
		super(headNode);
		ownerTrees = new TreeMap<>();
	}

	public void addOwnerTree(RdfLinkPath linkPath, RdfTree ownerTree) {
		ownerTrees.put(linkPath.getCopy(), ownerTree);
	}

	public SortedMap<RdfLinkPath, RdfTree> getOwnerTrees() {
		return ownerTrees;
	}

	@Override
	public ByteArray getChecksum() {
		
		if (checksum == null) {
			checksum = super.getChecksum();
		}
		return checksum;		
	}
	
	@Override
	public int getDepth() {
		if (depth == 0) {
			depth = super.getDepth();
		}
		return depth;
	}
	
	
}
