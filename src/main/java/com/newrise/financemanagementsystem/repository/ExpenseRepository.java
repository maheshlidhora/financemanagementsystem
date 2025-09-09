package com.newrise.financemanagementsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newrise.financemanagementsystem.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByAccount_AccountIdOrderByExpenseDateDesc(Long accountId);

	List<Expense> findByCategory_CategoryIdOrderByExpenseDateDesc(Long categoryId);

	List<Expense> findAllByOrderByExpenseDateDesc();

	@Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
	Double getTotalExpenses();

	@Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.account.accountId = :accountId")
	Double getTotalExpensesByAccount(@Param("accountId") Long accountId);

	@Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.category.categoryId = :categoryId")
	Double getTotalExpensesByCategory(@Param("categoryId") Long categoryId);

	@Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
	List<Expense> findExpensesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// Add this missing method
	@Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
	Double getTotalExpensesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}