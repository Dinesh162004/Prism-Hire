package com.prismhire.service;

import com.prismhire.entity.Session;
import com.prismhire.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(Long userId, String sessionName, String jobRole,
            String yearsOfExperience, String topicsToFocus, String jobDescription) {
        if (sessionName == null || sessionName.isBlank() || jobRole == null || jobRole.isBlank()) {
            throw new IllegalArgumentException("Session Name and Job Role are required.");
        }

        // Deactivate all existing sessions
        sessionRepository.deactivateAllByUserId(userId);

        Session s = new Session();
        s.setUserId(userId);
        s.setSessionName(sessionName);
        s.setJobRole(jobRole);
        s.setYearsOfExperience(yearsOfExperience != null ? yearsOfExperience : "");
        s.setTopicsToFocus(topicsToFocus != null ? topicsToFocus : "");
        s.setJobDescription(jobDescription != null ? jobDescription : "");
        s.setActive(true);

        return sessionRepository.save(s);
    }

    public List<Session> getSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Session activateSession(Long userId, Long sessionId) {
        sessionRepository.deactivateAllByUserId(userId);
        Session session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or unauthorized"));
        session.setActive(true);
        return sessionRepository.save(session);
    }

    public void deleteSession(Long userId, Long sessionId) {
        Session session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or unauthorized"));
        sessionRepository.delete(session);
    }
}
