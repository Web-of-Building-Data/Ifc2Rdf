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
	
	private int min;
	private int max;
	
	public Cardinality(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean isOptional() {
		return min == ZERO;
	}
	
	public boolean isMultiple() {
		return max > ONE;
	}
	
	public void setOptional(boolean optional) {
		if (optional) {
			min = ZERO;
		}
	}
	
	public boolean isSingle() {
		return max <= ONE;
	}	
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public int setMin(int min) {
		return this.min = min;
	}
	
	public int setMax(int max) {
		return this.max = max;
	}

	public CardinalityTypeEnum getMinType() {
		return CardinalityTypeEnum.getType(min);
	}
	
	public CardinalityTypeEnum getMaxType() {
		return CardinalityTypeEnum.getType(max);
	}
	
	@Override
	public String toString() {			
		return String.format("[%s:%s]", getMinType(), getMaxType());
	}
	
	public String toShortString() {
		if (max == 1) {
			return min == 0 ? "1?" : "1";
		} else if (min == 0) {
			return "N?";
		}  else {
			return min == 1 ? "N+" : "N";
		}		
	}

}
