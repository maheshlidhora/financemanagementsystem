package com.newrise.financemanagementsystem.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long loanId;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Double principalAmount;

	@Column(name = "outstanding_amount", nullable = false)
	private Double outstandingAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanStatus status = LoanStatus.ACTIVE;

	@Column(name = "issue_date", nullable = false)
	private LocalDate issueDate;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account linkedAccount;

	@Column(name = "is_borrowed", nullable = false)
	private Boolean isBorrowed; // true = loan received, false = loan given

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		if (outstandingAmount == null) {
			outstandingAmount = principalAmount;
		}
		if (issueDate == null) {
			issueDate = LocalDate.now();
		}
	}

	public enum LoanType {
		PERSONAL, BUSINESS, EDUCATION, MORTGAGE, AUTO, OTHER
	}

	public enum LoanStatus {
		ACTIVE, PARTIAL, PAID, DEFAULTED, CLOSED
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(Double principalAmount) {
		this.principalAmount = principalAmount;
	}

	public Double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(Double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public LoanType getType() {
		return type;
	}

	public void setType(LoanType type) {
		this.type = type;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Account getLinkedAccount() {
		return linkedAccount;
	}

	public void setLinkedAccount(Account linkedAccount) {
		this.linkedAccount = linkedAccount;
	}

	public Boolean getIsBorrowed() {
		return isBorrowed;
	}

	public void setIsBorrowed(Boolean isBorrowed) {
		this.isBorrowed = isBorrowed;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Loan(Long loanId, String description, Double principalAmount, Double outstandingAmount, LoanType type,
			LoanStatus status, LocalDate issueDate, LocalDate dueDate, Account linkedAccount, Boolean isBorrowed,
			LocalDateTime createdAt) {
		super();
		this.loanId = loanId;
		this.description = description;
		this.principalAmount = principalAmount;
		this.outstandingAmount = outstandingAmount;
		this.type = type;
		this.status = status;
		this.issueDate = issueDate;
		this.dueDate = dueDate;
		this.linkedAccount = linkedAccount;
		this.isBorrowed = isBorrowed;
		this.createdAt = createdAt;
	}

	public Loan() {
		super();
		// TODO Auto-generated constructor stub
	}
}
