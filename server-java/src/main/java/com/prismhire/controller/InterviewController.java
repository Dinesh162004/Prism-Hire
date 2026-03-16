package com.prismhire.controller;

import com.prismhire.entity.SavedInterviewQuestion;
import com.prismhire.service.GeminiService;
import com.prismhire.service.SavedInterviewQuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class InterviewController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private SavedInterviewQuestionService service;

    @PostMapping("/api/generate-interview")
    public ResponseEntity<?> generateInterview(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String jobRole = (String) body.get("jobRole");
        String difficulty = (String) body.get("difficulty");

        if (jobRole == null || difficulty == null) {
            return ResponseEntity.status(400).body(Map.of("message", "Job Role and Difficulty are required."));
        }

        int count = body.get("count") != null ? Integer.parseInt(body.get("count").toString()) : 2;
        String yearsOfExp = body.get("yearsOfExperience") != null ? body.get("yearsOfExperience").toString() : null;
        String topics = body.get("topicsToFocus") != null ? body.get("topicsToFocus").toString() : null;
        String jd = body.get("jobDescription") != null ? body.get("jobDescription").toString() : null;

        Map<String, Object> result = geminiService.generateInterview(jobRole, difficulty, count, yearsOfExp, topics,
                jd);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/save-interview-question")
    public ResponseEntity<?> saveSingle(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (body.get("sessionId") == null || body.get("jobRole") == null ||
                    body.get("question") == null || body.get("shortAnswer") == null || body.get("longAnswer") == null) {
                return ResponseEntity.status(400).body(Map.of("message", "Missing required fields."));
            }
            SavedInterviewQuestion saved = service.saveSingle(userId, body);
            return ResponseEntity.ok(Map.of("message", "Interview question saved successfully!", "question", saved));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save interview question."));
        }
    }

    @PostMapping("/api/save-interview-questions-bulk")
    public ResponseEntity<?> saveBulk(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Object questionsObj = body.get("questions");
            if (!(questionsObj instanceof List)) {
                return ResponseEntity.status(400).body(Map.of("message", "Invalid data format."));
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsObj;
            service.saveBulk(userId, questions);
            return ResponseEntity
                    .ok(Map.of("message", "Interview questions saved successfully!", "count", questions.size()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save interview questions."));
        }
    }

    @GetMapping("/api/saved-interview-questions")
    public ResponseEntity<?> getQuestions(HttpServletRequest request,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String source) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long sid = sessionId != null ? Long.parseLong(sessionId) : null;
            List<SavedInterviewQuestion> questions = service.getQuestions(userId, sid, source);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch saved interview questions."));
        }
    }

    @PatchMapping("/api/mark-interview-complete")
    public ResponseEntity<?> markComplete(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long sessionId = Long.parseLong(body.get("sessionId").toString());
            String questionText = body.get("questionText").toString();
            SavedInterviewQuestion updated = service.markComplete(userId, sessionId, questionText);
            return ResponseEntity.ok(Map.of("message", "Question marked as completed!", "question", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", "Question not found."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to mark question as completed."));
        }
    }
}
