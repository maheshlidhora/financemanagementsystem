package com.newrise.financemanagementsystem.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.AccountType;

public interface AccountService {
	Account createAccount(Account account);

	Account updateAccount(Long accountId, Account account);

	void deleteAccount(Long accountId);

	Optional<Account> getAccountById(Long accountId);

	Optional<Account> getAccountByName(String accountName);

	List<Account> getAllAccounts();

	Set<Account> getAccountsByType(AccountType accountType);

	void restoreAccount(Long accountId);
}
