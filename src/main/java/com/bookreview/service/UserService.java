package com.bookreview.service;

import com.bookreview.entity.User;
import com.bookreview.models.UserRole;
import com.bookreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() ||
            userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(UserRole.READER);
        return userRepository.save(user);
    }
}
