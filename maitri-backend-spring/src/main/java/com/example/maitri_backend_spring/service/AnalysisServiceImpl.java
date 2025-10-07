// File: src/main/java/com/example/maitri_backend_spring/service/AnalysisServiceImpl.java
package com.example.maitri_backend_spring.service; // <-- Correct package for the implementation

import com.example.maitri_backend_spring.dto.AnalysisResult;
import com.example.maitri_backend_spring.repository.AnalysisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService { // <-- Implements the interface

    private final RestTemplate restTemplate;

    // Injects the URL from application.properties (ai.service.url)
    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AnalysisServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AnalysisResult analyze(String text) {

        // The Python API expects a JSON body with the key "text".
        Map<String, String> requestBody = Map.of("text", text);

        try {
            // Call the Python AI service and map the JSON response to AnalysisResult DTO
            AnalysisResult result = restTemplate.postForObject(aiServiceUrl, requestBody, AnalysisResult.class);

            if (result == null) {
                throw new RuntimeException("AI service returned null result");
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            return createDefaultAnalysisResult();
        }
    }

    private AnalysisResult createDefaultAnalysisResult() {
        // Fallback DTO for when the AI service connection fails
        AnalysisResult defaultResult = new AnalysisResult();
        defaultResult.setSentiment("Failed");
        defaultResult.setTheme("Error");
        defaultResult.setWellbeingScore(5); // Default neutral score (1-10 range)
        defaultResult.setRiskFactors("Connection_Error");
        defaultResult.setCriticalRisk(false);
        defaultResult.setNamedEntities("None");
        defaultResult.setSummary("AI analysis service connection failed.");
        return defaultResult;
    }
}