package com.newrise.financemanagementsystem.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Liability;
import com.newrise.financemanagementsystem.entity.Loan;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	Set<Transaction> findByAccount(Account account);

	Set<Transaction> findByTransactionType(TransactionType transactionType);

	List<Transaction> findByAccount_AccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

	@Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId ORDER BY t.createdAt DESC")
	List<Transaction> findRecentTransactionsByAccountId(@Param("accountId") Long accountId, Pageable pageable);

	List<Transaction> findByAccount_AccountIdOrderByCreatedAtDesc(Long accountId);

	List<Transaction> findTop10ByAccount_AccountIdOrderByCreatedAtDesc(Long accountId);

	List<Transaction> findByLiability_LiabilityId(Long liabilityId);

	List<Transaction> findByLiability(Liability liability);

	List<Transaction> findByLoan(Loan loan);
}