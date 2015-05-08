package fi.hut.cs.drumbeat.rdf.data.msg;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

//import org.apache.log4j.Logger;



import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import fi.hut.cs.drumbeat.common.collections.SortedList;
import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.rdf.data.RdfComparator;

public class RdfMsg implements Comparable<RdfMsg> {
	
//	private Logger logger = Logger.getRootLogger();

	private SortedSet<RdfTopTree> topTrees;
	private SortedMap<Resource, RdfSharedTree> sharedTrees;
	
	public RdfMsg() {
		topTrees = new TreeSet<>();
	}

	@Override
	public int compareTo(RdfMsg other) {
		Iterator<RdfTopTree> it1 = this.topTrees.iterator();
		Iterator<RdfTopTree> it2 = other.topTrees.iterator();
		
		while (it1.hasNext()) {
			if (it2.hasNext()) {
				RdfTree tree1 = it1.next();
				RdfTree tree2 = it2.next();
				int result = tree1.compareTo(tree2);
				if (result != 0) {
					return result;
				}
			} else {
				return 1;
			}
		}
		
		return it2.hasNext() ? -1 : 0;		
		
//		RdfTree first = topTrees.first();
//		RdfTree otherFirst = other.topTrees.first();		
//		return first.compareTo(otherFirst);
	}	
	
	public void addTopTree(RdfTopTree tree) {
		topTrees.add(tree);
	}
	
	public void addSharedTree(RdfSharedTree tree) {
		if (sharedTrees == null) {
			sharedTrees = new TreeMap<>(RdfComparator.NODE_COMPARATOR);
		}
		sharedTrees.put(tree.getHeadNode(), tree);
	}
	
	public SortedSet<RdfTopTree> getTopTrees() {
		return topTrees;
	}
	
	public SortedMap<Resource, RdfSharedTree> getSharedTrees() {
		if (sharedTrees == null) {
			sharedTrees = new TreeMap<>(RdfComparator.NODE_COMPARATOR);
		}
		return sharedTrees;
	}

	public ByteArray getChecksum() {
		
		if (topTrees.size() == 1) {
			
			return topTrees.first().getChecksum();
			
		} else {
		
			ByteArray msgChecksum = null;
			
			for (RdfTree tree : topTrees) {
				ByteArray moleculeChecksum = tree.getChecksum(); 
				if (msgChecksum == null) {
					msgChecksum = moleculeChecksum;
				} else {
					msgChecksum.xor(moleculeChecksum);
				}
			}
			
			return msgChecksum;
		}
	}

	public void mergeWith(RdfMsg msg2) {
		topTrees.addAll(msg2.topTrees);
		if (msg2.sharedTrees != null) {
			getSharedTrees().putAll(msg2.sharedTrees);
		}
	}

	public int getSizeInTriples() {
		
		int totalSize = 0;
		
		for (RdfTopTree tree : topTrees) {
			totalSize += tree.getSizeInTriplesWithoutSubSharedTrees();
		}
		
		if (sharedTrees != null) {
			for (RdfSharedTree subSharedTree : sharedTrees.values()) {
				totalSize += subSharedTree.getSizeInTriplesWithoutSubSharedTrees();
			}
		}
		
		return totalSize;
	}
	
	public RdfMsgType getType() {
		if (topTrees.size() == 1) {
			return topTrees.first().isSingle() ? RdfMsgType.SingleTripleMsg : RdfMsgType.SingleTreeMsg;
		} else {
			assert(!sharedTrees.isEmpty());
			return RdfMsgType.MultiTreeMsg;
		}
	}
	
	public Model toModel() {
		Model model = ModelFactory.createDefaultModel();
		
		for (RdfTree tree : topTrees) {
			tree.addAllToModel(model);
		}
		
		return model;
	}
	
	public boolean contains(Statement statement) {
		for (RdfTopTree tree : topTrees) {
			if (tree.contains(statement)) {
				return true;
			}
		}
		return false;
	}
	
	public int getMaxDepth() {
		int maxDepth = 0;
		for (RdfTopTree tree : topTrees) {
			int depth = tree.getDepth();
			if (maxDepth < depth) {
				maxDepth = depth;
			}
		}
		return maxDepth;
	}
	
	public StringBuilder appendToStringBuilder(StringBuilder sb, String tabString, int tabsCount, boolean exportOnlyTopAndSharedNodes) {
		
		SortedList<RdfTopTree> topTreeList = new SortedList<>(new Comparator<RdfTree>() {

			@Override
			public int compare(RdfTree o1, RdfTree o2) {
				int result = Integer.compare(o1.getDepth(), o2.getDepth());
				if (result != 0) {
					return -result;
				}
				
				return o1.compareTo(o2);
			}
			
		});
		
		topTreeList.addAll(topTrees);
		
		int maxDepth = getMaxDepth();
		
		for (RdfTopTree topTree : topTreeList) {
			topTree.appendToStringBuilder(sb, tabString, maxDepth - topTree.getDepth() + tabsCount, exportOnlyTopAndSharedNodes);
			sb.append(StringUtils.END_LINE);
		}
		
		return sb;
	}
	
	public String toString(String tabString, int tabsCount, boolean exportOnlyTopAndSharedNodes) {
		StringBuilder sb = new StringBuilder();
		appendToStringBuilder(sb, tabString, tabsCount, exportOnlyTopAndSharedNodes);
		return sb.toString();		
	}
	
	@Override
	public String toString() {
		return toString(StringUtils.TABULAR, 0, false);
	}
	
	
}


