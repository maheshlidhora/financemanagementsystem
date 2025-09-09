package com.newrise.financemanagementsystem.service;

import java.util.Optional;

import com.newrise.financemanagementsystem.entity.User;

public interface UserService {
	public Optional<User> loginUser(String email, String password);
}
