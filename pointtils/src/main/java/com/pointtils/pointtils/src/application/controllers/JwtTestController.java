package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt-test")
public class JwtTestController {

    @GetMapping("/publico")
    public ResponseEntity<String> getPublicResource() {
        return ResponseEntity.ok("Esse endpoint é público.");
    }

    @GetMapping("/protegido")
    public ResponseEntity<String> getProtectedResource() {
        return ResponseEntity.ok("Esse endpoint é privado.");
    }
}
