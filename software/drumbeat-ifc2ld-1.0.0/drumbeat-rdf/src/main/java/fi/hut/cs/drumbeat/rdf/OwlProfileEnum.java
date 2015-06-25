package fi.hut.cs.drumbeat.rdf;

import java.util.EnumSet;

public enum OwlProfileEnum {
	OWL1_Lite,
	OWL1_DL,
	OWL1_Full,		
	OWL2_EL,
	OWL2_QL,
	OWL2_RL,
	OWL2_DL,
	OWL2_Full;
	
	public static final EnumSet<OwlProfileEnum> OWL1 = EnumSet.of(OWL1_Lite, OWL1_DL, OWL1_Full);		
	public static final EnumSet<OwlProfileEnum> OWL2 = EnumSet.of(OWL2_EL, OWL2_QL, OWL2_RL, OWL2_DL, OWL2_Full);
	
	public float toVersion() {
		if (OWL1.contains(this)) {
			return OwlProfile.OWL_VERSION_1_0;			
		} else if (OWL2.contains(this)) {
			return OwlProfile.OWL_VERSION_2_0;			
		} else {
			return 0.0f;
		}
	}

}
