package com.newrise.financemanagementsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.AccountType;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByAccountName(String accountName);

	Set<Account> findByAccountType(AccountType accountType);

	Optional<Account> findByAccountNameAndIsActiveTrue(String accountName);

	Optional<Account> findByAccountIdAndIsActiveTrue(Long accountId);

	List<Account> findByIsActiveTrue();

	List<Account> findByAccountTypeAndIsActiveTrue(AccountType accountType);

	long countByIsActiveTrue();

	@Query("SELECT a FROM Account a WHERE a.accountName = :accountName")
	Optional<Account> findByAccountNameIncludingInactive(@Param("accountName") String accountName);

	@Query("SELECT a FROM Account a")
	List<Account> findAllIncludingInactive();
}