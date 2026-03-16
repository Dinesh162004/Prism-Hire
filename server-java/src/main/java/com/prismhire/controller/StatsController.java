package com.prismhire.controller;

import com.prismhire.service.SavedInterviewQuestionService;
import com.prismhire.service.SavedQuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private SavedQuestionService savedQuestionService;

    @Autowired
    private SavedInterviewQuestionService interviewQuestionService;

    @GetMapping("/session-stats")
    public ResponseEntity<?> getSessionStats(HttpServletRequest request,
            @RequestParam(required = false) String sessionId) {
        try {
            if (sessionId == null) {
                return ResponseEntity.status(400).body(Map.of("message", "Session ID is required."));
            }
            Long userId = (Long) request.getAttribute("userId");
            Long sid = Long.parseLong(sessionId);

            long testCount = savedQuestionService.countByUserAndSession(userId, sid);
            long interviewSavedCount = interviewQuestionService.countByUserAndSession(userId, sid);

            return ResponseEntity.ok(Map.of(
                    "testCount", testCount,
                    "interviewSavedCount", interviewSavedCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch session stats."));
        }
    }
}
