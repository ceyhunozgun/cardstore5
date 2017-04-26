package com.cardstore.dao;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.cardstore.dao")
public class DynamoDBConfig {

	@Value("${amazon.dynamodb.endpoint}")
	private String amazonDynamoDBEndpoint;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();

		if (amazonDynamoDBEndpoint != null && !amazonDynamoDBEndpoint.equals("")) {
			amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
		} else
			amazonDynamoDB.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
		return amazonDynamoDB;
	}
}