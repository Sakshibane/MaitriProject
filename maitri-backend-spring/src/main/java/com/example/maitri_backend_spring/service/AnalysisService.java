package com.example.maitri_backend_spring.service;

import com.example.maitri_backend_spring.entity.JournalEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AnalysisService {

    private final RestTemplate restTemplate;

    // This reads the URL from application.properties
    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JournalEntry analyzeEntry(JournalEntry journalEntry) {
        // 1. Prepare the request for the Python service
        Map<String, String> requestBody = Map.of("text", journalEntry.getText());

        try {
            // 2. Call the Python AI service's /analyze endpoint
            Map<String, Object> aiResponse = restTemplate.postForObject(aiServiceUrl, requestBody, Map.class);

            if (aiResponse != null && aiResponse.containsKey("sentiment")) {
                // 3. Update the journal entry with the sentiment from the response
                String sentiment = (String) aiResponse.get("sentiment");
                journalEntry.setSentiment(sentiment);
            }
        } catch (Exception e) {
            // If the AI service fails, we'll just set a default sentiment
            System.err.println("Error calling AI service: " + e.getMessage());
            journalEntry.setSentiment("Analysis failed");
        }

        return journalEntry;
    }
}