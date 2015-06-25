package fi.hut.cs.drumbeat.ifc.util.decomposing.msg;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.hut.cs.drumbeat.common.collections.CollectionUtils;
import fi.hut.cs.drumbeat.common.collections.DefaultComparator;
import fi.hut.cs.drumbeat.common.numbers.IntegerWrapper;
import fi.hut.cs.drumbeat.common.statistics.GrouppedCounterTree;
import fi.hut.cs.drumbeat.common.statistics.GrouppedCounterTree.GroupedCounter;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.data.RdfLinkPath;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsg;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgContainer;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgType;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfSharedTree;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfTopTree;


public class RdfMsgStatisticsExporter {

	private static final int HUGE_MSG_SIZE_THRESHOLD = 1000;
	
	private final Writer writer;
	private String currentHeader;

	private RdfMsgStatisticsExporter(Writer writer) {
		this.writer = writer;
	}
	
	public static void export(RdfMsgContainer rdfMsgContainer, Writer writer) throws IOException {
		new RdfMsgStatisticsExporter(writer).export(rdfMsgContainer);
	}

	private void export(RdfMsgContainer rdfMsgContainer) throws IOException {
		
		writeHeaderStart("Total number of MSGs:");
		writer.write(StringUtils.END_LINE);

		writer.write(String.format("COUNT = %,9d\r\n", rdfMsgContainer.getTotalSize()));

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		
		writeHeaderStart("Total number of MSGs grouped by type:");
		writer.write(StringUtils.END_LINE);
		
		for (RdfMsgType type : RdfMsgType.values()) {
			writer.write(String.format("COUNT = %,9d <== %s\r\n", rdfMsgContainer.getAllMsgsByType(type).size(), type));
		}
		
		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		
		//
		// Analyzing number of triples, top trees and shared trees.
		//	
		long totalSize = 0;
		GrouppedCounterTree tripleSizeCounter = new GrouppedCounterTree(
				new DefaultComparator<Integer>());
		
		GrouppedCounterTree treeTypeAndTripleSizeCounter = new GrouppedCounterTree(
				new DefaultComparator<RdfMsgType>(),
				new DefaultComparator<Integer>());

		GrouppedCounterTree treeCounter = new GrouppedCounterTree(
				new DefaultComparator<Integer>(),
				new DefaultComparator<Integer>());
		
		GrouppedCounterTree topEntityNodeTypeCounter = new GrouppedCounterTree(new DefaultComparator<String>());
		GrouppedCounterTree sharedEntityNodeTypeCounter = new GrouppedCounterTree(new DefaultComparator<String>());
		
		GrouppedCounterTree topEntityNodeTypeAndSizeCounter =
				new GrouppedCounterTree(new DefaultComparator<Integer>(), new DefaultComparator<String>());
		GrouppedCounterTree sharedEntityNodeTypeAndSizeCounter =
				new GrouppedCounterTree(new DefaultComparator<Integer>(), new DefaultComparator<String>());
		
		GrouppedCounterTree sharedPathCounter = new GrouppedCounterTree(new DefaultComparator<String>());
		
		GrouppedCounterTree sharedEntityPathCounter = new GrouppedCounterTree(new DefaultComparator<String>(), new DefaultComparator<String>());
		GrouppedCounterTree topTreeDepthCounter = new GrouppedCounterTree(new DefaultComparator<Integer>());
		GrouppedCounterTree sharedTreeDepthCounter = new GrouppedCounterTree(new DefaultComparator<Integer>());
		
		Map<RdfMsg, Integer> hugeRdfMsgs = new HashMap<>();
		
		for (RdfMsg rdfMsg : rdfMsgContainer) {
			
			int size = rdfMsg.getSizeInTriples();
			
			if (size >= HUGE_MSG_SIZE_THRESHOLD) {
				hugeRdfMsgs.put(rdfMsg, size);
			}
			
			
			totalSize += size;
			tripleSizeCounter.addItem(new Integer(size));
			treeTypeAndTripleSizeCounter.addItem(rdfMsg.getType(), new Integer(size));
			
			SortedSet<RdfTopTree> topTrees = rdfMsg.getTopTrees();			
			int numberOfTopTrees = topTrees.size();			
			
			SortedMap<Resource, RdfSharedTree> sharedTrees = rdfMsg.getSharedTrees(); 
			int numberOfSharedTrees = sharedTrees.size();
			treeCounter.addItem(new Integer(numberOfTopTrees), new Integer(numberOfSharedTrees));
			
			if (!sharedTrees.isEmpty()) {
				for (RdfTopTree topTree : topTrees) {
					Resource headNode = topTree.getHeadNode();
					
					String headNodeTypeName = headNode.getPropertyResourceValue(RDF.type).getLocalName();
					topEntityNodeTypeCounter.addItem(headNodeTypeName);
					topEntityNodeTypeAndSizeCounter.addItem(topTree.getSizeInTriples(), headNodeTypeName);
					
					int depth = topTree.getDepth();
					topTreeDepthCounter.addItem(new Integer(depth));					
				}
				
				for (RdfSharedTree sharedTree : sharedTrees.values()) {
					int depth = sharedTree.getDepth();
					sharedTreeDepthCounter.addItem(new Integer(depth));				
					
					
					Resource sharedNode = sharedTree.getHeadNode();
					String sharedNodeTypeName = sharedNode.getPropertyResourceValue(RDF.type).getLocalName();
					sharedEntityNodeTypeCounter.addItem(sharedNodeTypeName);
					sharedEntityNodeTypeAndSizeCounter.addItem(sharedTree.getSizeInTriples(), sharedNodeTypeName);
					
					for (RdfLinkPath linkPath : sharedTree.getOwnerTrees().keySet()) {
						
						StringBuilder sb = new StringBuilder();
						
						Resource headNode = linkPath.getHeadNode();
						
						sb.append(String.format(headNode.getPropertyResourceValue(RDF.type).getLocalName()));
						
						
//						boolean isFunctional = true;
						
						for (Statement statement : linkPath.getStatements()) {
							
//							// TODO: check if the link is functional or not!
//							isFunctional = isFunctional && link.isFunctional();
							
							sb.append(String.format(".%s-->", statement.getPredicate().getLocalName()));
							RDFNode object = statement.getObject();							
							if (object.isResource()) {
								Resource objectResource = object.asResource();
								Resource type = objectResource.getPropertyResourceValue(RDF.type);
								if (type == null) {
									if (objectResource.hasProperty(RDF.first)) {
										type = RDF.List;
									} else {
										throw new IllegalArgumentException("Unknown RDF type of object " + objectResource.getURI());
									}
								}
								if (!type.equals(RDF.List)) {
									sb.append(type.getLocalName());
								} else {
									sb.append("LIST");
								}
							}
						}
						
						String sharedPath = sb.toString();
						sharedPathCounter.addItem(sharedPath);						

						String headNodeId = headNode.isURIResource() ? headNode.getLocalName() : headNode.getId().getLabelString();
						String sharedNodeId = sharedNode.getId().getLabelString();						
						
						sharedEntityPathCounter.addItem(sharedNodeId,
								headNodeId + "/" + sharedPath);
					}
					
				}
			}
		}
		
		writeHeaderStart("Total number of triples");
		writer.write(StringUtils.END_LINE);

		writer.write(String.format("COUNT = %,9d\r\n", totalSize));

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of MSGs grouped by number of triples");
		writer.write(StringUtils.END_LINE);

		for (Entry<Integer, IntegerWrapper> entry : tripleSizeCounter.<Integer>getRootGroupedCounter().entrySet()) {
			int numberOfTriples = entry.getKey().intValue();
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== NUMBER OF TRIPLES: %,9d\r\n", count, numberOfTriples));
		}
		
		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of MSGs grouped by tree type and number of triples");
		writer.write(StringUtils.END_LINE);

		for (Entry<RdfMsgType, GroupedCounter<?>> entry : treeTypeAndTripleSizeCounter.<RdfMsgType>getRootGroupedCounter().getSubGroupedCounterMap().entrySet()) {
			for (Entry<?, IntegerWrapper> entry2 : entry.getValue().entrySet()) {
				RdfMsgType type = entry.getKey();
				int numberOfTriples = ((Integer)entry2.getKey()).intValue();
				int count = entry2.getValue().intValue();
				writer.write(String.format("COUNT = %,9d <== MSG TYPE: %s \t NUMBER OF TRIPLES: %,9d\r\n", count, type, numberOfTriples));
			}
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);		
		writeHeaderStart("Counts of MSGs grouped by number of top and shared trees (same as: number of head nodes and shared blank nodes)");
		writer.write(StringUtils.END_LINE);
		
		
		for (Entry<Integer, GroupedCounter<?>> entry : treeCounter.<Integer>getRootGroupedCounter().getSubGroupedCounterMap().entrySet()) {
			for (Entry<?, IntegerWrapper> entry2 : entry.getValue().entrySet()) {
				int numberOfTopTrees = entry.getKey().intValue();
				int numberOfSharedTrees = ((Integer)entry2.getKey()).intValue();
				int count = entry2.getValue().intValue();
				writer.write(String.format("COUNT = %,9d <== NUMBER OF TOP & SHARED TREES: %,6d \t&\t %,6d\r\n",
						count, numberOfTopTrees, numberOfSharedTrees));
			}
		}
		
		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of head nodes grouped by head entity types");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<String, IntegerWrapper> entry : topEntityNodeTypeCounter.<String>getRootGroupedCounter().entrySet()) {
			String topTreeType = entry.getKey();
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== TOP TREE TYPE: %s\r\n", count, topTreeType));
		}
		
		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of head nodes grouped by head entity types and tree sizes");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<Integer, GroupedCounter<?>> entry : topEntityNodeTypeAndSizeCounter.<Integer>getRootGroupedCounter().getSubGroupedCounterMap().entrySet()) {
			for (Entry<?, IntegerWrapper> entry2 : entry.getValue().entrySet()) {
				int size = entry.getKey().intValue();
				String topTreeType = (String)entry2.getKey();
				int count = entry2.getValue().intValue();
				writer.write(String.format("COUNT = %,9d <== SIZE: %,9d,\tTOP TREE TYPE: %s\r\n", count, size, topTreeType));
			}
		}
		

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of shared blank nodes grouped by shared blank entity types");		
		writer.write(StringUtils.END_LINE);

		for (Entry<String, IntegerWrapper> entry : sharedEntityNodeTypeCounter.<String>getRootGroupedCounter().entrySet()) {
			String sharedTreeType = entry.getKey();			
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== SHARED TREE TYPE: %s\r\n", count, sharedTreeType));
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of shared blank nodes grouped by shared blank entity types and tree sizes");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<Integer, GroupedCounter<?>> entry : sharedEntityNodeTypeAndSizeCounter.<Integer>getRootGroupedCounter().getSubGroupedCounterMap().entrySet()) {
			for (Entry<?, IntegerWrapper> entry2 : entry.getValue().entrySet()) {
				int size = entry.getKey();
				String sharedTreeType = (String)entry2.getKey();
				int count = entry2.getValue().intValue();
				writer.write(String.format("COUNT = %,9d <== SIZE: %,9d,\tSHARED TREE TYPE: %s\r\n", count, size, sharedTreeType));
			}
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of links from grounded (head) nodes to shared blank nodes grouped by path strings");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<String, IntegerWrapper> entry : sharedPathCounter.<String>getRootGroupedCounter().entrySet()) {
			String sharedPath = entry.getKey();			
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== SHARED PATH: %s\r\n", count, sharedPath));
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of links from grounded (head) nodes to shared blank nodes grouped by shared node and path strings");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<String, GroupedCounter<?>> entry : sharedEntityPathCounter.<String>getRootGroupedCounter().getSubGroupedCounterMap().entrySet()) {
			for (Entry<?, IntegerWrapper> entry2 : entry.getValue().entrySet()) {
				String sharedNode = entry.getKey();
				String sharedPath = entry2.getKey().toString();
				int count = entry2.getValue().intValue();
				writer.write(String.format("COUNT = %,9d <== SHARED NODE: %s\t&\tSHARED PATH: %s\r\n",
						count, sharedNode, sharedPath));
			}
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of top trees grouped by depths");		
		writer.write(StringUtils.END_LINE);
		
		for (Entry<Integer, IntegerWrapper> entry : topTreeDepthCounter.<Integer>getRootGroupedCounter().entrySet()) {
			int topTreeDepth = entry.getKey().intValue();			
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== TOP TREE DEPTH: %d\r\n", count, topTreeDepth));
		}

		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Counts of shared trees grouped by depths");		
		writer.write(StringUtils.END_LINE);

		for (Entry<Integer, IntegerWrapper> entry : sharedTreeDepthCounter.<Integer>getRootGroupedCounter().entrySet()) {
			int sharedTreeDepth = entry.getKey().intValue();			
			int count = entry.getValue().intValue();
			writer.write(String.format("COUNT = %,9d <== SHARED TREE DEPTH: %d\r\n", count, sharedTreeDepth));
		}

		writer.write(StringUtils.END_LINE);
		
		for (Entry<RdfMsg, Integer> entry : CollectionUtils.getEntriesSortedByValues(hugeRdfMsgs)) {
			writeHugeRdfMsgAnalysis(entry.getKey(), entry.getValue());
		}
		

		writer.flush();
	}
	
	private void writeHugeRdfMsgAnalysis(RdfMsg rdfMsg, int totalSize) throws IOException {
		writer.write(StringUtils.END_LINE);
		writer.write(StringUtils.END_LINE);
		writeHeaderStart("Huge MSG info");		
		writer.write(StringUtils.END_LINE);
		
		if (totalSize > 23000) {
			totalSize = rdfMsg.getSizeInTriples();
		}
		
		writer.write(String.format("Total size: %,9d, \t Checksum: %s%n%n", totalSize, Hex.encodeHexString(rdfMsg.getChecksum().array)));
		
		for (RdfTopTree topTree : rdfMsg.getTopTrees()) {
			Resource headNode = topTree.getHeadNode();
			String headNodeName = headNode.isURIResource() ? headNode.getLocalName() : headNode.getId().getLabelString();
			String headNodeTypeName = headNode.getPropertyResourceValue(RDF.type).getLocalName();
			int size = topTree.getSizeInTriples();
			int sizeWithoutSharedtrees = topTree.getSizeInTriplesWithoutSubSharedTrees();
			int depth = topTree.getDepth();
			writer.write(
					String.format("Head node: %s/%s, \t Size: %,9d, \t Size w/o shared trees: %,9d \t Depth: %d%n",
							headNodeName,
							headNodeTypeName,
							size,
							sizeWithoutSharedtrees,
							depth));
		}
		
		writer.write(StringUtils.END_LINE);

		for (RdfSharedTree sharedTree : rdfMsg.getSharedTrees().values()) {
			Resource sharedNode = sharedTree.getHeadNode();
			String sharedNodeName = sharedNode.isURIResource() ? sharedNode.getLocalName() : sharedNode.getId().getLabelString();
			String sharedNodeTypeName = sharedNode.getPropertyResourceValue(RDF.type).getLocalName();
			int size = sharedTree.getSizeInTriples();
			int sizeWithoutSharedtrees = sharedTree.getSizeInTriplesWithoutSubSharedTrees();
			int depth = sharedTree.getDepth();
			writer.write(
					String.format("Shared node: %s/%s, \t Number of incoming links: %,d, \t Size: %,9d, \t Size w/o shared trees: %,9d \t Depth: %d%n",
							sharedNodeName,
							sharedNodeTypeName,
							RdfUtils.getNumberOfIncomingLinks(sharedNode),
							size,
							sizeWithoutSharedtrees,
							depth));
		}
		
		writer.write(StringUtils.END_LINE);
		
		
	}
	
	private void writeHeaderStart(String header) throws IOException {
		writeHeaderEnd();
		
		writer.write("************************************************************************\r\n");
		writer.write(header);
		writer.write(StringUtils.END_LINE);
		writer.write("************************************************************************\r\n");
	}

	private void writeHeaderEnd() throws IOException {
		if (currentHeader != null) {
			writer.write("************************************************************************\r\n");
			writer.write(String.format("END OF %s\r\n", currentHeader));
			writer.write("************************************************************************\r\n");
			currentHeader = null;
		}
	}
	
	
//	private static class Counter<T> extends TreeMap<T, Integer> {
//		
//		private static final long serialVersionUID = 1L;
//		
//		public Counter() {			
//		}
//		
//		public Counter(Comparator<? super T> comparator) {
//			super(comparator);
//		}
//
//		void addItem(T key) {
//			Integer count = get(key);
//			if (count == null) {
//				put(key, new Integer(1));
//			} else {
//				put(key, new Integer(count.intValue() + 1));				
//			}			
//		}
//	}
}
