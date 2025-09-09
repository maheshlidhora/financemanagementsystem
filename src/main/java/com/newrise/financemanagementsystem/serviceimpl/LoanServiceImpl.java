package com.newrise.financemanagementsystem.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newrise.financemanagementsystem.entity.Account;
import com.newrise.financemanagementsystem.entity.Loan;
import com.newrise.financemanagementsystem.entity.Transaction;
import com.newrise.financemanagementsystem.entity.TransactionCategory;
import com.newrise.financemanagementsystem.entity.TransactionType;
import com.newrise.financemanagementsystem.repository.AccountRepository;
import com.newrise.financemanagementsystem.repository.LoanRepository;
import com.newrise.financemanagementsystem.repository.TransactionCategoryRepository;
import com.newrise.financemanagementsystem.repository.TransactionRepository;
import com.newrise.financemanagementsystem.repository.TransactionTypeRepository;
import com.newrise.financemanagementsystem.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {
	@Autowired
	private LoanRepository loanRepository;
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
	public Loan createLoan(Loan loan) {
		Loan savedLoan = loanRepository.save(loan);
		Account account = loan.getLinkedAccount();
		if (account == null) {
			throw new RuntimeException("Loan must be linked to an account");
		}
		double newBalance = account.getCurrentBalance();
		if (loan.getIsBorrowed()) {
			newBalance += loan.getPrincipalAmount(); // Borrowed → add money
		} else {
			newBalance -= loan.getPrincipalAmount(); // Given → subtract money
		}
		account.setCurrentBalance(newBalance);
		accountRepository.save(account);
		Transaction transaction = new Transaction();
		transaction.setAccount(account);
		transaction.setAmount(loan.getPrincipalAmount());
		transaction.setLoan(savedLoan);
		transaction.setCreatedAt(LocalDateTime.now());
		transaction.setDescription("Loan disbursement");
		TransactionType transactionType = transactionTypeRepository
				.findByName(loan.getIsBorrowed() ? "Credit" : "Debit")
				.orElseThrow(() -> new RuntimeException("Transaction type not found"));
		transaction.setTransactionType(transactionType);
		String categoryName = loan.getIsBorrowed() ? "Loan Credit" : "Loan Payment";
		TransactionCategory loanCategory = transactionCategoryRepository.findByName(categoryName)
				.orElseThrow(() -> new RuntimeException("Default category '" + categoryName + "' not found"));
		transaction.setCategory(loanCategory);
		transactionRepository.save(transaction);
		return savedLoan;
	}

	@Override
	@Transactional
	public Loan makePayment(Long loanId, Long accountId, Double amount) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		validatePayment(loan, account, amount);
		account.setCurrentBalance(account.getCurrentBalance() - amount);
		accountRepository.save(account);
		createLoanPaymentTransaction(loan, account, amount);
		loan.setOutstandingAmount(loan.getOutstandingAmount() - amount);
		updateLoanStatus(loan);
		return loanRepository.save(loan);
	}

	private void validatePayment(Loan loan, Account account, Double amount) {
		if (amount <= 0) {
			throw new RuntimeException("Payment amount must be positive");
		}
		if (amount > loan.getOutstandingAmount()) {
			throw new RuntimeException("Payment amount cannot exceed outstanding amount");
		}
		if (account.getCurrentBalance() < amount) {
			throw new RuntimeException("Insufficient account balance");
		}
		if (loan.getStatus() == Loan.LoanStatus.PAID || loan.getStatus() == Loan.LoanStatus.CLOSED) {
			throw new RuntimeException("Loan is already fully paid or closed");
		}
	}

	private void createLoanPaymentTransaction(Loan loan, Account account, Double amount) {
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(amount);
			TransactionCategory loanPaymentCategory = transactionCategoryRepository.findByName("Loan Payment")
					.orElseThrow(() -> new RuntimeException("Loan Payment category not found"));
			transaction.setCategory(loanPaymentCategory);
			TransactionType transactionType;
			if (Boolean.TRUE.equals(loan.getIsBorrowed())) {
				transactionType = transactionTypeRepository.findByName("Debit")
						.orElseThrow(() -> new RuntimeException("TransactionType 'Debit' not found"));
			} else {
				transactionType = transactionTypeRepository.findByName("Credit")
						.orElseThrow(() -> new RuntimeException("TransactionType 'Credit' not found"));
			}
			transaction.setTransactionType(transactionType);
			transaction.setDescription("Loan Payment: " + loan.getDescription());
			transaction.setLoan(loan);
			transactionRepository.save(transaction);
		} catch (Exception e) {
			System.err.println("Failed to create loan payment transaction: " + e.getMessage());
			throw new RuntimeException("Failed to create loan payment transaction: " + e.getMessage(), e);
		}
	}

	private void updateLoanStatus(Loan loan) {
		if (loan.getOutstandingAmount() == 0) {
			loan.setStatus(Loan.LoanStatus.PAID);
		} else if (loan.getOutstandingAmount() < loan.getPrincipalAmount()) {
			loan.setStatus(Loan.LoanStatus.PARTIAL);
		}
		if (loan.getDueDate() != null && loan.getDueDate().isBefore(LocalDate.now())
				&& loan.getStatus() != Loan.LoanStatus.PAID && loan.getStatus() != Loan.LoanStatus.CLOSED) {
			loan.setStatus(Loan.LoanStatus.DEFAULTED);
		}
	}

	@Override
	public List<Loan> getAllLoans() {
		return loanRepository.findAll();
	}

	@Override
	public Optional<Loan> getLoanById(Long loanId) {
		return loanRepository.findById(loanId);
	}

	@Override
	public List<Loan> getLoansByStatus(Loan.LoanStatus status) {
		return loanRepository.findByStatus(status);
	}

	@Override
	public List<Loan> getLoansByType(Loan.LoanType type) {
		return loanRepository.findByType(type);
	}

	@Override
	public List<Loan> getBorrowedLoans() {
		return loanRepository.findByIsBorrowedTrue();
	}

	@Override
	public List<Loan> getGivenLoans() {
		return loanRepository.findByIsBorrowedFalse();
	}

	@Override
	@Transactional
	public Loan updateLoanStatus(Long loanId, Loan.LoanStatus status) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		loan.setStatus(status);
		return loanRepository.save(loan);
	}

	@Override
	public Double getTotalOutstandingAmount() {
		return loanRepository.getTotalOutstandingAmount();
	}

	@Override
	public Double getTotalOutstandingBorrowedAmount() {
		return loanRepository.getTotalOutstandingBorrowedAmount();
	}

	@Override
	public Double getTotalOutstandingGivenAmount() {
		return loanRepository.getTotalOutstandingGivenAmount();
	}

	@Override
	public List<Loan> getOverdueLoans() {
		LocalDate today = LocalDate.now();
		return loanRepository.findByDueDateBeforeAndStatusNot(today, Loan.LoanStatus.PAID);
	}

	@Override
	@Transactional
	public Loan updateLoan(Long loanId, Loan updatedLoan) {
		Loan existingLoan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
		existingLoan.setDescription(updatedLoan.getDescription());
		existingLoan.setPrincipalAmount(updatedLoan.getPrincipalAmount());
		existingLoan.setOutstandingAmount(updatedLoan.getOutstandingAmount());
		existingLoan.setType(updatedLoan.getType());
		existingLoan.setStatus(updatedLoan.getStatus());
		existingLoan.setIssueDate(updatedLoan.getIssueDate());
		existingLoan.setDueDate(updatedLoan.getDueDate());
		existingLoan.setLinkedAccount(updatedLoan.getLinkedAccount());
		existingLoan.setIsBorrowed(updatedLoan.getIsBorrowed());
		updateLoanStatus(existingLoan);
		return loanRepository.save(existingLoan);
	}

	@Override
	@Transactional
	public void deleteLoan(Long loanId) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
		if (loan.getOutstandingAmount() > 0 && loan.getStatus() != Loan.LoanStatus.PAID) {
			throw new RuntimeException("Cannot delete loan with outstanding amount");
		}
		List<Transaction> loanTransactions = transactionRepository.findByLoan(loan);
		if (!loanTransactions.isEmpty()) {
			loanTransactions.forEach(transaction -> transaction.setLoan(null));
			transactionRepository.saveAll(loanTransactions);
		}
		loanRepository.deleteById(loanId);
	}
}