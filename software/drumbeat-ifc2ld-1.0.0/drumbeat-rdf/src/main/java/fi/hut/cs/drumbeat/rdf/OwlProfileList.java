package fi.hut.cs.drumbeat.rdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.rdf.OwlProfile.RdfTripleObjectTypeEnum;

public class OwlProfileList extends ArrayList<OwlProfile> {
	
	private static final long serialVersionUID = 1L;

	public OwlProfileList(String[] owlProfileNames) {
		for (String owlProfileName : owlProfileNames) {
			OwlProfileEnum owlProfileEnum = OwlProfileEnum.valueOf(owlProfileName.trim());
			add(new OwlProfile(owlProfileEnum));
		}		
	}
	
	public List<OwlProfileEnum> getOwlProfileIds() {
		return this.stream().map(OwlProfile::getOwlProfileId).collect(Collectors.toList());
	}
	
	public boolean supportsRdfProperty(Resource property, EnumSet<RdfTripleObjectTypeEnum> tripleObjectType) {
		for (OwlProfile owlProfile : this) {
			if (!owlProfile.supportsRdfProperty(property, tripleObjectType)) {
				return false;
			}
		}
		return true;
	}
	
	public Resource getFirstSupportedType(Collection<Resource> types) {
		
		for (Resource type : types) {
			boolean isSupported = true;
			for (OwlProfile profile : this) {
				if (!profile.supportXsdType(type)) {
					isSupported = false;
				}
			}
			if (isSupported) {
				return type;
			}
		}
		
		return null;		
	}

}
