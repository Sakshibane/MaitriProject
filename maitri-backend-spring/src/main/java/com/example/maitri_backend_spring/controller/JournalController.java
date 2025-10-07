// File: src/main/java/com/example/maitri_backend_spring/controller/JournalController.java
package com.example.maitri_backend_spring.controller;

import com.example.maitri_backend_spring.entity.JournalEntry;
import com.example.maitri_backend_spring.service.JournalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@Valid @RequestBody JournalEntry entry) {
        try {
            JournalEntry savedEntry = journalService.createEntry(entry.getText());
            return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating journal entry: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<JournalEntry>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<JournalEntry> entries = journalService.findAll(page, size);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error retrieving journal entries: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
