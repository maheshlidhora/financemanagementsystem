package com.newrise.financemanagementsystem.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Liability;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.entity.TransactionType;
import com.newrise.financemanagementsystem.repository.AccountRepository;
import com.newrise.financemanagementsystem.repository.LiabilityRepository;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.repository.TransactionRepository;
import com.newrise.financemanagementsystem.repository.TransactionTypeRepository;
import com.newrise.financemanagementsystem.service.LiabilityService;

@Service
public class LiabilityServiceImpl implements LiabilityService {

	@Autowired
	private LiabilityRepository liabilityRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	@Autowired
	private TransactionCategoryRepository transactionCategoryRepository;

	@Override
	@Transactional
	public Liability createLiability(Liability liability) {
		if (liability.getStatus() == null) {
			liability.setStatus(Liability.LiabilityStatus.OUTSTANDING);
		}
		if (liability.getOutstandingAmount() == null) {
			liability.setOutstandingAmount(liability.getAmount());
		}
		return liabilityRepository.save(liability);
	}

	@Override
	@Transactional
	public Liability makePayment(Long liabilityId, Long accountId, Double amount) {
		Liability liability = liabilityRepository.findById(liabilityId)
				.orElseThrow(() -> new RuntimeException("Liability not found"));
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		validatePayment(liability, account, amount);
		account.setCurrentBalance(account.getCurrentBalance() - amount);
		accountRepository.save(account);
		createPaymentTransaction(liability, account, amount);
		liability.setOutstandingAmount(liability.getOutstandingAmount() - amount);
		updateLiabilityStatus(liability);
		return liabilityRepository.save(liability);
	}

	private void validatePayment(Liability liability, Account account, Double amount) {
		if (amount <= 0) {
			throw new RuntimeException("Payment amount must be positive");
		}
		if (amount > liability.getOutstandingAmount()) {
			throw new RuntimeException("Payment amount cannot exceed outstanding amount");
		}
		if (account.getCurrentBalance() < amount) {
			throw new RuntimeException("Insufficient account balance");
		}
		if (liability.getStatus() == Liability.LiabilityStatus.PAID) {
			throw new RuntimeException("Liability is already fully paid");
		}
	}

	private void createPaymentTransaction(Liability liability, Account account, Double amount) {
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(amount);
			TransactionType debitType = transactionTypeRepository.findByName("Debit")
					.orElseThrow(() -> new RuntimeException("TransactionType 'Debit' not found"));
			transaction.setTransactionType(debitType);
			TransactionCategory liabilityCategory = transactionCategoryRepository.findByName("Loan Payment")
					.orElseGet(() -> {
						return transactionCategoryRepository.findAll().stream().findFirst()
								.orElseThrow(() -> new RuntimeException("No transaction categories found"));
					});
			transaction.setCategory(liabilityCategory);
			transaction.setLiability(liability);
			transaction.setDescription("Liability Payment: " + liability.getDescription());
			transactionRepository.save(transaction);
		} catch (Exception e) {
			System.err.println("Failed to create payment transaction: " + e.getMessage());
			throw new RuntimeException("Failed to create payment transaction: " + e.getMessage(), e);
		}
	}

	private void updateLiabilityStatus(Liability liability) {
		if (liability.getOutstandingAmount() == 0) {
			liability.setStatus(Liability.LiabilityStatus.PAID);
		} else if (liability.getOutstandingAmount() < liability.getAmount()) {
			liability.setStatus(Liability.LiabilityStatus.PARTIAL);
		}
		if (liability.getDueDate() != null && liability.getDueDate().isBefore(LocalDate.now())
				&& liability.getStatus() != Liability.LiabilityStatus.PAID) {
			liability.setStatus(Liability.LiabilityStatus.OVERDUE);
		}
	}

	@Override
	public List<Liability> getAllLiabilities() {
		return liabilityRepository.findAll();
	}

	@Override
	public Optional<Liability> getLiabilityById(Long liabilityId) {
		return liabilityRepository.findById(liabilityId);
	}

	@Override
	public List<Liability> getLiabilitiesByStatus(Liability.LiabilityStatus status) {
		return liabilityRepository.findByStatus(status);
	}

	@Override
	@Transactional
	public Liability updateLiabilityStatus(Long liabilityId, Liability.LiabilityStatus status) {
		Liability liability = liabilityRepository.findById(liabilityId)
				.orElseThrow(() -> new RuntimeException("Liability not found"));

		liability.setStatus(status);
		return liabilityRepository.save(liability);
	}

	@Override
	public List<Liability> getOverdueLiabilities() {
		LocalDate today = LocalDate.now();
		return liabilityRepository.findByDueDateBeforeAndStatusNot(today, Liability.LiabilityStatus.PAID);
	}

	@Override
	public List<Liability> getLiabilitiesByAccount(Long accountId) {
		return liabilityRepository.findByAccount_AccountId(accountId);
	}

	@Override
	public Double getTotalOutstandingAmount() {
		Double total = liabilityRepository.getTotalOutstandingAmount();
		return total != null ? total : 0.0;
	}

	@Override
	public Double getTotalOutstandingAmountByAccount(Long accountId) {
		return liabilityRepository.getTotalOutstandingAmountByAccount(accountId);
	}

	@Override
	@Transactional
	public Liability updateLiability(Long liabilityId, Liability updatedLiability) {
		Liability existingLiability = liabilityRepository.findById(liabilityId)
				.orElseThrow(() -> new RuntimeException("Liability not found"));
		existingLiability.setDescription(updatedLiability.getDescription());
		existingLiability.setAmount(updatedLiability.getAmount());
		existingLiability.setOutstandingAmount(updatedLiability.getOutstandingAmount());
		existingLiability.setDueDate(updatedLiability.getDueDate());
		existingLiability.setStatus(updatedLiability.getStatus());
		updateLiabilityStatus(existingLiability);
		return liabilityRepository.save(existingLiability);
	}

	@Override
	@Transactional
	public void deleteLiability(Long liabilityId) {
		Liability liability = liabilityRepository.findById(liabilityId)
				.orElseThrow(() -> new RuntimeException("Liability not found"));
		if (liability.getOutstandingAmount() > 0 && liability.getStatus() != Liability.LiabilityStatus.PAID) {
			throw new RuntimeException("Cannot delete liability with outstanding amount");
		}
		List<Transaction> liabilityTransactions = transactionRepository.findByLiability(liability);
		if (!liabilityTransactions.isEmpty()) {
			liabilityTransactions.forEach(transaction -> transaction.setLiability(null));
			transactionRepository.saveAll(liabilityTransactions);
		}
		liabilityRepository.deleteById(liabilityId);
	}

	@Override
	public List<Liability> getLiabilitiesWithOutstandingAmountGreaterThan(Double amount) {
		return liabilityRepository.findByOutstandingAmountGreaterThan(amount);
	}

	@Override
	public List<Liability> getLiabilitiesDueBetween(LocalDate startDate, LocalDate endDate) {
		return liabilityRepository.findByDueDateBetween(startDate, endDate);
	}

	@Override
	@Transactional
	public int markOverdueLiabilities() {
		LocalDate today = LocalDate.now();
		List<Liability> overdueLiabilities = liabilityRepository.findByDueDateBeforeAndStatusNot(today,
				Liability.LiabilityStatus.PAID);
		overdueLiabilities.forEach(liability -> {
			if (liability.getStatus() != Liability.LiabilityStatus.OVERDUE) {
				liability.setStatus(Liability.LiabilityStatus.OVERDUE);
			}
		});
		liabilityRepository.saveAll(overdueLiabilities);
		return overdueLiabilities.size();
	}
}