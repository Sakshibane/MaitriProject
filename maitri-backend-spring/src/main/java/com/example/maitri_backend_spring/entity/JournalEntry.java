package com.example.maitri_backend_spring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data // Lombok annotation for automatic getters, setters, toString, etc.
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private String sentiment; // This will be filled by the AI service

    private LocalDateTime createdAt = LocalDateTime.now();

    // private Long userId; // We will uncomment and use this later
}
