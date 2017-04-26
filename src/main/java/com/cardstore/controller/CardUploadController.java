package com.cardstore.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cardstore.entity.User;
import com.cardstore.s3.PreSignedS3UploadData;
import com.cardstore.s3.S3SignUtil;

@RestController
public class CardUploadController {

	@Value("${user.card.upload.s3.bucket.name}")
	String s3BucketName;

	@Value("${user.card.upload.s3.bucket.region}")
	String s3BucketRegion;

	@Value("${user.card.upload.s3.bucket.awsId}")
	String s3BucketAwsId;

	@Value("${user.card.upload.s3.bucket.awsSecret}")
	String s3BucketAwsSecret;

	@RequestMapping(value = "/presign", method = RequestMethod.POST)
	@ResponseBody
	public PreSignedS3UploadData presignS3Upload(@RequestParam("contentType") String contentType, @RequestParam("fileName") String fileName, HttpSession session) {
		PreSignedS3UploadData res; 
		try { 
			String extension = fileName.lastIndexOf('.') == -1 ? "" : fileName.substring(fileName.lastIndexOf('.'));
			String s3FileName = "upload_" + (int)(100000 * Math.random()) + extension;
			
			res = S3SignUtil.generatePreSignedUploadData(s3BucketName, s3BucketRegion, s3BucketAwsId, s3BucketAwsSecret, contentType, s3FileName);
		}
		catch (Exception e) {
			res = new PreSignedS3UploadData("Can't generate signature for upload: " + e.toString());
		}
		return res;
	}
}
