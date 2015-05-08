package fi.hut.cs.drumbeat.ifc.data;

/**
 * Enumeration of cardinality types: 0, 1 or many (N)
 * @author Nam
 *
 */
public enum CardinalityTypeEnum implements Comparable<CardinalityTypeEnum> {
	
	ZERO("0"),
	ONE("1"),
	MANY("N");
	
	private final String value;	
	
	private CardinalityTypeEnum(String value) {
		this.value = value;
	}
	
	
	/**
	 * Returns the value string
	 * @return "0" for ZERO, "1" for ONE, and "N" for MANY
	 * 
	 */
	@Override
	public String toString() {
		return value;
	}
	
	
	/**
	 * Analyzes a cardinality value and returns its type
	 * @param cardinality
	 * @return ZERO if cardinality is 0,
	 *         ONE if cardinality is 1,
	 *         or MANY for all other cases
	 */
	public static CardinalityTypeEnum getType(int cardinality) {
		switch (cardinality) {
		case 0:
			return ZERO;
		case 1:
			return ONE;
		default:
			return MANY;
		}
	}	

}
