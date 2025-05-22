package com.example.demon.controller;

import com.example.demon.service.GoGPTService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mealplan")
public class MealPlanController {

    private final GoGPTService goGPTService;

    public MealPlanController(GoGPTService goGPTService) {
        this.goGPTService = goGPTService;
    }

    @GetMapping("/{params}")
    public String generateMealPlan(@PathVariable String params) {
        return goGPTService.getMealPlan(params);
    }
}
