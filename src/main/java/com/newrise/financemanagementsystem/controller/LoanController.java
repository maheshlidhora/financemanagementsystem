package com.newrise.financemanagementsystem.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.newrise.financemanagementsystem.dto.LoanReportSummary;
import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Loan;
import com.newrise.financemanagementsystem.service.AccountService;
import com.newrise.financemanagementsystem.service.LoanService;

@Controller
public class LoanController {
	@Autowired
	private LoanService loanService;
	@Autowired
	private AccountService accountService;

	@GetMapping("/loans")
	public String listLoans(Model model) {
		List<Loan> loans = loanService.getAllLoans();
		model.addAttribute("loans", loans);
		model.addAttribute("totalOutstanding", loanService.getTotalOutstandingAmount());
		model.addAttribute("totalBorrowed", loanService.getTotalOutstandingBorrowedAmount());
		model.addAttribute("totalGiven", loanService.getTotalOutstandingGivenAmount());
		return "loans/list";
	}

	@GetMapping("/loans/create")
	public String showCreateLoanForm(Model model) {
		List<Account> accounts = accountService.getAllAccounts();
		model.addAttribute("loan", new Loan());
		model.addAttribute("accounts", accounts);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("types", Loan.LoanType.values());
		model.addAttribute("statuses", Loan.LoanStatus.values());
		return "loans/create";
	}

	@PostMapping("/loans/create")
	public String createLoan(@ModelAttribute Loan loan, @RequestParam(required = false) Long accountId,
			@RequestParam Boolean isBorrowed) {
		try {
			if (accountId != null) {
				Account account = accountService.getAccountById(accountId)
						.orElseThrow(() -> new RuntimeException("Account not found"));
				loan.setLinkedAccount(account);
			}
			loan.setIsBorrowed(isBorrowed);
			loanService.createLoan(loan);
			String message = isBorrowed ? "Loan+received+successfully" : "Loan+given+successfully";
			return "redirect:/loans?success=" + message;
		} catch (Exception e) {
			return "redirect:/loans/create?error=" + e.getMessage();
		}
	}

	@GetMapping("/loans/{id}")
	public String viewLoan(@PathVariable Long id, Model model) {
		Optional<Loan> loan = loanService.getLoanById(id);
		if (loan.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("loan", loan.get());
			model.addAttribute("accounts", accounts);
			return "loans/view";
		} else {
			return "redirect:/loans?error=Loan+not+found";
		}
	}

	@GetMapping("/loans/edit/{id}")
	public String showEditLoanForm(@PathVariable Long id, Model model) {
		Optional<Loan> loan = loanService.getLoanById(id);
		if (loan.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("loan", loan.get());
			model.addAttribute("accounts", accounts);
			model.addAttribute("types", Loan.LoanType.values());
			model.addAttribute("statuses", Loan.LoanStatus.values());
			return "loans/edit";
		} else {
			return "redirect:/loans?error=Loan+not+found";
		}
	}

	@PostMapping("/loans/edit/{id}")
	public String updateLoan(@PathVariable Long id, @ModelAttribute Loan loan,
			@RequestParam(required = false) Long accountId, @RequestParam Boolean isBorrowed) {
		try {
			if (accountId != null) {
				Account account = accountService.getAccountById(accountId)
						.orElseThrow(() -> new RuntimeException("Account not found"));
				loan.setLinkedAccount(account);
			}
			loan.setIsBorrowed(isBorrowed);
			loanService.updateLoan(id, loan);
			return "redirect:/loans?success=Loan+updated+successfully";
		} catch (Exception e) {
			return "redirect:/loans/edit/" + id + "?error=" + e.getMessage();
		}
	}

	@GetMapping("/loans/delete/{id}")
	public String deleteLoan(@PathVariable Long id) {
		try {
			loanService.deleteLoan(id);
			return "redirect:/loans?success=Loan+deleted+successfully";
		} catch (Exception e) {
			return "redirect:/loans?error=" + e.getMessage();
		}
	}

	@GetMapping("/loans/payment/{id}")
	public String showLoanPaymentForm(@PathVariable Long id, Model model) {
		Optional<Loan> loan = loanService.getLoanById(id);
		if (loan.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("loan", loan.get());
			model.addAttribute("accounts", accounts);
			return "loans/payment"; // ✅ maps to templates/loans/payment.html
		} else {
			return "redirect:/loans?error=Loan+not+found";
		}
	}

	@PostMapping("/loans/payment/{id}")
	public String makeLoanPayment(@PathVariable Long id, @RequestParam Long accountId, @RequestParam Double amount) {
		try {
			loanService.makePayment(id, accountId, amount);
			return "redirect:/loans?success=Payment+processed+successfully";
		} catch (Exception e) {
			return "redirect:/loans/payment/" + id + "?error=" + e.getMessage();
		}
	}

	@GetMapping("/loans/borrowed")
	public String showBorrowedLoans(Model model) {
		List<Loan> borrowedLoans = loanService.getBorrowedLoans();
		model.addAttribute("loans", borrowedLoans);
		model.addAttribute("title", "Borrowed Loans");
		model.addAttribute("totalOutstanding", loanService.getTotalOutstandingBorrowedAmount());
		return "loans/list";
	}

	@GetMapping("/loans/given")
	public String showGivenLoans(Model model) {
		List<Loan> givenLoans = loanService.getGivenLoans();
		model.addAttribute("loans", givenLoans);
		model.addAttribute("title", "Given Loans");
		model.addAttribute("totalOutstanding", loanService.getTotalOutstandingGivenAmount());
		return "loans/list";
	}

	@GetMapping("/loans/overdue")
	public String showOverdueLoans(Model model) {
		List<Loan> overdueLoans = loanService.getOverdueLoans();
		model.addAttribute("loans", overdueLoans);
		model.addAttribute("title", "Overdue Loans");
		return "loans/list";
	}

	@GetMapping("/loans/report")
	public String loanReport(Model model) {
		List<Loan> loans = loanService.getAllLoans();
		Map<String, Long> typeCounts = loans.stream()
				.collect(Collectors.groupingBy(loan -> loan.getType().toString(), Collectors.counting()));
		Map<String, Long> statusCounts = loans.stream()
				.collect(Collectors.groupingBy(loan -> loan.getStatus().toString(), Collectors.counting()));
		Double totalOutstanding = loans.stream()
				.mapToDouble(loan -> loan.getOutstandingAmount() != null ? loan.getOutstandingAmount() : 0.0).sum();
		long totalLoans = loans.size();
		long activeLoans = loans.stream().filter(loan -> Loan.LoanStatus.ACTIVE.equals(loan.getStatus())).count();
		long overdueLoans = loans.stream().filter(loan -> loan.getDueDate() != null
				&& loan.getDueDate().isBefore(LocalDate.now()) && !Loan.LoanStatus.PAID.equals(loan.getStatus()))
				.count();
		long paidLoans = loans.stream().filter(loan -> Loan.LoanStatus.PAID.equals(loan.getStatus())).count();
		List<LoanReportSummary> loanReports = calculateLoanReportsByCategory(loans);
		model.addAttribute("loans", loans);
		model.addAttribute("totalOutstanding", totalOutstanding);
		model.addAttribute("statusCounts", statusCounts);
		model.addAttribute("typeCounts", typeCounts);
		model.addAttribute("totalLoans", totalLoans);
		model.addAttribute("activeLoans", activeLoans);
		model.addAttribute("overdueLoans", overdueLoans);
		model.addAttribute("paidLoans", paidLoans);
		model.addAttribute("loanReports", loanReports);
		model.addAttribute("typeLabels", new ArrayList<>(typeCounts.keySet()));
		model.addAttribute("typeData", new ArrayList<>(typeCounts.values()));
		model.addAttribute("statusLabels", new ArrayList<>(statusCounts.keySet()));
		model.addAttribute("statusData", new ArrayList<>(statusCounts.values()));
		return "loans/report";
	}

	private List<LoanReportSummary> calculateLoanReportsByCategory(List<Loan> loans) {
		Map<String, LoanReportSummary> reportMap = new HashMap<>();
		for (Loan loan : loans) {
			String category = loan.getType().toString();
			double outstandingBalance = loan.getOutstandingAmount() != null ? loan.getOutstandingAmount() : 0.0;
			LoanReportSummary summary = reportMap.getOrDefault(category, new LoanReportSummary());
			summary.setCategory(category);
			summary.setCount(summary.getCount() + 1);
			summary.setTotalAmount(summary.getTotalAmount() + loan.getPrincipalAmount());
			summary.setOutstandingAmount(summary.getOutstandingAmount() + outstandingBalance);
			summary.setAverageAmount(summary.getTotalAmount() / summary.getCount());
			reportMap.put(category, summary);
		}
		return new ArrayList<>(reportMap.values());
	}
}