// File: src/main/java/com/example/maitri_backend_spring/service/JournalService.java
package com.example.maitri_backend_spring.service;

import com.example.maitri_backend_spring.dto.AnalysisResult;
import com.example.maitri_backend_spring.entity.JournalEntry;
import com.example.maitri_backend_spring.repository.AnalysisService;
import com.example.maitri_backend_spring.repository.JournalEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JournalService {

    private final JournalEntryRepository repository;
    private final AnalysisService analysisService;

    public JournalService(JournalEntryRepository repository, AnalysisService analysisService) {
        this.repository = repository;
        this.analysisService = analysisService;
    }

    public JournalEntry createEntry(String text) {
        AnalysisResult result = analysisService.analyze(text);

        JournalEntry entry = new JournalEntry();
        entry.setText(text);

        entry.setSentiment(result.getSentiment());
        entry.setTheme(result.getTheme());
        entry.setWellbeingScore(result.getWellbeingScore());
        entry.setRiskFactors(result.getRiskFactors());
        entry.setCriticalRisk(result.isCriticalRisk());
        entry.setNamedEntities(result.getNamedEntities());
        entry.setSummary(result.getSummary());

        return repository.save(entry);
    }

    public Page<JournalEntry> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
}
