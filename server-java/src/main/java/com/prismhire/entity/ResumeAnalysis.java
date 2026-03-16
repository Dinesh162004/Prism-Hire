package com.prismhire.entity;

import com.prismhire.converter.StringListConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String role;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "resume_text", columnDefinition = "TEXT")
    private String resumeText;

    @Column(name = "resume_file_name")
    private String resumeFileName;

    @Column(name = "ats_score")
    private double atsScore;

    @Column(name = "skill_match_score")
    private double skillMatchScore;

    @Column(name = "keyword_match_score")
    private double keywordMatchScore;

    @Column(name = "role_relevance_score")
    private double roleRelevanceScore;

    @Convert(converter = StringListConverter.class)
    @Column(name = "matched_skills", columnDefinition = "JSON")
    private List<String> matchedSkills;

    @Convert(converter = StringListConverter.class)
    @Column(name = "missing_skills", columnDefinition = "JSON")
    private List<String> missingSkills;

    @Column(name = "matched_keywords_percent")
    private double matchedKeywordsPercent;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "JSON")
    private List<String> suggestions;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ResumeAnalysis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }

    public double getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(double atsScore) {
        this.atsScore = atsScore;
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public double getKeywordMatchScore() {
        return keywordMatchScore;
    }

    public void setKeywordMatchScore(double keywordMatchScore) {
        this.keywordMatchScore = keywordMatchScore;
    }

    public double getRoleRelevanceScore() {
        return roleRelevanceScore;
    }

    public void setRoleRelevanceScore(double roleRelevanceScore) {
        this.roleRelevanceScore = roleRelevanceScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public double getMatchedKeywordsPercent() {
        return matchedKeywordsPercent;
    }

    public void setMatchedKeywordsPercent(double matchedKeywordsPercent) {
        this.matchedKeywordsPercent = matchedKeywordsPercent;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
