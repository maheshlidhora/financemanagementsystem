package com.newrise.financemanagementsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long transactionId;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	// Credit or Debit
	@ManyToOne
	@JoinColumn(name = "transaction_type_id", nullable = false)
	private TransactionType transactionType;

	// Expense, Loan Payment, Income, etc.
	@ManyToOne
	@JoinColumn(name = "category_id", nullable = true)
	private TransactionCategory category;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
	}

	@Column(name = "description", length = 500)
	private String description;

	@ManyToOne
	@JoinColumn(name = "expense_id")
	private Expense expense;

	@ManyToOne
	@JoinColumn(name = "liability_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Liability liability;

	@ManyToOne
	@JoinColumn(name = "loan_id")
	private Loan loan;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public Liability getLiability() {
		return liability;
	}

	public void setLiability(Liability liability) {
		this.liability = liability;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public Transaction(Long transactionId, Double amount, Account account, TransactionType transactionType,
			TransactionCategory category, LocalDateTime createdAt, String description, Expense expense,
			Liability liability, Loan loan) {
		super();
		this.transactionId = transactionId;
		this.amount = amount;
		this.account = account;
		this.transactionType = transactionType;
		this.category = category;
		this.createdAt = createdAt;
		this.description = description;
		this.expense = expense;
		this.liability = liability;
		this.loan = loan;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}
}
