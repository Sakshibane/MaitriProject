// File: src/main/java/com/example/maitri_backend_spring/entity/JournalEntry.java
package com.example.maitri_backend_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Text cannot be blank")
    @Size(max = 10000, message = "Text cannot exceed 10000 characters")
    @Column(columnDefinition = "TEXT")
    private String text;

    private String sentiment;
    private String theme;
    private int wellbeingScore;
    private String riskFactors;
    private boolean criticalRisk;
    private String namedEntities;
    private String summary;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getWellbeingScore() {
        return wellbeingScore;
    }

    public void setWellbeingScore(int wellbeingScore) {
        this.wellbeingScore = wellbeingScore;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    public boolean isCriticalRisk() {
        return criticalRisk;
    }

    public void setCriticalRisk(boolean criticalRisk) {
        this.criticalRisk = criticalRisk;
    }

    public String getNamedEntities() {
        return namedEntities;
    }

    public void setNamedEntities(String namedEntities) {
        this.namedEntities = namedEntities;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
