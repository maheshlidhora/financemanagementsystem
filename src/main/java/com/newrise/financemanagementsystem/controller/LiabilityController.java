package com.newrise.financemanagementsystem.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Liability;
import com.newrise.financemanagementsystem.service.AccountService;
import com.newrise.financemanagementsystem.service.LiabilityService;

@Controller
public class LiabilityController {
	@Autowired
	private LiabilityService liabilityService;
	@Autowired
	private AccountService accountService;

	@GetMapping("/liabilities")
	public String listLiabilities(Model model) {
		List<Liability> liabilities = liabilityService.getAllLiabilities();
		Double totalOutstanding = liabilityService.getTotalOutstandingAmount();
		// Handle null case
		model.addAttribute("totalOutstanding", totalOutstanding != null ? totalOutstanding : 0.0);
		model.addAttribute("liabilities", liabilities);
		return "liabilities/list";
	}

	@GetMapping("/liabilities/create")
	public String showCreateLiabilityForm(Model model) {
		List<Account> accounts = accountService.getAllAccounts();
		model.addAttribute("liability", new Liability());
		model.addAttribute("accounts", accounts);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("statuses", Liability.LiabilityStatus.values());
		return "liabilities/create";
	}

	@PostMapping("/liabilities/create")
	public String createLiability(@ModelAttribute Liability liability, @RequestParam(required = false) Long accountId) {
		try {
			if (accountId != null) {
				Account account = accountService.getAccountById(accountId)
						.orElseThrow(() -> new RuntimeException("Account not found"));
				liability.setAccount(account);
			}
			liabilityService.createLiability(liability);
			return "redirect:/liabilities?success=Liability+created+successfully";
		} catch (Exception e) {
			return "redirect:/liabilities/create?error=" + e.getMessage();
		}
	}

	@GetMapping("/liabilities/{id}")
	public String viewLiability(@PathVariable Long id, Model model) {
		Optional<Liability> liability = liabilityService.getLiabilityById(id);
		if (liability.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("liability", liability.get());
			model.addAttribute("accounts", accounts);
			return "liabilities/view";
		} else {
			return "redirect:/liabilities?error=Liability+not+found";
		}
	}

	@GetMapping("/liabilities/edit/{id}")
	public String showEditLiabilityForm(@PathVariable Long id, Model model) {
		Optional<Liability> liability = liabilityService.getLiabilityById(id);
		if (liability.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("liability", liability.get());
			model.addAttribute("accounts", accounts);
			model.addAttribute("statuses", Liability.LiabilityStatus.values());
			return "liabilities/edit";
		} else {
			return "redirect:/liabilities?error=Liability+not+found";
		}
	}

	@PostMapping("/liabilities/edit/{id}")
	public String updateLiability(@PathVariable Long id, @ModelAttribute Liability liability,
			@RequestParam(required = false) Long accountId) {
		try {
			if (accountId != null) {
				Account account = accountService.getAccountById(accountId)
						.orElseThrow(() -> new RuntimeException("Account not found"));
				liability.setAccount(account);
			}

			liabilityService.updateLiability(id, liability);
			return "redirect:/liabilities?success=Liability+updated+successfully";
		} catch (Exception e) {
			return "redirect:/liabilities/edit/" + id + "?error=" + e.getMessage();
		}
	}

	@GetMapping("/liabilities/delete/{id}")
	public String deleteLiability(@PathVariable Long id) {
		try {
			liabilityService.deleteLiability(id);
			return "redirect:/liabilities?success=Liability+deleted+successfully";
		} catch (Exception e) {
			return "redirect:/liabilities?error=" + e.getMessage();
		}
	}

	@GetMapping("/liabilities/payment/{id}")
	public String showPaymentForm(@PathVariable Long id, Model model) {
		Optional<Liability> liability = liabilityService.getLiabilityById(id);
		if (liability.isPresent()) {
			List<Account> accounts = accountService.getAllAccounts();
			model.addAttribute("liability", liability.get());
			model.addAttribute("accounts", accounts);
			return "liabilities/payment";
		} else {
			return "redirect:/liabilities?error=Liability+not+found";
		}
	}

	@PostMapping("/liabilities/payment/{id}")
	public String makePayment(@PathVariable Long id, @RequestParam Long accountId, @RequestParam Double amount) {
		try {
			liabilityService.makePayment(id, accountId, amount);
			return "redirect:/liabilities?success=Payment+processed+successfully";
		} catch (Exception e) {
			return "redirect:/liabilities/payment/" + id + "?error=" + e.getMessage();
		}
	}

	@GetMapping("/liabilities/overdue")
	public String showOverdueLiabilities(Model model) {
		List<Liability> overdueLiabilities = liabilityService.getOverdueLiabilities();
		LocalDate today = LocalDate.now();
		// Calculate total overdue amount
		Double totalOverdue = overdueLiabilities.stream().mapToDouble(Liability::getOutstandingAmount).sum();
		// Create a map with Liability as key and days overdue as value
		Map<Liability, Long> liabilityDaysOverdue = new HashMap<>();
		for (Liability liability : overdueLiabilities) {
			long daysOverdue = ChronoUnit.DAYS.between(liability.getDueDate(), today);
			liabilityDaysOverdue.put(liability, daysOverdue);
		}
		model.addAttribute("overdueLiabilities", overdueLiabilities);
		model.addAttribute("totalOverdue", totalOverdue);
		model.addAttribute("today", today);
		model.addAttribute("liabilityDaysOverdue", liabilityDaysOverdue);
		model.addAttribute("title", "Overdue Liabilities");
		return "liabilities/overdue_liabilities";
	}

	@GetMapping("/liabilities/report")
	public String liabilityReport(Model model) {
		List<Liability> liabilities = liabilityService.getAllLiabilities();
		Double totalOutstanding = liabilityService.getTotalOutstandingAmount();
		// Calculate summary statistics
		long totalLiabilities = liabilities.size();
		long outstandingLiabilities = liabilities.stream()
				.filter(liability -> Liability.LiabilityStatus.OUTSTANDING.equals(liability.getStatus())).count();
		long partialLiabilities = liabilities.stream()
				.filter(liability -> Liability.LiabilityStatus.PARTIAL.equals(liability.getStatus())).count();
		long paidLiabilities = liabilities.stream()
				.filter(liability -> Liability.LiabilityStatus.PAID.equals(liability.getStatus())).count();
		long overdueLiabilities = liabilities.stream()
				.filter(liability -> Liability.LiabilityStatus.OVERDUE.equals(liability.getStatus())).count();
		long functionallyOverdue = liabilities.stream()
				.filter(liability -> liability.getDueDate() != null && liability.getDueDate().isBefore(LocalDate.now())
						&& (Liability.LiabilityStatus.OUTSTANDING.equals(liability.getStatus())
								|| Liability.LiabilityStatus.PARTIAL.equals(liability.getStatus())))
				.count();
		Map<String, Long> statusCounts = liabilities.stream().collect(Collectors
				.groupingBy(liability -> liability.getStatus().toString().toUpperCase(), Collectors.counting()));
		Map<String, Double> amountByStatus = liabilities.stream()
				.collect(Collectors.groupingBy(liability -> liability.getStatus().toString().toUpperCase(),
						Collectors.summingDouble(Liability::getAmount)));
		for (Liability.LiabilityStatus status : Liability.LiabilityStatus.values()) {
			statusCounts.putIfAbsent(status.toString(), 0L);
			amountByStatus.putIfAbsent(status.toString(), 0.0);
		}
		model.addAttribute("liabilities", liabilities);
		model.addAttribute("totalOutstanding", totalOutstanding != null ? totalOutstanding : 0.0);
		model.addAttribute("totalLiabilities", totalLiabilities);
		model.addAttribute("outstandingLiabilities", outstandingLiabilities);
		model.addAttribute("partialLiabilities", partialLiabilities);
		model.addAttribute("paidLiabilities", paidLiabilities);
		model.addAttribute("overdueLiabilities", overdueLiabilities);
		model.addAttribute("functionallyOverdue", functionallyOverdue);
		model.addAttribute("statusCounts", statusCounts);
		model.addAttribute("amountByStatus", amountByStatus);
		List<String> statusLabels = Arrays.stream(Liability.LiabilityStatus.values()).map(Enum::toString)
				.collect(Collectors.toList());
		List<Long> statusData = statusLabels.stream().map(label -> statusCounts.getOrDefault(label, 0L))
				.collect(Collectors.toList());
		List<Double> amountData = statusLabels.stream().map(label -> amountByStatus.getOrDefault(label, 0.0))
				.collect(Collectors.toList());
		model.addAttribute("statusLabels", statusLabels);
		model.addAttribute("statusData", statusData);
		model.addAttribute("amountLabels", statusLabels);
		model.addAttribute("amountData", amountData);
		return "liabilities/report";
	}
}