package com.newrise.financemanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.service.AccountService;
import com.newrise.financemanagementsystem.service.ExpenseService;
import com.newrise.financemanagementsystem.service.LiabilityService;
import com.newrise.financemanagementsystem.service.LoanService;

@Controller
public class DashboardController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ExpenseService expenseService;
	@Autowired
	private LiabilityService liabilityService;
	@Autowired
	private LoanService loanService;

	@GetMapping("/accounts")
	public String showAccountsDashboard(Model model) {
		List<Account> accounts = Optional.ofNullable(accountService.getAllAccounts()).orElse(Collections.emptyList());
		model.addAttribute("accounts", accounts);
		return "account_dashboard";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		// Get total accounts
		int accountsCount = accountService.getAllAccounts().size();
		// Get financial summaries
		double totalExpenses = Optional.ofNullable(expenseService.getTotalExpenses()).orElse(0.0);
		double liabilitiesAmount = Optional.ofNullable(liabilityService.getTotalOutstandingAmount()).orElse(0.0);
		double loansAmount = Optional.ofNullable(loanService.getTotalOutstandingAmount()).orElse(0.0);
		// Get recent data for dashboard widgets
		List<Account> recentAccounts = accountService.getAllAccounts().stream().limit(5).toList();
		model.addAttribute("accountsCount", accountsCount);
		model.addAttribute("totalExpenses", totalExpenses);
		model.addAttribute("liabilitiesAmount", liabilitiesAmount);
		model.addAttribute("loansAmount", loansAmount);
		model.addAttribute("recentAccounts", recentAccounts);
		return "dashboard";
	}
}