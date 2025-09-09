package com.newrise.financemanagementsystem.service;

import java.util.List;
import java.util.Optional;

import com.newrise.financemanagementsystem.entity.Liability;

public interface LiabilityService {
	Liability createLiability(Liability liability);

	Liability makePayment(Long liabilityId, Long accountId, Double amount);

	List<Liability> getAllLiabilities();

	Optional<Liability> getLiabilityById(Long liabilityId);

	List<Liability> getLiabilitiesByStatus(Liability.LiabilityStatus status);

	Liability updateLiabilityStatus(Long liabilityId, Liability.LiabilityStatus status);

	List<Liability> getOverdueLiabilities();

	List<Liability> getLiabilitiesByAccount(Long accountId);

	Double getTotalOutstandingAmount();

	Double getTotalOutstandingAmountByAccount(Long accountId);

	Liability updateLiability(Long liabilityId, Liability updatedLiability);

	void deleteLiability(Long liabilityId);

	List<Liability> getLiabilitiesWithOutstandingAmountGreaterThan(Double amount);

	List<Liability> getLiabilitiesDueBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

	int markOverdueLiabilities();
}