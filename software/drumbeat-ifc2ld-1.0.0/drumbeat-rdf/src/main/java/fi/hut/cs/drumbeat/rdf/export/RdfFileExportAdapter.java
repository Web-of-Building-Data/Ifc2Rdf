//package fi.hut.cs.drumbeat.rdf.export;
//
//import java.io.Writer;
//import java.util.Dictionary;
//import java.util.Hashtable;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Property;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.Resource;
//
//public class RdfFileExportAdapter implements RdfExportAdapter {
//
//	private static final String PREFIX = "@prefix";
//	
//	private Writer writer;
//	private String currentSubject;
//	private String currentSectionTitle;
//	private Model jenaModel;
//	
//	private Dictionary<String, String> prefixDictionary;
//
//	public RdfFileExportAdapter(Writer writer) {
//		this.writer = writer;
//		this.jenaModel = ModelFactory.createDefaultModel();
//		prefixDictionary = new Hashtable<>();
//	}
//	
//	@Override
//	public Model getInternalJenaModel() {
//		return jenaModel;
//	}	
//
//	@Override
//	public void exportOntologyHeader(String uri, String version, String comment) {
//	}
//
//	@Override
//	public void setNamespacePrefix(String prefix, String uri) {
//		prefixDictionary.put(prefix, uri);
//		writeSimpleTriple(PREFIX, prefix, String.format("<%s>", uri));		
//	}
//
//	@Override
//	public void startExport() {
//	}
//
//	@Override
//	public void endExport() {
//	}
//
//	@Override
//	public void exportEmptyLine() {
//	}
//
//	@Override
//	public void startSection(String name) {
//	}
//
//	@Override
//	public void endSection() {
//	}
//
//	@Override
//	public void exportTriple(Resource subject, Property predicate, RDFNode object) {
//	}
//
//	public void writeSimpleTriple(String subject, String predicate, String object) {
////		try {
////			writer.write(subject);
////		} catch (IOException e) {
////			logger.
////		}
////		writer.write(' ');
////		writer.write(predicate);
////		writer.write(' ');
////		writer.write(object);
////		writer.write(".\r\n");
//	}
//	
//	public void writeTriple(String subject, String predicate, String object) {
////		if (subject.equals(currentSubject)) {
////			writer.write(";");
////		} else {
////			if (currentSubject != null) { 
////				writer.write(".\r\n");
////			}
////			currentSubject = subject;
////			writer.write(subject);
////		}
////		writer.write("\r\n\t");
////		writer.write(predicate);
////		writer.write(' ');
////		writer.write(object);
//	}
//	
//}
