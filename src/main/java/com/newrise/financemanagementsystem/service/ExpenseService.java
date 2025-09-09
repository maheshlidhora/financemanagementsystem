package com.newrise.financemanagementsystem.service;

import java.util.List;
import java.util.Optional;

import com.newrise.financemanagementsystem.entity.Expense;

public interface ExpenseService {
	Expense createExpense(Expense expense);

	void deleteExpense(Long expenseId);

	Expense updateExpense(Long expenseId, Expense updatedExpense);

	List<Expense> getAllExpenses();

	Optional<Expense> getExpenseById(Long expenseId);

	List<Expense> getExpensesByAccount(Long accountId);

	List<Expense> getExpensesByCategory(Long categoryId);

	List<Expense> getExpensesByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);

	Double getTotalExpenses();

	Double getTotalExpensesByAccount(Long accountId);

	Double getTotalExpensesByCategory(Long categoryId);

	Double getTotalExpensesByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
