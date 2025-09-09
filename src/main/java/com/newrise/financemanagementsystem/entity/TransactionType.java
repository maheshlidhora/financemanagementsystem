package com.newrise.financemanagementsystem.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_types")
public class TransactionType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_type_id")
	private Long id;

	@Column(name = "name", nullable = false, unique = true, length = 50)
	private String name; // Credit / Debit

	@OneToMany(mappedBy = "transactionType", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Transaction> transactions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public TransactionType(Long id, String name, Set<Transaction> transactions) {
		super();
		this.id = id;
		this.name = name;
		this.transactions = transactions;
	}

	public TransactionType() {
		super();
		// TODO Auto-generated constructor stub
	}
}
