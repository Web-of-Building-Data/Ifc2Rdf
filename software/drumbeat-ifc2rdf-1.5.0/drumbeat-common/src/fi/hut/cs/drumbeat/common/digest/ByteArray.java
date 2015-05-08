package fi.hut.cs.drumbeat.common.digest;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

public class ByteArray implements Comparable<ByteArray> {

	public byte[] array;
	private int hash;

	public ByteArray() {
	}

	public ByteArray(int length) {
		array = new byte[length];
	}

	public ByteArray(byte[] array) {
		this.array = array;
	}

	public ByteArray(byte[] array, boolean clone) {
		if (clone) {
			this.array = array.clone();
		} else {
			this.array = array;
		}
	}

	public byte[] getArray() {
		return array;
	}

	public void setArray(byte[] array) {
		array = this.array;
	}

	public void reset() {
		if (array != null) {
			for (int i = 0; i < array.length; ++i) {
				array[i] = 0;
			}
		}
	}

	public ByteArray and(ByteArray other) {
		return and(other.array);
	}

	public ByteArray and(byte[] operand) {
		for (int i = 0; i < operand.length; ++i) {
			this.array[i] &= operand[i];
		}
		return this;
	}

	public ByteArray or(ByteArray other) {
		return or(other.array);
	}

	public ByteArray or(byte[] operand) {
		for (int i = 0; i < operand.length; ++i) {
			array[i] |= operand[i];
		}
		return this;
	}

	public ByteArray xor(ByteArray other) {
		return xor(other.array);
	}

	public ByteArray xor(byte[] operand) {
		for (int i = 0; i < operand.length; ++i) {
			array[i] ^= operand[i];
		}
		return this;
	}

	public int compareTo(ByteArray other) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != other.array[i]) {
				return Byte.compare(array[i], other.array[i]);
			}
		}
		return 0;
	}
	
	public String toBase64String() {
		return Base64.encodeBase64URLSafeString(array);
	}

	@Override
	public boolean equals(Object other) {
		return Arrays.equals(array, ((ByteArray) other).array);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}
	
	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = Arrays.hashCode(array); 
		}
		return hash;
	}

}
