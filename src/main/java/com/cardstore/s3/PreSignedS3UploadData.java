package com.cardstore.s3;

public class PreSignedS3UploadData {
	private String errorMessage;
	private String fileName;
	private String contentType;
	private String credential;
	private String date;
	private String policy;
	private String signature;
	private String bucketUrl;
	
	public PreSignedS3UploadData() {
	}
	
	public PreSignedS3UploadData(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getCredential() {
		return credential;
	}
	public void setCredential(String credential) {
		this.credential = credential;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getBucketUrl() {
		return bucketUrl;
	}

	public void setBucketUrl(String bucketUrl) {
		this.bucketUrl = bucketUrl;
	}
	
}
