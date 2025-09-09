package com.newrise.financemanagementsystem.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.AccountType;
import com.newrise.financemanagementsystem.repository.AccountRepository;
import com.newrise.financemanagementsystem.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Account createAccount(Account account) {
		if (account.getCurrentBalance() == null) {
			account.setCurrentBalance(account.getOpeningBalance());
		}
		if (account.getIsActive() == null) {
			account.setIsActive(true);
		}
		return accountRepository.save(account);
	}

	@Override
	public Account updateAccount(Long accountId, Account updatedAccount) {
		Optional<Account> optionalAccount = accountRepository.findByAccountIdAndIsActiveTrue(accountId);
		if (optionalAccount.isPresent()) {
			Account existingAccount = optionalAccount.get();
			existingAccount.setAccountName(updatedAccount.getAccountName());
			existingAccount.setOpeningBalance(updatedAccount.getOpeningBalance());
			existingAccount.setCurrentBalance(updatedAccount.getCurrentBalance());
			existingAccount.setAccountType(updatedAccount.getAccountType());
			existingAccount.setDescription(updatedAccount.getDescription());
			return accountRepository.save(existingAccount);
		} else {
			throw new RuntimeException("Account not found with ID: " + accountId);
		}
	}

	@Override
	@Transactional
	public void deleteAccount(Long accountId) {
		Account account = accountRepository.findByAccountIdAndIsActiveTrue(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

		// Soft delete - mark as inactive
		account.setIsActive(false);
		accountRepository.save(account);
	}

	@Override
	public Optional<Account> getAccountById(Long accountId) {
		return accountRepository.findByAccountIdAndIsActiveTrue(accountId);
	}

	@Override
	public Optional<Account> getAccountByName(String accountName) {
		return accountRepository.findByAccountNameAndIsActiveTrue(accountName);
	}

	@Override
	public List<Account> getAllAccounts() {
		return accountRepository.findByIsActiveTrue(); // Only active accounts
	}

	@Override
	public Set<Account> getAccountsByType(AccountType accountType) {
		return accountRepository.findByAccountTypeAndIsActiveTrue(accountType).stream().collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public void restoreAccount(Long accountId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

		account.setIsActive(true);
		accountRepository.save(account);
	}

	public List<Account> getAllAccountsIncludingInactive() {
		return accountRepository.findAllIncludingInactive();
	}

	public boolean accountNameExists(String accountName) {
		return accountRepository.findByAccountNameIncludingInactive(accountName).isPresent();
	}
}