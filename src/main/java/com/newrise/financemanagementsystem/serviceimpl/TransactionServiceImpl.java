package com.newrise.financemanagementsystem.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.entity.TransactionType;
import com.newrise.financemanagementsystem.repository.AccountRepository;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.repository.TransactionRepository;
import com.newrise.financemanagementsystem.repository.TransactionTypeRepository;
import com.newrise.financemanagementsystem.service.TransactionService;

import jakarta.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	@Autowired
	private TransactionCategoryRepository transactionCategoryRepository;

	@Transactional
	public Transaction deposit(Long accountId, Double amount, Long categoryId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		account.setCurrentBalance(account.getCurrentBalance() + amount);
		TransactionType creditType = transactionTypeRepository.findByName("Credit")
				.orElseThrow(() -> new RuntimeException("TransactionType 'Credit' not found"));
		TransactionCategory category = transactionCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Transaction transaction = new Transaction();
		transaction.setAccount(account);
		transaction.setAmount(amount);
		transaction.setTransactionType(creditType);
		transaction.setCategory(category);
		accountRepository.save(account);
		return transactionRepository.save(transaction);
	}

	@Transactional
	public Transaction withdraw(Long accountId, Double amount, Long categoryId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		if (account.getCurrentBalance() < amount) {
			throw new RuntimeException("Insufficient balance");
		}
		account.setCurrentBalance(account.getCurrentBalance() - amount);
		TransactionType debitType = transactionTypeRepository.findByName("Debit")
				.orElseThrow(() -> new RuntimeException("TransactionType 'Debit' not found"));
		TransactionCategory category = transactionCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Transaction transaction = new Transaction();
		transaction.setAccount(account);
		transaction.setAmount(amount);
		transaction.setTransactionType(debitType);
		transaction.setCategory(category);
		accountRepository.save(account);
		return transactionRepository.save(transaction);
	}

	public List<Transaction> getAllTransactions() {
		return transactionRepository.findAll();
	}

	@Override
	public List<Transaction> getTransactionsByAccountId(Long accountId, int limit) {
		try {
			Pageable pageable = PageRequest.of(0, limit);
			return transactionRepository.findByAccount_AccountIdOrderByCreatedAtDesc(accountId, pageable);
		} catch (Exception e) {
			try {
				return transactionRepository.findTop10ByAccount_AccountIdOrderByCreatedAtDesc(accountId).stream()
						.limit(limit).collect(Collectors.toList());
			} catch (Exception ex) {
				return transactionRepository.findByAccount_AccountIdOrderByCreatedAtDesc(accountId).stream()
						.limit(limit).collect(Collectors.toList());
			}
		}
	}
}