package com.newrise.financemanagementsystem.service;

import java.util.List;

import com.newrise.financemanagementsystem.entity.Transaction;

public interface TransactionService {
	Transaction deposit(Long accountId, Double amount, Long categoryId);

	Transaction withdraw(Long accountId, Double amount, Long categoryId);

	List<Transaction> getAllTransactions();

	List<Transaction> getTransactionsByAccountId(Long accountId, int limit);
}