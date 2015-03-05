package net.linkedbuildingdata.rdf;

import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class RdfVocabulary {
	
	public static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();
	
	public static class OWL {
		public static final String BASE_PREFIX = "owl";
	}
	
	public static class RDF {
		public static final String BASE_PREFIX = "rdf";
	}

	public static class RDFS {
		public static final String BASE_PREFIX = "rdfs";
	}

	public static class XSD {
		public static final String BASE_PREFIX = "xsd";
		public static final Property maxExclusive = DEFAULT_MODEL.createProperty(com.hp.hpl.jena.vocabulary.XSD.getURI() + "maxExclusive");
		public static final Property minExclusive = DEFAULT_MODEL.createProperty(com.hp.hpl.jena.vocabulary.XSD.getURI() + "minExclusive");		
	}	
	
	public static class OLO {
		public static final String BASE_PREFIX = "olo";
		public static final String BASE_URI = "http://purl.org/ontology/olo/core#";
		public static final String IMPORT_URI = "http://purl.org/ontology/olo/orderedlistontology.owl"; 

		public static final Resource OrderedList = DEFAULT_MODEL.createResource(BASE_URI + "OrderedList");
		public static final Resource Slot = DEFAULT_MODEL.createResource(BASE_URI +  "Slot");
		
		public static final Property index = DEFAULT_MODEL.createProperty(BASE_URI +  "index");
		public static final Property item = DEFAULT_MODEL.createProperty(BASE_URI +  "item");
		public static final Property length = DEFAULT_MODEL.createProperty(BASE_URI +  "length");
		public static final Property slot = DEFAULT_MODEL.createProperty(BASE_URI +  "slot");		
	}
	
	public static String getRdfFileExtension(RDFFormat format) {
		// See: http://jena.apache.org/documentation/io/
		String formatString = format.toString().toUpperCase();		
		
		if (formatString.startsWith("RDF")) {
			if (formatString.contains("XML")) {
				// RDF/XML --> .rdf
				return "rdf";
			} else if (formatString.contains("JSON")) {
				// RDF/JSON --> .rj
				return "rj";
			} else if (formatString.contains("THRIFT")) {
				return "rt";
			}
		} else if (formatString.startsWith("TURTLE") || formatString.startsWith("TTL")) {
			return "ttl"; // similar to .n3
		} else if (formatString.startsWith("N-TRIPLES") || formatString.startsWith("NT")) {
			return "nt";
		} else if (formatString.startsWith("N-QUADS") || formatString.startsWith("NQ"))  {
			return "nq";
		} else if (formatString.startsWith("JSON")) {
			return "jsonld";
		} else if (formatString.startsWith("TRIG")) {
			return "trig";
		}
		return "rdf";
	}
}
