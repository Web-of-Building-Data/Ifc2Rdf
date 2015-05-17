package fi.hut.cs.drumbeat.ifc.data;

import java.io.Serializable;

/**
 * A pair of two integerers: min and max 
 * @author Nam
 *
 */
public class Cardinality implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int UNBOUNDED = Integer.MAX_VALUE;
	
	private int minCardinality;
	private int maxCardinality;
	private int minIndex;
	private int maxIndex;
	private boolean isArrayIndex;
	
	public Cardinality(int min, int max, boolean isArrayIndex) {
		this.isArrayIndex = isArrayIndex;
		if (isArrayIndex) {
			this.minIndex = min;
			this.maxCardinality = max;
			this.minCardinality = max - min + 1;
			this.maxCardinality = this.minCardinality;
		} else {
			this.minCardinality = min;
			this.maxCardinality = max;
			this.minIndex = 1;
			this.maxIndex = this.minIndex + maxCardinality - 1;
		}
	}
	
	public boolean isArrayIndex() {
		return isArrayIndex;
	}
	
	public boolean isCardinalityFixed() {
		return minCardinality == maxCardinality;
	}
	
	public boolean isMultiple() {
		return maxCardinality > ONE;
	}
	
	public boolean isSingle() {
		return maxCardinality <= ONE;
	}	
	
	public int getMinCardinality() {
		return minCardinality;
	}
	
	public int getMaxCardinality() {
		return maxCardinality;
	}
	
	public int getMinIndex() {
		return minIndex;
	}
	
	public int getMaxIndex() {
		return maxIndex;
	}
	
	public CardinalityTypeEnum getMinType() {
		return CardinalityTypeEnum.getType(minCardinality);
	}
	
	public CardinalityTypeEnum getMaxType() {
		return CardinalityTypeEnum.getType(maxCardinality);
	}
	
	@Override
	public String toString() {			
		return String.format("[%s:%s]", getMinType(), getMaxType());
	}
	
	public String toShortString() {
		if (maxCardinality == 1) {
			return minCardinality == 0 ? "1?" : "1";
		} else if (minCardinality == 0) {
			return "N?";
		}  else {
			return minCardinality == 1 ? "N+" : "N";
		}		
	}

}
