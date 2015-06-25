package fi.hut.cs.drumbeat.ifc.util.decomposing.msg;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.codec.binary.Hex;

import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.common.file.FileManager;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsg;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgContainer;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgType;


public class RdfMsgExporter {
	
	private RdfMsgExporter() {
	}
	
	public static void export(RdfMsgContainer rdfMsgContainer, String filePathFormat) throws IOException {
		new RdfMsgExporter().exportStructuredMsgs(rdfMsgContainer, filePathFormat);
	}

	private void exportStructuredMsgs(RdfMsgContainer rdfMsgContainer, String filePathFormat) throws IOException {
		
		for (RdfMsgType type : RdfMsgType.values()) {
			Writer writer = createWriter(filePathFormat, type);
			for (RdfMsg rdfMsg : rdfMsgContainer.getAllMsgsByType(type)) {
				exportStructuredMsg(writer, rdfMsg);
			}
			writer.close();	
		}
		
	}
	
	private Writer createWriter(String filePathFormat, RdfMsgType type) throws IOException {
		String filePath = String.format(filePathFormat, type);
		FileWriter writer = FileManager.createFileWriter(filePath);
		return writer;
	}

	private void exportStructuredMsg(Writer writer, RdfMsg rdfMsg) throws IOException {
		
		ByteArray checksum = rdfMsg.getChecksum();		
		
		writer.write(String.format("{Number of top trees: %,d, shared trees: %,d, size: %,d, depth: %,d, Checksum: %s, Base 64: %s}\r\n",
				rdfMsg.getTopTrees().size(),
				rdfMsg.getSharedTrees().size(),
				rdfMsg.getSizeInTriples(),
				rdfMsg.getMaxDepth(),
				Hex.encodeHexString(checksum.array),
				checksum.toBase64String()));
		//writer.write(String.format("{Size: %d, Depth: %d, Checksum: %d, Digest: %s}\r\n",
		//structuredMsg.getTotalSize(),
		//structuredMsg.getDepth(),
		//structuredMsg.getChecksum(),
		//Hex.encodeHexString(structuredMsg.getChecksumEx())));
		
		boolean exportOnlyTopAndSharedNodes = false;		
		writer.write(rdfMsg.toString("  ", 1, exportOnlyTopAndSharedNodes));
		
//		SortedSet<RdfTree> exportedSharedSubTreeMsgs = new TreeSet<>();
//		
//		for (RdfTree tree : rdfMsg.getTopTrees()) {
//			exportRdfTree(writer, tree, 0, exportedSharedSubTreeMsgs);
//		}
		
		writer.write(StringUtils.END_LINE);
	}
	
//	private void exportRdfTree(Writer writer, RdfTree tree, int level, SortedSet<RdfTree> exportedSharedSubTreeMsgs) throws IOException {
//		
//		for (Entry<Statement, RdfTree> entry : tree.entrySet()) {
//			Statement statement = entry.getKey();
//			exportRdfNode(writer, statement, level);
//			writer.write(StringUtils.END_LINE);
//			
//			RdfTree subTreeMsg = entry.getValue();
//			if (subTreeMsg != null) {
//				if (subTreeMsg instanceof RdfSharedTree) {
//					for (int i = 0; i <= level; ++i) {
//						writer.append(StringUtils.TABULAR_CHAR);
//					}
//					
//					if (!exportedSharedSubTreeMsgs.contains(subTreeMsg)) {
//
//						exportedSharedSubTreeMsgs.add(subTreeMsg);
//						
//						writer.write("[ _SHARED_");
//						writer.write(StringUtils.END_LINE);
//						
//
//						exportRdfTree(writer, subTreeMsg, level + 2, exportedSharedSubTreeMsgs);
//
//						writer.write(StringUtils.END_LINE);
//
//						for (int i = 0; i <= level; ++i) {
//							writer.append(StringUtils.TABULAR_CHAR);
//						}
//						writer.write("]");
//						writer.write(StringUtils.END_LINE);
//
//					} else {						
//						writer.write("[ _SHARED_  ");
//						exportRdfNode(writer, subTreeMsg.lastKey(), -1);						
//						writer.write(" ... ]");
//						writer.write(StringUtils.END_LINE);
//					}
//				} else {
//					exportRdfTree(writer, subTreeMsg, level + 1, exportedSharedSubTreeMsgs);
//				}
//			}
//		}
//	}
	
//	private void exportRdfNode(Writer writer, Statement statement, int level) throws IOException {
//		for (int i = 0; i <= level; ++i) {
//			writer.append(StringUtils.TABULAR_CHAR);
//		}
//		writer.write(RdfUtils.getShortStringWithTypes(statement));
//
//		//		return String.format("%s %s %s",
////				prefixMapping.shortForm(property.getSubject().toString()),
////				prefixMapping.shortForm(property.getPredicate().toString()),
////				prefixMapping.shortForm(property.getObject().toString()));
//	}

}
