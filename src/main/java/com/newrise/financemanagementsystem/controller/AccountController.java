package com.newrise.financemanagementsystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.AccountType;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.repository.AccountTypeRepository;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.service.AccountService;
import com.newrise.financemanagementsystem.service.TransactionService;
import com.newrise.financemanagementsystem.service.UserService;

@Controller
public class AccountController {
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private TransactionCategoryRepository transactionCategoryRepository;

	@GetMapping("/")
	public String homePage() {
		return "login";
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		boolean success = userService.loginUser(email, password).isPresent();
		if (success) {
			return "redirect:/dashboard";
		} else {
			model.addAttribute("error", "❌ Invalid email or password");
			return "login";
		}
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/login";
	}

	// Show create account form
	@GetMapping("/accounts/create")
	public String showCreateAccountForm(Model model) {
		model.addAttribute("accountTypes", accountTypeRepository.findAll());
		model.addAttribute("account", new Account());
		return "create_account";
	}

	// Handle create account form
	@PostMapping("/accounts/create")
	public String createAccount(@ModelAttribute("account") Account account,
			@RequestParam("accountTypeId") Long accountTypeId) {
		Optional<AccountType> accountType = accountTypeRepository.findById(accountTypeId);
		if (accountType.isPresent()) {
			account.setAccountType(accountType.get());
			// Set currentBalance equal to openingBalance if not set
			if (account.getCurrentBalance() == null) {
				account.setCurrentBalance(account.getOpeningBalance());
			}
			accountService.createAccount(account);
		}
		return "redirect:/accounts";
	}

	// Show edit account form
	@GetMapping("/accounts/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		Optional<Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			model.addAttribute("account", account.get());
			model.addAttribute("accountTypes", accountTypeRepository.findAll());
			return "edit_account";
		} else {
			return "redirect:/accounts";
		}
	}

	// Handle update account form
	@PostMapping("/accounts/edit")
	public String updateAccount(@ModelAttribute("account") Account account) {
		if (account.getAccountType() != null && account.getAccountType().getId() != null) {
			AccountType type = accountTypeRepository.findById(account.getAccountType().getId())
					.orElseThrow(() -> new RuntimeException("AccountType not found"));
			account.setAccountType(type);
		}
		accountService.updateAccount(account.getAccountId(), account);
		return "redirect:/accounts";
	}

	// Delete account
	@GetMapping("/accounts/delete/{id}")
	public String deleteAccount(@PathVariable("id") Long id) {
		accountService.deleteAccount(id);
		return "redirect:/accounts";
	}

	@GetMapping("/accounts/{id}")
	public String viewAccountDetails(@PathVariable("id") Long id, Model model) {
		Optional<Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			model.addAttribute("account", account.get());
			// Get recent transactions (last 10)
			List<Transaction> transactions = transactionService.getTransactionsByAccountId(id, 10);
			model.addAttribute("transactions", transactions);
			// Get categories for dropdowns
			List<TransactionCategory> categories = transactionCategoryRepository.findAll();
			model.addAttribute("categories", categories);
			return "account_details";
		} else {
			return "redirect:/accounts";
		}
	}

	// Add these endpoints for deposit/withdraw processing
	@PostMapping("/transactions/deposit")
	public String processDeposit(@RequestParam Long accountId, @RequestParam Double amount,
			@RequestParam Long categoryId) {
		try {
			transactionService.deposit(accountId, amount, categoryId);
			return "redirect:/accounts/" + accountId + "?success=Deposit+successful";
		} catch (Exception e) {
			return "redirect:/accounts/" + accountId + "?error=Deposit+failed: " + e.getMessage();
		}
	}

	@PostMapping("/transactions/withdraw")
	public String processWithdraw(@RequestParam Long accountId, @RequestParam Double amount,
			@RequestParam Long categoryId) {
		try {
			transactionService.withdraw(accountId, amount, categoryId);
			return "redirect:/accounts/" + accountId + "?success=Withdrawal+successful";
		} catch (Exception e) {
			return "redirect:/accounts/" + accountId + "?error=" + e.getMessage();
		}
	}
}
