package com.newrise.financemanagementsystem.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Expense;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionType;
import com.newrise.financemanagementsystem.repository.AccountRepository;
import com.newrise.financemanagementsystem.repository.ExpenseRepository;
import com.newrise.financemanagementsystem.repository.TransactionRepository;
import com.newrise.financemanagementsystem.repository.TransactionTypeRepository;
import com.newrise.financemanagementsystem.service.ExpenseService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;

	@Override
	@Transactional
	public Expense createExpense(Expense expense) {
		if (expense.getAmount() <= 0) {
			throw new RuntimeException("Expense amount must be positive");
		}
		Account account = expense.getAccount();
		if (account == null || account.getAccountId() == null) {
			throw new RuntimeException("Account is required for expense");
		}
		Account managedAccount = accountRepository.findById(account.getAccountId())
				.orElseThrow(() -> new RuntimeException("Account not found"));
		if (managedAccount.getCurrentBalance() < expense.getAmount()) {
			throw new RuntimeException("Insufficient balance in account: " + managedAccount.getAccountName());
		}
		managedAccount.setCurrentBalance(managedAccount.getCurrentBalance() - expense.getAmount());
		accountRepository.save(managedAccount);
		createExpenseTransaction(expense, managedAccount);
		return expenseRepository.save(expense);
	}

	private void createExpenseTransaction(Expense expense, Account account) {
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(expense.getAmount());
			transaction.setCategory(expense.getCategory());
			TransactionType debitType = transactionTypeRepository.findByName("Debit")
					.orElseThrow(() -> new RuntimeException("TransactionType 'Debit' not found"));
			transaction.setTransactionType(debitType);
			transaction.setDescription("Expense: " + expense.getDescription());
			transactionRepository.save(transaction);
		} catch (Exception e) {
			System.err.println("Failed to create transaction record: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteExpense(Long expenseId) {
		Expense expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
		Account account = expense.getAccount();
		if (account != null) {
			Account managedAccount = accountRepository.findById(account.getAccountId())
					.orElseThrow(() -> new RuntimeException("Account not found"));
			managedAccount.setCurrentBalance(managedAccount.getCurrentBalance() + expense.getAmount());
			accountRepository.save(managedAccount);
			createRefundTransaction(expense, managedAccount);
		}
		expenseRepository.deleteById(expenseId);
	}

	private void createRefundTransaction(Expense expense, Account account) {
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(expense.getAmount());
			transaction.setCategory(expense.getCategory());
			TransactionType creditType = transactionTypeRepository.findByName("Credit")
					.orElseThrow(() -> new RuntimeException("TransactionType 'Credit' not found"));
			transaction.setTransactionType(creditType);
			transaction.setDescription("Expense Refund: " + expense.getDescription());
			transactionRepository.save(transaction);
		} catch (Exception e) {
			System.err.println("Failed to create refund transaction: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public Expense updateExpense(Long expenseId, Expense updatedExpense) {
		Expense existingExpense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
		Double oldAmount = existingExpense.getAmount();
		Double newAmount = updatedExpense.getAmount();
		existingExpense.setDescription(updatedExpense.getDescription());
		existingExpense.setAmount(newAmount);
		existingExpense.setExpenseDate(updatedExpense.getExpenseDate());
		existingExpense.setCategory(updatedExpense.getCategory());
		if (!existingExpense.getAccount().getAccountId().equals(updatedExpense.getAccount().getAccountId())
				|| !oldAmount.equals(newAmount)) {
			Account oldAccount = existingExpense.getAccount();
			Account newAccount = updatedExpense.getAccount();
			oldAccount.setCurrentBalance(oldAccount.getCurrentBalance() + oldAmount);
			accountRepository.save(oldAccount);
			if (newAccount.getCurrentBalance() < newAmount) {
				throw new RuntimeException("Insufficient balance in new account");
			}
			newAccount.setCurrentBalance(newAccount.getCurrentBalance() - newAmount);
			accountRepository.save(newAccount);
			existingExpense.setAccount(newAccount);
		}
		return expenseRepository.save(existingExpense);
	}

	@Override
	public List<Expense> getAllExpenses() {
		return expenseRepository.findAllByOrderByExpenseDateDesc();
	}

	@Override
	public Optional<Expense> getExpenseById(Long expenseId) {
		return expenseRepository.findById(expenseId);
	}

	@Override
	public List<Expense> getExpensesByAccount(Long accountId) {
		return expenseRepository.findByAccount_AccountIdOrderByExpenseDateDesc(accountId);
	}

	@Override
	public List<Expense> getExpensesByCategory(Long categoryId) {
		return expenseRepository.findByCategory_CategoryIdOrderByExpenseDateDesc(categoryId);
	}

	@Override
	public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
		return expenseRepository.findExpensesByDateRange(startDate, endDate);
	}

	@Override
	public Double getTotalExpenses() {
		return expenseRepository.getTotalExpenses();
	}

	@Override
	public Double getTotalExpensesByAccount(Long accountId) {
		return expenseRepository.getTotalExpensesByAccount(accountId);
	}

	@Override
	public Double getTotalExpensesByCategory(Long categoryId) {
		return expenseRepository.getTotalExpensesByCategory(categoryId);
	}

	@Override
	public Double getTotalExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
		return expenseRepository.getTotalExpensesByDateRange(startDate, endDate);
	}
}