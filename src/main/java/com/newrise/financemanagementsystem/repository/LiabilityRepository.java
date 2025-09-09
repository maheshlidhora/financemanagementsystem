package com.newrise.financemanagementsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newrise.financemanagementsystem.entity.Liability;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {

	List<Liability> findByStatus(Liability.LiabilityStatus status);

	List<Liability> findByAccount_AccountId(Long accountId);

	List<Liability> findByOutstandingAmountGreaterThan(Double amount);

	List<Liability> findByDueDateBeforeAndStatusNot(LocalDate dueDate, Liability.LiabilityStatus status);

	List<Liability> findByDueDateBefore(LocalDate date);

	List<Liability> findByDueDateAfter(LocalDate date);

	List<Liability> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

	List<Liability> findByDescriptionContainingIgnoreCase(String description);

	@Query("SELECT l FROM Liability l WHERE l.outstandingAmount > 0")
	List<Liability> findByOutstandingAmountGreaterThanZero();

	List<Liability> findByStatusAndAccount_AccountId(Liability.LiabilityStatus status, Long accountId);

	List<Liability> findByAmountGreaterThanEqual(Double amount);

	List<Liability> findByAmountLessThanEqual(Double amount);

	List<Liability> findByAmountBetween(Double minAmount, Double maxAmount);

	List<Liability> findByOutstandingAmountBetween(Double minAmount, Double maxAmount);

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0.0) FROM Liability l WHERE l.status != 'PAID'")
	Double getTotalOutstandingAmount();

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Liability l WHERE l.account.accountId = :accountId AND l.status != 'PAID'")
	Double getTotalOutstandingAmountByAccount(@Param("accountId") Long accountId);

	@Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Liability l WHERE l.status = :status")
	Double getTotalOutstandingAmountByStatus(@Param("status") Liability.LiabilityStatus status);

	@Query("SELECT COALESCE(SUM(l.amount), 0) FROM Liability l")
	Double getTotalLiabilityAmount();

	@Query("SELECT l FROM Liability l WHERE l.dueDate < CURRENT_DATE AND l.status != 'PAID' ORDER BY l.dueDate ASC")
	List<Liability> findOverdueLiabilities();

	@Query("SELECT l FROM Liability l WHERE l.outstandingAmount > 0 ORDER BY l.dueDate ASC")
	List<Liability> findActiveLiabilitiesOrderByDueDate();

	@Query("SELECT l FROM Liability l WHERE l.dueDate BETWEEN CURRENT_DATE AND :futureDate AND l.status != 'PAID' ORDER BY l.dueDate ASC")
	List<Liability> findLiabilitiesDueWithinNextDays(@Param("futureDate") LocalDate futureDate);

	List<Liability> findByCreatedAtAfter(java.time.LocalDateTime date);

	List<Liability> findByCreatedAtBefore(java.time.LocalDateTime date);

	List<Liability> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

	Long countByStatus(Liability.LiabilityStatus status);

	Long countByAccount_AccountId(Long accountId);

	@Query("SELECT l FROM Liability l ORDER BY l.dueDate ASC")
	List<Liability> findAllOrderByDueDateAsc();

	@Query("SELECT l FROM Liability l ORDER BY l.dueDate DESC")
	List<Liability> findAllOrderByDueDateDesc();

	@Query("SELECT l FROM Liability l ORDER BY l.outstandingAmount DESC")
	List<Liability> findAllOrderByOutstandingAmountDesc();

	@Query("SELECT l FROM Liability l ORDER BY l.amount DESC")
	List<Liability> findAllOrderByAmountDesc();

	@Query("SELECT l FROM Liability l WHERE l.status = :status AND l.dueDate BETWEEN :startDate AND :endDate")
	List<Liability> findByStatusAndDueDateBetween(@Param("status") Liability.LiabilityStatus status,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT l FROM Liability l WHERE l.status IN :statuses")
	List<Liability> findByStatusIn(@Param("statuses") List<Liability.LiabilityStatus> statuses);

	List<Liability> findByDueDateIsNull();

	List<Liability> findByDueDateIsNotNull();

	List<Liability> findByAccountIsNull();

	@Query("SELECT l FROM Liability l WHERE l.outstandingAmount > 0 ORDER BY l.outstandingAmount DESC")
	List<Liability> findTopNByOutstandingAmount(@Param("limit") int limit);
}
