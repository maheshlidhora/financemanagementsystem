package com.newrise.financemanagementsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	List<Loan> findByStatus(Loan.LoanStatus status);

	List<Loan> findByType(Loan.LoanType type);

	List<Loan> findByIsBorrowedTrue();

	List<Loan> findByIsBorrowedFalse();

	List<Loan> findByDueDateBeforeAndStatusNot(LocalDate date, Loan.LoanStatus status);

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Loan l")
	Double getTotalOutstandingAmount();

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Loan l WHERE l.isBorrowed = true")
	Double getTotalOutstandingBorrowedAmount();

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Loan l WHERE l.isBorrowed = false")
	Double getTotalOutstandingGivenAmount();

	@Query("SELECT l FROM Loan l WHERE l.linkedAccount.accountId = :accountId")
	List<Loan> findByAccountId(@Param("accountId") Long accountId);

	List<Loan> findByLinkedAccount(Account account);
}