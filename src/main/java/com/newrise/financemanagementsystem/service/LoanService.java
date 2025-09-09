package com.newrise.financemanagementsystem.service;

import java.util.List;
import java.util.Optional;

import com.newrise.financemanagementsystem.entity.Loan;

public interface LoanService {
	Loan createLoan(Loan loan);

	Loan makePayment(Long loanId, Long accountId, Double amount);

	List<Loan> getAllLoans();

	Optional<Loan> getLoanById(Long loanId);

	List<Loan> getLoansByStatus(Loan.LoanStatus status);

	List<Loan> getLoansByType(Loan.LoanType type);

	List<Loan> getBorrowedLoans();

	List<Loan> getGivenLoans();

	Loan updateLoanStatus(Long loanId, Loan.LoanStatus status);

	Double getTotalOutstandingAmount();

	Double getTotalOutstandingBorrowedAmount();

	Double getTotalOutstandingGivenAmount();

	List<Loan> getOverdueLoans();

	Loan updateLoan(Long loanId, Loan updatedLoan);

	void deleteLoan(Long loanId);
}
