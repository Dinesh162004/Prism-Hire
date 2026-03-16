package com.prismhire.service;

import com.prismhire.entity.ResumeAnalysis;
import com.prismhire.repository.ResumeAnalysisRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    private ResumeAnalysisRepository repository;

    // ─── Tech Skills Library ──────────────────────────────────────────────────
    private static final List<String> TECH_SKILLS = List.of(
            "java", "python", "javascript", "typescript", "c++", "c#", "go", "rust", "kotlin", "swift",
            "react", "angular", "vue", "node.js", "spring", "spring boot", "django", "flask", "express",
            "mysql", "postgresql", "mongodb", "redis", "sqlite", "oracle",
            "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git", "github", "gitlab",
            "rest api", "graphql", "microservices", "agile", "scrum", "devops", "ci/cd",
            "machine learning", "deep learning", "tensorflow", "pytorch", "sql", "html", "css",
            "linux", "bash", "terraform", "ansible");

    // ─── Parse Resume ─────────────────────────────────────────────────────────
    public String parseResume(MultipartFile file) throws Exception {
        String originalName = (file.getOriginalFilename() != null ? file.getOriginalFilename() : "").toLowerCase();

        if (originalName.endsWith(".pdf")) {
            try (PDDocument pd = Loader.loadPDF(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(pd);
            }
        } else if (originalName.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                StringBuilder sb = new StringBuilder();
                for (XWPFParagraph para : doc.getParagraphs()) {
                    sb.append(para.getText()).append("\n");
                }
                return sb.toString();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload a PDF or DOCX file.");
        }
    }

    // ─── ATS Scoring ──────────────────────────────────────────────────────────
    public Map<String, Object> calculateATSScore(String resumeText, String jobDescription, String role) {
        String resumeLower = resumeText.toLowerCase();
        String jdLower = jobDescription.toLowerCase();

        // 1. Skill Match Score — find matching tech skills in resume vs jd
        List<String> jdSkills = TECH_SKILLS.stream()
                .filter(jdLower::contains).collect(Collectors.toList());
        List<String> matchedSkills = jdSkills.stream()
                .filter(resumeLower::contains).collect(Collectors.toList());
        List<String> missingSkills = jdSkills.stream()
                .filter(s -> !resumeLower.contains(s)).collect(Collectors.toList());
        double skillMatchScore = jdSkills.isEmpty() ? 70 : ((double) matchedSkills.size() / jdSkills.size()) * 100;

        // 2. Keyword Match Score — match meaningful jd words
        String[] jdWords = jdLower.replaceAll("[^a-z0-9 ]", " ").split("\\s+");
        Set<String> uniqueJdWords = Arrays.stream(jdWords)
                .filter(w -> w.length() > 4)
                .collect(Collectors.toSet());
        long matchedKeywords = uniqueJdWords.stream().filter(resumeLower::contains).count();
        double keywordMatchScore = uniqueJdWords.isEmpty() ? 60
                : ((double) matchedKeywords / uniqueJdWords.size()) * 100;
        double matchedKeywordsPercent = keywordMatchScore;

        // 3. Role Relevance Score — check role keyword in resume
        String roleLower = role.toLowerCase();
        double roleRelevanceScore;
        if (resumeLower.contains(roleLower)) {
            roleRelevanceScore = 90;
        } else {
            String[] roleParts = roleLower.split("\\s+");
            long roleMatches = Arrays.stream(roleParts).filter(resumeLower::contains).count();
            roleRelevanceScore = roleParts.length == 0 ? 50
                    : Math.min(90, ((double) roleMatches / roleParts.length) * 85);
        }

        // 4. ATS Score — weighted average
        double atsScore = (skillMatchScore * 0.4) + (keywordMatchScore * 0.4) + (roleRelevanceScore * 0.2);

        // 5. Suggestions
        List<String> suggestions = new ArrayList<>();
        if (skillMatchScore < 60) {
            suggestions.add("Add more relevant technical skills to match the job description requirements.");
        }
        if (keywordMatchScore < 60) {
            suggestions.add("Include more keywords from the job description to improve ATS compatibility.");
        }
        if (roleRelevanceScore < 60) {
            suggestions.add("Clearly mention the target role (" + role + ") in your resume summary.");
        }
        if (!missingSkills.isEmpty()) {
            suggestions.add("Consider adding these missing skills to strengthen your profile: " +
                    missingSkills.subList(0, Math.min(5, missingSkills.size())));
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Great match! Your resume aligns well with the job requirements.");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("atsScore", Math.min(100, atsScore));
        result.put("skillMatchScore", Math.min(100, skillMatchScore));
        result.put("keywordMatchScore", Math.min(100, keywordMatchScore));
        result.put("roleRelevanceScore", Math.min(100, roleRelevanceScore));
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("matchedKeywordsPercent", Math.min(100, matchedKeywordsPercent));
        result.put("suggestions", suggestions);
        return result;
    }

    // ─── Save and Return ──────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public ResumeAnalysis saveAnalysis(Long userId, String role, String jobDescription,
            String resumeText, String fileName, Map<String, Object> score) {
        ResumeAnalysis ra = new ResumeAnalysis();
        ra.setUserId(userId);
        ra.setRole(role);
        ra.setJobDescription(jobDescription);
        ra.setResumeText(resumeText.length() > 5000 ? resumeText.substring(0, 5000) : resumeText);
        ra.setResumeFileName(fileName);
        ra.setAtsScore((double) score.get("atsScore"));
        ra.setSkillMatchScore((double) score.get("skillMatchScore"));
        ra.setKeywordMatchScore((double) score.get("keywordMatchScore"));
        ra.setRoleRelevanceScore((double) score.get("roleRelevanceScore"));
        ra.setMatchedSkills((List<String>) score.get("matchedSkills"));
        ra.setMissingSkills((List<String>) score.get("missingSkills"));
        ra.setMatchedKeywordsPercent((double) score.get("matchedKeywordsPercent"));
        ra.setSuggestions((List<String>) score.get("suggestions"));
        return repository.save(ra);
    }
}
