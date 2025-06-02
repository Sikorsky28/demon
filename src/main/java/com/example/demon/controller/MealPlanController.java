package com.example.demon.controller;

import com.example.demon.service.GoGPTService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mealplan")
@CrossOrigin(origins = "http://localhost:63342") // ✅ Разрешить запросы с фронтенда// Разрешаем запросы с фронтенда
public class MealPlanController {

    private final GoGPTService goGPTService;

    public MealPlanController(GoGPTService goGPTService) {
        this.goGPTService = goGPTService;
    }
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{age}/{weight}/{activity}")

    public String generateMealPlan(@PathVariable int age, @PathVariable int weight, @PathVariable String activity) {
        // Формируем параметры для генерации меню
        String params = String.format("возраст_%d_вес_%d_активность_%s", age, weight, activity);
        return goGPTService.getMealPlan(params);
    }
}
