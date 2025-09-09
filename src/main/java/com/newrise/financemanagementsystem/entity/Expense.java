package com.newrise.financemanagementsystem.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long expenseId;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Double amount;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private TransactionCategory category;

	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "notes")
	private String notes;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		if (expenseDate == null) {
			expenseDate = LocalDate.now();
		}
	}

	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}

	public LocalDate getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Expense(Long expenseId, String description, Double amount, Account account, TransactionCategory category,
			LocalDate expenseDate, LocalDateTime createdAt, String notes) {
		super();
		this.expenseId = expenseId;
		this.description = description;
		this.amount = amount;
		this.account = account;
		this.category = category;
		this.expenseDate = expenseDate;
		this.createdAt = createdAt;
		this.notes = notes;
	}

	public Expense() {
		super();
		// TODO Auto-generated constructor stub
	}
}