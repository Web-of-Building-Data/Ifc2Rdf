package fi.hut.cs.drumbeat.common.digest;

import java.security.InvalidParameterException;

import org.apache.commons.codec.digest.DigestUtils;

import fi.hut.cs.drumbeat.common.string.RegexUtils;


public enum EncoderTypeEnum {
	None,
	MD5,
	Base64,
	Base64SafeUrl,
	SafeUrl;
	
	public static String encode(EncoderTypeEnum encoderType, String s) {
		if (encoderType == None) {
			return s;
		} else if (encoderType == MD5) {
			return DigestUtils.md5Hex(s);
		} else if (encoderType == Base64) {
			return org.apache.commons.codec.binary.Base64.encodeBase64String(s.getBytes());
		} else if (encoderType == Base64SafeUrl) {
			return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(s.getBytes());			
		} else if (encoderType == SafeUrl) {
			return RegexUtils.getSafeUrl(s);
		}
		throw new InvalidParameterException("encoderType");
	}
}
