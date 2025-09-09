package com.newrise.financemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newrise.financemanagementsystem.entity.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
	Optional<AccountType> findByName(String name);
}
