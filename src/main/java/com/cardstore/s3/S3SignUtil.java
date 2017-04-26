package com.cardstore.s3;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class S3SignUtil {
	
	public static String base64(String str) throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(str.getBytes("UTF-8"));
	}

	public static String base64(byte[] bytes) throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	static byte[] HmacSHA256(String data, byte[] key) throws Exception {
	    String algorithm="HmacSHA256";
	    Mac mac = Mac.getInstance(algorithm);
	    mac.init(new SecretKeySpec(key, algorithm));
	    return mac.doFinal(data.getBytes("UTF8"));
	}

	static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
	    byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
	    byte[] kDate = HmacSHA256(dateStamp, kSecret);
	    byte[] kRegion = HmacSHA256(regionName, kDate);
	    byte[] kService = HmacSHA256(serviceName, kRegion);
	    byte[] kSigning = HmacSHA256("aws4_request", kService);
	    return kSigning;
	}
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String generateSignature(String key, String dateStamp, String regionName, String serviceName, String stringToSign) throws Exception {
		return bytesToHex(HmacSHA256(stringToSign, getSignatureKey(key, dateStamp, regionName, serviceName)));
	}

	public static PreSignedS3UploadData generatePreSignedUploadData(String bucketName, String region, String awsId, String awsSecret,
			String contentType, String fileName) throws Exception {
		
		String shortDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
		String longDate = shortDate + "T000000Z";
		
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String expires = dateFormat.format(LocalDateTime.now().plusMinutes(3));
		
		int maxContentLength = 1024*1024;
	
		String credential = awsId +  "/" + shortDate + "/" + region + "/s3/aws4_request";
		
		PreSignedS3UploadData res = new PreSignedS3UploadData();
		String policy = "{ \"expiration\": \"" + expires + "\","+
						  "\"conditions\": [" +
						    "{\"acl\": \"public-read\" }," +
						    "{\"bucket\": \"" + bucketName + "\" }," +
						    "{\"key\": \"" + fileName +"\"}," +
						    "{\"Content-Type\": \"" + contentType + "\"}," +
						    "[\"content-length-range\", 0, " + maxContentLength + "]," +
						    "{\"x-amz-credential\":\"" + credential + "\"}," +
						    "{\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"}," +
						    "{\"x-amz-date\": \"" + longDate + "\"}" +
						  "]" +
						"}";
		
		System.out.println(policy);
		
		res.setPolicy(base64(policy));
		res.setCredential(credential);
		res.setDate(longDate);
		res.setContentType(contentType);
		res.setFileName(fileName);
		res.setSignature(generateSignature(awsSecret, shortDate, region, "s3", res.getPolicy()));
		res.setBucketUrl("https://" + bucketName + ".s3." + region + ".amazonaws.com");
		
		return res;
	}
}
