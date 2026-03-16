package com.prismhire.service;

import com.prismhire.entity.SavedInterviewQuestion;
import com.prismhire.repository.SavedInterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SavedInterviewQuestionService {

    @Autowired
    private SavedInterviewQuestionRepository repository;

    public SavedInterviewQuestion saveSingle(Long userId, Map<String, Object> data) {
        SavedInterviewQuestion q = new SavedInterviewQuestion();
        q.setUserId(userId);
        q.setSessionId(Long.parseLong(data.get("sessionId").toString()));
        q.setJobRole(data.get("jobRole").toString());
        q.setDifficulty(data.getOrDefault("difficulty", "General").toString());
        q.setQuestion(data.get("question").toString());
        q.setShortAnswer(data.get("shortAnswer").toString());
        q.setLongAnswer(data.get("longAnswer").toString());
        if (data.get("options") instanceof List<?> opts) {
            q.setOptions(opts.stream().map(Object::toString).toList());
        }
        if (data.get("correctAnswer") != null)
            q.setCorrectAnswer(data.get("correctAnswer").toString());
        q.setCompleted(false);
        return repository.save(q);
    }

    public void saveBulk(Long userId, List<Map<String, Object>> questions) {
        List<SavedInterviewQuestion> docs = questions.stream().map(data -> {
            SavedInterviewQuestion q = new SavedInterviewQuestion();
            q.setUserId(userId);
            q.setSessionId(Long.parseLong(data.get("sessionId").toString()));
            q.setJobRole(data.get("jobRole").toString());
            q.setDifficulty(data.getOrDefault("difficulty", "General").toString());
            q.setQuestion(data.get("question").toString());
            q.setShortAnswer(data.get("shortAnswer").toString());
            q.setLongAnswer(data.get("longAnswer").toString());
            if (data.get("options") instanceof List<?> opts) {
                q.setOptions(opts.stream().map(Object::toString).toList());
            }
            if (data.get("correctAnswer") != null)
                q.setCorrectAnswer(data.get("correctAnswer").toString());
            return q;
        }).toList();
        repository.saveAll(docs);
    }

    public List<SavedInterviewQuestion> getQuestions(Long userId, Long sessionId, String source) {
        if (sessionId != null) {
            return repository.findByUserIdAndSessionIdOrderByCreatedAtDesc(userId, sessionId);
        }
        if ("completed".equals(source)) {
            return repository.findByUserIdAndIsCompletedOrderByCreatedAtDesc(userId, true);
        }
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public SavedInterviewQuestion markComplete(Long userId, Long sessionId, String questionText) {
        int updated = repository.markAsCompleted(userId, sessionId, questionText);
        if (updated == 0) {
            throw new IllegalArgumentException("Question not found.");
        }
        return repository.findByUserIdAndSessionIdAndQuestion(userId, sessionId, questionText)
                .orElseThrow(() -> new IllegalArgumentException("Question not found."));
    }

    public long countByUserAndSession(Long userId, Long sessionId) {
        return repository.countByUserIdAndSessionId(userId, sessionId);
    }
}
