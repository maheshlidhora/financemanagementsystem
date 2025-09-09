package com.newrise.financemanagementsystem.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Expense;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.service.AccountService;
import com.newrise.financemanagementsystem.service.ExpenseService;

@Controller
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionCategoryRepository transactionCategoryRepository;

	@GetMapping("/expenses")
	public String listExpenses(Model model) {
		List<Expense> expenses = expenseService.getAllExpenses();
		model.addAttribute("expenses", expenses);
		model.addAttribute("totalExpenses", expenseService.getTotalExpenses());
		return "expenses/list";
	}

	@GetMapping("/expenses/create")
	public String showCreateExpenseForm(Model model) {
		List<Account> accounts = accountService.getAllAccounts();
		List<TransactionCategory> categories = transactionCategoryRepository.findAll();
		model.addAttribute("expense", new Expense());
		model.addAttribute("accounts", accounts);
		model.addAttribute("categories", categories);
		model.addAttribute("today", LocalDate.now());
		return "expenses/create";
	}

	@PostMapping("/expenses/create")
	public String createExpense(@ModelAttribute Expense expense, @RequestParam Long accountId,
			@RequestParam Long categoryId) {
		try {
			System.out.println("Creating expense: " + expense);
			System.out.println("AccountId: " + accountId + ", CategoryId: " + categoryId);

			Account account = accountService.getAccountById(accountId)
					.orElseThrow(() -> new RuntimeException("Account not found"));
			TransactionCategory category = transactionCategoryRepository.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("Category not found"));

			expense.setAccount(account);
			expense.setCategory(category);

			expenseService.createExpense(expense);
			return "redirect:/expenses?success=Expense+created+successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/expenses/create?error=" + e.getMessage();
		}
	}

	@GetMapping("/expenses/{id}")
	public String viewExpense(@PathVariable Long id, Model model) {
		Optional<Expense> expense = expenseService.getExpenseById(id);
		if (expense.isPresent()) {
			model.addAttribute("expense", expense.get());
			return "expenses/view";
		} else {
			return "redirect:/expenses?error=Expense+not+found";
		}
	}

	@GetMapping("/expenses/edit/{id}")
	public String showEditExpenseForm(@PathVariable Long id, Model model) {
		Optional<Expense> expense = expenseService.getExpenseById(id);
		if (expense.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			List<TransactionCategory> categories = transactionCategoryRepository.findAll();

			model.addAttribute("expense", expense.get());
			model.addAttribute("accounts", accounts);
			model.addAttribute("categories", categories);
			return "expenses/edit";
		} else {
			return "redirect:/expenses?error=Expense+not+found";
		}
	}

	@PostMapping("/expenses/edit/{id}")
	public String updateExpense(@PathVariable Long id, @ModelAttribute Expense expense, @RequestParam Long accountId,
			@RequestParam Long categoryId) {
		try {
			Account account = accountService.getAccountById(accountId)
					.orElseThrow(() -> new RuntimeException("Account not found"));
			TransactionCategory category = transactionCategoryRepository.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("Category not found"));

			expense.setAccount(account);
			expense.setCategory(category);

			expenseService.updateExpense(id, expense);
			return "redirect:/expenses?success=Expense+updated+successfully";
		} catch (Exception e) {
			return "redirect:/expenses/edit/" + id + "?error=" + e.getMessage();
		}
	}

	@GetMapping("/expenses/delete/{id}")
	public String deleteExpense(@PathVariable Long id) {
		try {
			expenseService.deleteExpense(id);
			return "redirect:/expenses?success=Expense+deleted+successfully";
		} catch (Exception e) {
			return "redirect:/expenses?error=" + e.getMessage();
		}
	}

	@GetMapping("/expenses/report")
	public String expenseReport(@RequestParam(required = false) LocalDate startDate,
			@RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long accountId, Model model) {

		Double totalExpenses = 0.0;
		List<Expense> expenses = expenseService.getAllExpenses();
		if (startDate != null && endDate != null) {
			expenses = expenseService.getExpensesByDateRange(startDate, endDate);
			totalExpenses = expenseService.getTotalExpensesByDateRange(startDate, endDate);
		} else if (categoryId != null) {
			expenses = expenseService.getExpensesByCategory(categoryId);
			totalExpenses = expenseService.getTotalExpensesByCategory(categoryId);
		} else if (accountId != null) {
			expenses = expenseService.getExpensesByAccount(accountId);
			totalExpenses = expenseService.getTotalExpensesByAccount(accountId);
		} else {
			totalExpenses = expenseService.getTotalExpenses();
		}
		List<Account> accounts = accountService.getAllAccounts();
		List<TransactionCategory> categories = transactionCategoryRepository.findAll();
		// Calculate report statistics
		long totalExpenseCount = expenses.size();
		// Calculate category-wise summary
		Map<String, Double> categorySummary = expenses.stream()
				.collect(Collectors.groupingBy(
						expense -> expense.getCategory() != null ? expense.getCategory().getName() : "Uncategorized",
						Collectors.summingDouble(Expense::getAmount)));
		// Calculate account-wise summary
		Map<String, Double> accountSummary = expenses.stream()
				.collect(Collectors.groupingBy(
						expense -> expense.getAccount() != null ? expense.getAccount().getAccountName() : "No Account",
						Collectors.summingDouble(Expense::getAmount)));
		model.addAttribute("expenses", expenses);
		model.addAttribute("totalExpenses", totalExpenses);
		model.addAttribute("totalExpenseCount", totalExpenseCount);
		model.addAttribute("accounts", accounts);
		model.addAttribute("categories", categories);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("selectedCategoryId", categoryId);
		model.addAttribute("selectedAccountId", accountId);
		model.addAttribute("categorySummary", categorySummary);
		model.addAttribute("accountSummary", accountSummary);
		// For charts
		model.addAttribute("categoryLabels", new ArrayList<>(categorySummary.keySet()));
		model.addAttribute("categoryData", new ArrayList<>(categorySummary.values()));
		model.addAttribute("accountLabels", new ArrayList<>(accountSummary.keySet()));
		model.addAttribute("accountData", new ArrayList<>(accountSummary.values()));
		return "expenses/report";
	}
}