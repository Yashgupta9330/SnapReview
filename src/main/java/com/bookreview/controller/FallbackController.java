package com.bookreview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {
    @RequestMapping("/api/**")
    public ResponseEntity<?> fallback() {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", "No handler found for this endpoint");
        body.put("path", ""); 
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
} 