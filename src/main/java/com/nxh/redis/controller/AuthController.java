package com.nxh.redis.controller;

import com.nxh.redis.dto.AuthRequest;
import com.nxh.redis.dto.AuthResponse;
import com.nxh.redis.dto.RegisterRequest;
import com.nxh.redis.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    /**
     * Tạo tài khoản mới
     * POST /api/auth/register
     * Body: { "username": "abc", "password": "123456" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Đăng nhập — trả về JWT token
     * POST /api/auth/login
     * Body: { "username": "admin", "password": "Admin@123" }
     * Response: { "token": "eyJ...", "username": "admin", "role": "ADMIN" }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
