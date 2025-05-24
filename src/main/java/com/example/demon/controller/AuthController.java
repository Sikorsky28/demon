package com.example.demon.controller;

import com.example.demon.dto.LoginRequest;
import com.example.demon.model.User;
import com.example.demon.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth") // 🔹 Убедимся, что путь соответствует API
@CrossOrigin(origins = "http://localhost:63342") // ✅ Разрешаем CORS для фронтенда
public class AuthController {
    @Autowired
    private UserService userService;

    // ✅ Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success
                ? ResponseEntity.ok(Map.of("message", "Регистрация успешна!"))
                : ResponseEntity.badRequest().body(Map.of("message", "Ошибка регистрации"));
    }

    // ✅ Вход пользователя
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<User> user = userService.findByUsername(request.getUsername());

        if (user.isPresent() && user.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok(Map.of("token", "fake-jwt-token"));
        }

        return ResponseEntity.status(401).body(Map.of("message", "Ошибка входа: неверный логин или пароль"));
    }

    // ✅ Получение данных пользователя
    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);

        return user.map(u -> ResponseEntity.ok(Map.of(
                "username", u.getUsername(),
                "age", (Object) u.getAge(), // 🔹 Приведение к Object
                "weight", (Object) u.getWeight(),
                "activityLevel", u.getActivityLevel()
        ))).orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Пользователь не найден")));
    }
}