package com.cardstore.entity;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Card")
public class Card implements Cloneable {
	@Id
	private CardId cardId;

	private String dateLoaded;
	private String imageUrl;
	private boolean inSale;
	private double price;

	public Card() {
	}

	public Card(String owner, String name, String dateLoaded, String imageUrl) {
		super();
		cardId = new CardId(owner, name);
		this.dateLoaded = dateLoaded;
		this.imageUrl = imageUrl;
	}

	@DynamoDBHashKey(attributeName = "owner")
	public String getOwner() {
		return cardId == null ? null : cardId.getOwner();
	}

	public void setOwner(String owner) {
		if (cardId == null)
			cardId = new CardId(owner, null);
		else
			cardId.setOwner(owner);
	}

	@DynamoDBRangeKey(attributeName = "name")
	public String getName() {
		return cardId == null ? null : cardId.getName();
	}

	public void setName(String name) {
		if (cardId == null)
			cardId = new CardId(null, name);
		else
			cardId.setName(name);
	}

	public String getDateLoaded() {
		return dateLoaded;
	}

	public void setDateLoaded(String dateLoaded) {
		this.dateLoaded = dateLoaded;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isInSale() {
		return inSale;
	}

	public void setInSale(boolean inSale) {
		this.inSale = inSale;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Card clone() throws CloneNotSupportedException {
		Card newCard = new Card(getOwner(), getName(), getDateLoaded(), getImageUrl());

		newCard.setPrice(getPrice());
		return newCard;
	}
}
