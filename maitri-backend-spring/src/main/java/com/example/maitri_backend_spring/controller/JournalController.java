package com.example.maitri_backend_spring.controller;

import com.example.maitri_backend_spring.entity.JournalEntry;
import com.example.maitri_backend_spring.repository.JournalEntryRepository;
import com.example.maitri_backend_spring.service.AnalysisService; // <-- IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    @Autowired
    private JournalEntryRepository journalRepository;

    @Autowired
    private AnalysisService analysisService; // <-- INJECT THE NEW SERVICE

    @PostMapping
    public JournalEntry createJournalEntry(@RequestBody JournalEntry journalEntry) {
        // 1. First, send the entry for analysis
        JournalEntry analyzedEntry = analysisService.analyzeEntry(journalEntry);

        // 2. Then, save the updated entry to the database
        return journalRepository.save(analyzedEntry);
    }

    @GetMapping
    public List<JournalEntry> getAllEntries() {
        return journalRepository.findAll();
    }
}