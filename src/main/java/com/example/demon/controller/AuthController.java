package com.example.demon.controller;

import com.example.demon.dto.ErrorResponse;
import com.example.demon.dto.LoginRequest;
import com.example.demon.dto.AuthResponse;
import com.example.demon.dto.UserResponse;
import com.example.demon.model.User;
import com.example.demon.security.JwtUtil;
import com.example.demon.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:63342")
public class AuthController {
    @Autowired
    private UserService userService;

    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return ResponseEntity.ok(new AuthResponse(null, null, "Регистрация успешна!"));
        }
        return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Ошибка регистрации"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<User> user = userService.findByUsername(request.getUsername());

        if (user.isPresent() && user.get().getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.get().getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.get().getUsername());

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, "Вход успешен"));
        }

        return ResponseEntity.status(401).body(new AuthResponse(null, null, "Ошибка входа: неверный логин или пароль"));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        try {
            String username = jwtUtil.extractUsername(refreshToken);

            if (jwtUtil.validateToken(refreshToken)) {
                String newAccessToken = jwtUtil.generateToken(username);
                return ResponseEntity.ok(Map.of("token", newAccessToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Недействительный токен");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка при обновлении токена");
        }
    }




    @GetMapping("/user/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);

        return user.map(u -> ResponseEntity.ok(new UserResponse(
                u.getUsername(),
                u.getAge(),
                u.getWeight(),
                u.getActivityLevel()
        ))).orElseGet(() -> ResponseEntity.status(404).body(new UserResponse("Пользователь не найден")));
    }
    }
