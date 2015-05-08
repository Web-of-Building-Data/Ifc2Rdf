package fi.hut.cs.drumbeat.ifc.common.guid;

import org.apache.commons.codec.binary.Base64;

/**
 * Converts {@link Guid} from/to byte arrays and {@link Base64} strings.
 * @author Nam
 *
 */
public class GuidConverter {
	
	
	/**
	 * Converters {@link Guid} to a byte array.
	 * @param guid guid value to converte.
	 * @return a 16-byte array
	 */
	public static byte[] convertGuidToByteArray(Guid guid) {
		byte[] buffer = new byte[16];
		for (int i = 0; i < 4; ++i) {
			buffer[i] = (byte)(guid.Data1 >>> 8 * (3 - i)); 
		}
		for (int i = 0; i < 2; ++i) {
			buffer[4 + i] = (byte)(guid.Data2 >>> 8 * (1 - i)); 
		}
		for (int i = 0; i < 2; ++i) {
			buffer[6 + i] = (byte)(guid.Data3 >>> 8 * (1 - i)); 
		}
		for (int i = 0; i < 8; ++i) {
			buffer[8 + i] = (byte)(guid.Data4[i]); 
		}
		return buffer;
	}
	
	/**
	 * Converts a 16-byte array to a {@link Guid}.
	 * @param byteArray a 16-byte array to convert.
	 * @return
	 */
	public static Guid convertByteArrayToGuid(byte[] byteArray) {
		Guid guid = new Guid();
		for (int i = 0; i < 4; ++i) {
			guid.Data1 = (guid.Data1 << 8) | (byteArray[i] & 0xFF); 
		}
		for (int i = 0; i < 2; ++i) {
			guid.Data2 = (guid.Data2 << 8) | (byteArray[4 + i] & 0xFF); 
		}
		for (int i = 0; i < 2; ++i) {
			guid.Data3 = (guid.Data3 << 8) | (byteArray[6 + i] & 0xFF); 
		}
		for (int i = 0; i < 8; ++i) {
			guid.Data4[i] = (char)(byteArray[8 + i] & 0xFF); 
		}
		return guid;
	}
	
	/**
	 * Converts a {@link Guid} to a safe {@link Base64} string.
	 * @param guid guid value to convert.
	 * @return a safe {@link Base64} string.
	 */
	public static String convertGuidToBase64String(Guid guid) {
		byte[] buffer = convertGuidToByteArray(guid);
		return Base64.encodeBase64URLSafeString(buffer);
	}
	
	/**
	 * Converts a {@link Base64} string to a {@link Guid}.
	 * @param base64String
	 * @return
	 */
	public static Guid convertBase64StringToGuid(String base64String) {
		byte[] buffer = Base64.decodeBase64(base64String);
		return convertByteArrayToGuid(buffer);
	}

}
