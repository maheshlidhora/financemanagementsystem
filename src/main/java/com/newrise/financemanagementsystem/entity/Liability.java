package com.newrise.financemanagementsystem.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "liabilities")
public class Liability {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long liabilityId;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Double amount;

	@Column(name = "outstanding_amount", nullable = false)
	private Double outstandingAmount;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LiabilityStatus status = LiabilityStatus.OUTSTANDING;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// Add these two missing fields
	@Column(name = "interest_rate")
	private Double interestRate;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@OneToMany(mappedBy = "liability")
	private List<Transaction> transactions;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		if (outstandingAmount == null) {
			outstandingAmount = amount;
		}
	}

	public enum LiabilityStatus {
		OUTSTANDING, PARTIAL, PAID, OVERDUE
	}

	// Getters and setters for all fields including the new ones

	public Long getId() {
		return liabilityId;
	}

	public Long getLiabilityId() {
		return liabilityId;
	}

	public void setLiabilityId(Long liabilityId) {
		this.liabilityId = liabilityId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(Double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public LiabilityStatus getStatus() {
		return status;
	}

	public void setStatus(LiabilityStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	// Add getters and setters for the new fields
	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Liability(Long liabilityId, String description, Double amount, Double outstandingAmount, LocalDate dueDate,
			LiabilityStatus status, LocalDateTime createdAt, Double interestRate, String notes, Account account,
			List<Transaction> transactions) {
		super();
		this.liabilityId = liabilityId;
		this.description = description;
		this.amount = amount;
		this.outstandingAmount = outstandingAmount;
		this.dueDate = dueDate;
		this.status = status;
		this.createdAt = createdAt;
		this.interestRate = interestRate;
		this.notes = notes;
		this.account = account;
		this.transactions = transactions;
	}

	// Constructors
	public Liability() {
	}
}