package com.newrise.financemanagementsystem.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long accountId;

	@Column(name = "account_name", nullable = false, length = 100)
	private String accountName;

	@Column(name = "opening_balance", nullable = false)
	private Double openingBalance;

	@Column(name = "current_balance", nullable = false)
	private Double currentBalance;

	@ManyToOne
	@JoinColumn(name = "account_type_id", nullable = false)
	private AccountType accountType; // Cash, Bank, Wallet, etc.

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Transaction> transactions;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "description", length = 500)
	private String description;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(Double openingBalance) {
		this.openingBalance = openingBalance;
	}

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Account(Long accountId, String accountName, Double openingBalance, Double currentBalance,
			AccountType accountType, Set<Transaction> transactions, Boolean isActive, String description) {
		super();
		this.accountId = accountId;
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = currentBalance;
		this.accountType = accountType;
		this.transactions = transactions;
		this.isActive = isActive;
		this.description = description;
	}

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}
}
