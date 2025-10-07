// File: src/main/java/com/example/maitri_backend_spring/dto/AnalysisResult.java
package com.example.maitri_backend_spring.dto;

public class AnalysisResult {
    private String sentiment;
    private String theme;
    private int wellbeingScore;
    private String riskFactors;
    private boolean criticalRisk;
    private String namedEntities;
    private String summary;

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
