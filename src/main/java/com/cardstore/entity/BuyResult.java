package com.cardstore.entity;

public class BuyResult {
	String error;
	double newBalance;

	public BuyResult(String error) {
		this.error = error;
	}

	public BuyResult(double newBalance) {
		this.newBalance = newBalance;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public double getNewBalance() {
		return newBalance;
	}

	public void setNewBalance(double newBalance) {
		this.newBalance = newBalance;
	}

}
