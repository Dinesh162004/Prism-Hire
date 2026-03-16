package com.prismhire.controller;

import com.prismhire.service.GeminiService;
import com.prismhire.service.SavedQuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private SavedQuestionService savedQuestionService;

    // Mock test data (matches Node.js /api/test-data)
    private static final Map<String, Object> TEST_DATA = Map.of(
            "testId", 101,
            "title", "Full Stack Competency Assessment",
            "questions", List.of(
                    Map.of("id", 1, "text", "What is the virtual DOM?", "type", "concept"),
                    Map.of("id", 2, "text", "Explain the event loop in Node.js.", "type", "concept"),
                    Map.of("id", 3, "text", "Difference between SQL and NoSQL?", "type", "comparison")));

    @GetMapping("/api/test-data")
    public ResponseEntity<?> getTestData() {
        return ResponseEntity.ok(TEST_DATA);
    }

    @PostMapping("/api/generate-test")
    public ResponseEntity<?> generateTest(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String jobRole = (String) body.get("jobRole");
        String difficulty = (String) body.get("difficulty");

        if (jobRole == null || difficulty == null) {
            return ResponseEntity.status(400).body(Map.of("message", "Job Role and Difficulty are required."));
        }

        int count = body.get("count") != null ? Integer.parseInt(body.get("count").toString()) : 5;
        String yearsOfExp = body.get("yearsOfExperience") != null ? body.get("yearsOfExperience").toString() : null;
        String topics = body.get("topicsToFocus") != null ? body.get("topicsToFocus").toString() : null;
        String jd = body.get("jobDescription") != null ? body.get("jobDescription").toString() : null;

        Map<String, Object> result = geminiService.generateTest(jobRole, difficulty, count, yearsOfExp, topics, jd);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/save-questions")
    public ResponseEntity<?> saveQuestions(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Object questionsObj = body.get("questions");
            if (!(questionsObj instanceof List)) {
                return ResponseEntity.status(400).body(Map.of("message", "Invalid data format."));
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsObj;
            savedQuestionService.saveQuestions(userId, questions);
            return ResponseEntity.ok(Map.of("message", "Questions saved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save questions."));
        }
    }

    @GetMapping("/api/saved-questions")
    public ResponseEntity<?> getSavedQuestions(HttpServletRequest request,
            @RequestParam(required = false) String sessionId) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long sid = sessionId != null ? Long.parseLong(sessionId) : null;
            return ResponseEntity.ok(savedQuestionService.getSavedQuestions(userId, sid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch saved questions."));
        }
    }

    @DeleteMapping("/api/saved-questions/{id}")
    public ResponseEntity<?> deleteQuestion(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            savedQuestionService.deleteQuestion(userId, id);
            return ResponseEntity.ok(Map.of("message", "Question deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete question."));
        }
    }
}
