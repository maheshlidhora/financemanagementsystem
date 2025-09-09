package com.newrise.financemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newrise.financemanagementsystem.entity.TransactionType;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
	Optional<TransactionType> findByName(String name);
}
