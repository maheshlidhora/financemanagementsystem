package com.newrise.financemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newrise.financemanagementsystem.entity.TransactionCategory;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
	Optional<TransactionCategory> findByName(String name);
}
