package com.bookreview.controller;

import com.bookreview.exception.InvalidCredentialsException;
import com.bookreview.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        logger.info("Login attempt for username: {}", username);
        try {
            logger.debug("Authenticating user: {}", username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username, loginData.get("password")));
            logger.debug("Authentication successful for user: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetails.getUsername());
            logger.info("JWT generated for user: {}", username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", username);
            throw new InvalidCredentialsException("Invalid credentials");
        } catch (Exception e) {
            logger.error("Unexpected error during login for user: {}", username, e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
}
