package com.prismhire.controller;

import com.prismhire.entity.Session;
import com.prismhire.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public ResponseEntity<?> createSession(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String sessionName = body.get("sessionName") != null ? body.get("sessionName").toString() : null;
            String jobRole = body.get("jobRole") != null ? body.get("jobRole").toString() : null;
            String yearsOfExp = body.get("yearsOfExperience") != null ? body.get("yearsOfExperience").toString() : "";
            String topics = body.get("topicsToFocus") != null ? body.get("topicsToFocus").toString() : "";
            String jd = body.get("jobDescription") != null ? body.get("jobDescription").toString() : "";

            Session session = sessionService.createSession(userId, sessionName, jobRole, yearsOfExp, topics, jd);
            return ResponseEntity.status(201).body(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getSessions(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Session> sessions = sessionService.getSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateSession(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Session session = sessionService.activateSession(userId, id);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            sessionService.deleteSession(userId, id);
            return ResponseEntity.ok(Map.of("message", "Session deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
