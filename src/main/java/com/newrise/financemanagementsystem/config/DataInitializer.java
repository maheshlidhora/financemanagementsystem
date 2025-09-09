package com.newrise.financemanagementsystem.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.newrise.financemanagementsystem.entity.AccountType;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.entity.TransactionType;
import com.newrise.financemanagementsystem.entity.User;
import com.newrise.financemanagementsystem.repository.AccountTypeRepository;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.repository.TransactionTypeRepository;
import com.newrise.financemanagementsystem.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	@Autowired
	private TransactionCategoryRepository transactionCategoryRepository;

	@Override
	public void run(String... args) throws Exception {
		String defaultEmail = "mahesh@gmail.com";
		String defaultPassword = "123456";
		// Check if default user exists
		if (userRepository.findByEmail(defaultEmail).isEmpty()) {
			User user = new User();
			user.setEmail(defaultEmail);
			user.setPassword(defaultPassword);
			userRepository.save(user);
			System.err.println("Default user created: " + defaultEmail);
		}
		// Default Account Types
		List<String> defaultAccountTypes = Arrays.asList("Cash", "Bank", "Wallet", "Credit Card", "Loan", "Liability");
		for (String typeName : defaultAccountTypes) {
			if (accountTypeRepository.findByName(typeName).isEmpty()) {
				AccountType accountType = new AccountType();
				accountType.setName(typeName);
				accountTypeRepository.save(accountType);
				System.err.println("AccountType created: " + typeName);
			}
		}
		// Default Transaction Types
		List<String> defaultTransactionTypes = Arrays.asList("Credit", "Debit");
		for (String typeName : defaultTransactionTypes) {
			if (transactionTypeRepository.findByName(typeName).isEmpty()) {
				TransactionType transactionType = new TransactionType();
				transactionType.setName(typeName);
				transactionTypeRepository.save(transactionType);
				System.err.println("TransactionType created: " + typeName);
			}
		}
		// Default Transaction Categories
		List<String> defaultCategories = Arrays.asList("Income", "Expense", "Transfer", "Loan Credit",
				"Loan Repayment");
		for (String categoryName : defaultCategories) {
			if (transactionCategoryRepository.findByName(categoryName).isEmpty()) {
				TransactionCategory category = new TransactionCategory();
				category.setName(categoryName);
				transactionCategoryRepository.save(category);
				System.err.println("TransactionCategory created: " + categoryName);
			}
		}
	}
}
