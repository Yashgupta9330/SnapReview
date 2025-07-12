package com.bookreview.controller;

import com.bookreview.entity.User;
import com.bookreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import com.bookreview.dto.UserDTO;
import com.bookreview.models.UserRole;
import com.bookreview.dto.UserRegistrationRequest;
import com.bookreview.service.UserService;
import com.bookreview.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserDTO created = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getProfile(@PathVariable String username) {
        return userService.getUserProfile(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
