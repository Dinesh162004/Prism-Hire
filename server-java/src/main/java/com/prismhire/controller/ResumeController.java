package com.prismhire.controller;

import com.prismhire.entity.ResumeAnalysis;
import com.prismhire.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(HttpServletRequest request,
            @RequestParam("resume") MultipartFile file,
            @RequestParam("role") String role,
            @RequestParam("jobDescription") String jobDescription) {
        try {
            // Allow guest userId if unauthenticated (matching Node.js behavior)
            Long userId = request.getAttribute("userId") != null
                    ? (Long) request.getAttribute("userId")
                    : 0L;

            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Resume file is required"));
            }
            if (role == null || role.isBlank() || jobDescription == null || jobDescription.isBlank()) {
                return ResponseEntity.status(400).body(Map.of("error", "Role and job description are required"));
            }

            // Step 1: Parse resume text
            String resumeText = resumeService.parseResume(file);
            if (resumeText == null || resumeText.trim().length() < 50) {
                return ResponseEntity.status(400).body(Map.of(
                        "error", "Could not extract enough text from resume. Please ensure the file is readable."));
            }

            // Step 2: ATS scoring
            Map<String, Object> scoreResult = resumeService.calculateATSScore(resumeText, jobDescription, role);

            // Step 3: Save to DB
            ResumeAnalysis analysis = resumeService.saveAnalysis(userId, role, jobDescription,
                    resumeText, file.getOriginalFilename(), scoreResult);

            // Step 4: Build response
            Map<String, Object> response = new HashMap<>(scoreResult);
            response.put("success", true);
            response.put("analysisId", analysis.getId());
            response.put("role", role);
            response.put("resumeFileName", file.getOriginalFilename());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error analyzing resume. Please try again."));
        }
    }
}
