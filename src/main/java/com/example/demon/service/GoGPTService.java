package com.example.demon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoGPTService {

    private final String API_URL = "https://api.gogpt.ru/v1/chat/completions"; // ✅ Проверь правильность URL

    @Value("${api.key}") // ✅ Загружаем ключ из application.properties
    private String apiKey;

    public String getMealPlan(String userParams) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); // 
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4"); // Проверь, какая модель доступна
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", "Ты - помощник, который составляет рацион питания."),
                Map.of("role", "user", "content", "Составь рацион питания на неделю для параметров: " + userParams)
        });
        requestBody.put("max_tokens", 300);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
            System.out.println("Ответ API: " + response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.has("error")) {
                return "Ошибка API: " + rootNode.get("error").get("message").asText();
            }

            JsonNode choicesNode = rootNode.get("choices");
            if (choicesNode != null && choicesNode.isArray()) {
                JsonNode firstChoice = choicesNode.get(0);
                if (firstChoice != null && firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    return firstChoice.get("message").get("content").asText();
                }
            }

            return "Ошибка: не удалось получить текст ответа!";
        } catch (Exception e) {
            System.err.println("Ошибка при запросе к API: " + e.getMessage());
            return "Ошибка API: " + e.getMessage();
        }
    }
}
