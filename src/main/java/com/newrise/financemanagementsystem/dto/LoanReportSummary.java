package com.newrise.financemanagementsystem.dto;

public class LoanReportSummary {
	private String category;
	private long count;
	private double totalAmount;
	private double outstandingAmount;
	private double averageAmount;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public double getAverageAmount() {
		return averageAmount;
	}

	public void setAverageAmount(double averageAmount) {
		this.averageAmount = averageAmount;
	}
}
