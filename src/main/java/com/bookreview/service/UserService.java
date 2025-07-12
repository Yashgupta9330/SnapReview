package com.bookreview.service;

import com.bookreview.entity.User;
import com.bookreview.exception.UserAlreadyExistsException;
import com.bookreview.models.UserRole;
import com.bookreview.repository.UserRepository;
import com.bookreview.dto.UserDTO;
import com.bookreview.dto.UserRegistrationRequest;
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
            throw new UserAlreadyExistsException("Username or Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(UserRole.READER);
        return userRepository.save(user);
    }

    public UserDTO registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Username or Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.getRoles().add(UserRole.READER);
        User saved = userRepository.save(user);
        return new UserDTO(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getFirstName(), saved.getLastName());
    }

    public Optional<UserDTO> getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()));
    }
}
