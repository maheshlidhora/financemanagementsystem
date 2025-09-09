package com.newrise.financemanagementsystem.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newrise.financemanagementsystem.entity.User;
import com.newrise.financemanagementsystem.repository.UserRepository;
import com.newrise.financemanagementsystem.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public Optional<User> loginUser(String email, String password) {
		try {
			Optional<User> user = userRepository.findByEmail(email);
			if (user.get() != null && user.get().getPassword().equalsIgnoreCase(password)) {
				return user;
			}
			return Optional.empty();
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
