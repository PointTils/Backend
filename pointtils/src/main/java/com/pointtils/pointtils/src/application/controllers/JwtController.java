package com.pointtils.pointtils.src.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.infrastructure.configs.JwtService;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/public")
    public ResponseEntity<String> getPublicResource() {
        String token = jwtService.generateToken("user");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/protected")
    public ResponseEntity<String> getProtectedResource() {
        return ResponseEntity.ok("Authenticated.");
    }
}
