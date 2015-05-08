package fi.hut.cs.drumbeat.common.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestManager {
	
	public static MessageDigest getMessageDigest() {		
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}		
	}
	
//	public static int compare(byte[] checksum1, byte[] checksum2) {
//		for (int i = 0; i < checksum1.length; ++i) {
//			if (checksum1[i] != checksum2[i]) {
//				return Byte.compare(checksum1[i], checksum2[i]);
//			}
//		}
//		return 0;
//	}
//	
//	public static boolean areEqual(byte[] checksum1, byte[] checksum2) {
//		return Arrays.equals(checksum1, checksum2);
//	}

}
