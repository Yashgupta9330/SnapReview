package com.bookreview.controller;

import com.bookreview.entity.User;
import com.bookreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import com.bookreview.dto.UserDTO;
import com.bookreview.models.UserRole;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() ||
            userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username or Email already exists");
        }

        user.getRoles().add(UserRole.READER); // ✅ Correct role enum
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

        // ⚠️ Return DTO, not entity
        UserDTO response = new UserDTO(saved.getUsername(), saved.getEmail(), saved.getFirstName(), saved.getLastName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable String username) {
        return userRepository.findByUsername(username)
            .map(user -> ResponseEntity.ok(new UserDTO(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName())))
            .orElse(ResponseEntity.notFound().build());
    }
}
