package com.cardstore.ses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

@Configuration
public class SESConfig {
	@Value("${mail.from.address}")
	String mailFromAddress;

	@Bean
	public AmazonSimpleEmailService amazonSimpleEmailService() {
		AmazonSimpleEmailService client = new AmazonSimpleEmailServiceClient();
		client.setRegion(Region.getRegion(Regions.EU_WEST_1));
		return client;
	}

	public String getFromAddress() {
		return mailFromAddress;
	}
}