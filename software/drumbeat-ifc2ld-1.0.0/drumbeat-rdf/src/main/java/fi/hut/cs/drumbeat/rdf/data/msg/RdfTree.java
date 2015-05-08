package fi.hut.cs.drumbeat.rdf.data.msg;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.common.digest.MessageDigestManager;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.data.RdfComparator;


public class RdfTree extends TreeMap<Statement, RdfTree> implements Comparable<RdfTree> {
	
	private static final long serialVersionUID = 1L;

	private static final String PREFIX_SHARED_NODE = "[S]";
	private static final String PREFIX_TOP_NODE = "[T]";

	private static final byte BYTE_START_PREDICATE = (byte)0;
	private static final byte BYTE_END_PREDICATE = (byte)0xFF;
	
	private final Resource headNode;

	public RdfTree(Resource headNode) {
		super(RdfComparator.TRIPLE_COMPARATOR);
		this.headNode = headNode;
	}
	
	public int getDepth() {
		int maxDepth = 0;
		for (RdfTree subMsg : values()) {
			if (subMsg != null) {
				int depth = subMsg.getDepth();
				if (maxDepth < depth) {
					maxDepth = depth;
				}
			}
		}
		return maxDepth + 1;
	}
	
	public int getSizeInTriples() {
		return (int)toModel().size();
//		Set<RdfSharedTree> subSharedTrees = new TreeSet<>();
//		int totalSize = getSizeInTriplesWithoutSubSharedTrees(subSharedTrees );
//		for (RdfSharedTree subSharedTree : subSharedTrees) {
//			totalSize += subSharedTree.getSizeInTriplesWithoutSubSharedTrees();
//		}
//		return totalSize;
	}
	
	public int getSizeInTriplesWithoutSubSharedTrees() {

		int totalSize = size();
		
		for (RdfTree subTree : values()) {
			if (subTree != null && !(subTree instanceof RdfSharedTree)) {
				totalSize += subTree.getSizeInTriplesWithoutSubSharedTrees();
			}
		}
		return totalSize;
	}	
	
	@Override
	public RdfTree put(Statement statement, RdfTree subTreeMsg) {
		return super.put(statement, subTreeMsg);
	}

//	/**
//	 * @return the isShared
//	 */
//	public boolean isShared() {
//		return isShared;
//	}
//
//	/**
//	 * @param isShared the isShared to set
//	 */
//	public void setShared(boolean isShared) {
//		this.isShared = isShared;
//	}

	@Override
	public int compareTo(RdfTree other) {
		int result = RdfComparator.NODE_COMPARATOR.compare(headNode, other.getHeadNode());
		if (result != 0) {
			return result;
		} else {
			Iterator<Statement> it1 = this.keySet().iterator();
			Iterator<Statement> it2 = other.keySet().iterator();
			
			while (it1.hasNext()) {
				if (it2.hasNext()) {
					Statement statement1 = it1.next();
					Statement statement2 = it2.next();
					result = RdfComparator.TRIPLE_COMPARATOR.compare(statement1, statement2);
					if (result != 0) {
						return result;
					}
				} else {
					return 1;
				}
			}
			
			return it2.hasNext() ? -1 : 0;
			
//			Statement thisFirstStatement = firstKey();
//			Statement otherFirstStatement = other.firstKey();
//			return RdfComparator.TRIPLE_COMPARATOR.compare(thisFirstStatement, otherFirstStatement);
		}
	}
	
	public Statement getHeadStatement() {
		return firstKey();
	}
	
	public Resource getHeadNode() {
		return headNode;
	}
	
	public ByteArray getChecksum() {
		
		MessageDigest messageDigest = MessageDigestManager.getMessageDigest();
		
		Resource subject = getHeadNode();
		if (subject.isURIResource()) {
			messageDigest.update(subject.getURI().getBytes());
		}

		Property currentPredicate = null;
		ByteArray xorOjectChecksum = null;
		
		for (Entry<Statement, RdfTree> entry : entrySet()) {
			Statement statement = entry.getKey();	
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			RdfTree subMsg = entry.getValue();			
			
			if (predicate != currentPredicate) {
				//
				// handle the end of the previous predicate
				//
				if (currentPredicate != null) {
					assert (xorOjectChecksum != null) : "Expected: (xorOjectChecksum != null)";
					messageDigest.update(BYTE_START_PREDICATE);
					messageDigest.update(currentPredicate.getURI().getBytes());
					messageDigest.update(xorOjectChecksum.array);
					messageDigest.update(BYTE_END_PREDICATE);
				}
				
				// 
				// handle the start of the new predicate
				//
				currentPredicate = predicate;
				xorOjectChecksum = null;
			}
			
			ByteArray newObjectChecksum; 

			if (subMsg == null) {
				assert(!object.isAnon()) : String.format("Expected: !object.getRdfNodeType().isBlankNode(), RdfProperty = %s", statement);
				newObjectChecksum = new ByteArray(DigestUtils.md5(object.toString()));				
			} else {
				newObjectChecksum = subMsg.getChecksum();
			}
			
			if (xorOjectChecksum == null) {
				xorOjectChecksum = newObjectChecksum;
			} else {
				xorOjectChecksum.xor(newObjectChecksum);
			}
		}
		
		if (currentPredicate != null) {
			assert (xorOjectChecksum != null) : "Expected: (xorOjectChecksum != null)";
			messageDigest.update(BYTE_START_PREDICATE);
			messageDigest.update(currentPredicate.getURI().getBytes());
			messageDigest.update(xorOjectChecksum.array);
			messageDigest.update(BYTE_END_PREDICATE);
		}
			
		return new ByteArray(messageDigest.digest());
	}
	
	public Model toModel() {
		Model model = ModelFactory.createDefaultModel();
		addAllToModel(model);
		return model;
	}	
	
	public void addAllToModel(Model model) {
		for (Entry<Statement, RdfTree> entry : this.entrySet()) {
			Statement statement = entry.getKey();
			model.add(statement);
			RdfTree subTree = entry.getValue();
			if (subTree != null) {
				subTree.addAllToModel(model);
			}
		}
	}

	public boolean contains(Statement statement) {
		if (this.containsKey(statement)) {
			return true;
		}
		
		for (RdfTree subTree : values()) {
			if (subTree != null && subTree.contains(statement)) {
				return true;
			}
		}
		
		return false;
	}
	
	public StringBuilder appendToStringBuilder(StringBuilder sb, String tabString, int tabsCount, boolean exportOnlyTopAndSharedNodes) {
		
		boolean exportThis = !exportOnlyTopAndSharedNodes || this instanceof RdfTopTree || this instanceof RdfSharedTree; 
		
		for (Entry<Statement, RdfTree> entry : this.entrySet()) {
			Statement statement = entry.getKey();
			
			if (exportThis) {
			
				for (int i = 0; i < tabsCount; ++i) {
					sb.append(tabString);
				}
				
				if (this instanceof RdfTopTree) {
					sb.append(PREFIX_TOP_NODE);				
					
				} else if (this instanceof RdfSharedTree) {
					sb.append(PREFIX_SHARED_NODE);
				}
				
				sb.append(RdfUtils.getShortStringWithTypes(statement));
				sb.append(StringUtils.END_LINE);
			}
			
			RdfTree subTree = entry.getValue();
			if (subTree != null) {
				subTree.appendToStringBuilder(sb, tabString, tabsCount + 1, exportOnlyTopAndSharedNodes);
			}			
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
