package fi.hut.cs.drumbeat.common.numbers;

public class IntegerWrapper {
	
	private int value;

	public IntegerWrapper() {
	}
	
	public IntegerWrapper(int value) {
		this.value = value;
	}

	public int intValue() {
		return value;
	}

	public int getValue() {
		return value;		
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int increase() {
		return ++value;
	}
	
	public int decrease() {
		return --value;
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}
	
	@Override
	public int hashCode() {
		return value;
	}

}
