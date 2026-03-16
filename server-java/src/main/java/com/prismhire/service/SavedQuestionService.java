package com.prismhire.service;

import com.prismhire.entity.SavedQuestion;
import com.prismhire.repository.SavedQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SavedQuestionService {

    @Autowired
    private SavedQuestionRepository repository;

    public void saveQuestions(Long userId, List<Map<String, Object>> questions) {
        List<SavedQuestion> docs = questions.stream().map(q -> {
            SavedQuestion sq = new SavedQuestion();
            sq.setUserId(userId);
            if (q.get("sessionId") != null) {
                try {
                    sq.setSessionId(Long.parseLong(q.get("sessionId").toString()));
                } catch (Exception ignored) {
                }
            }
            sq.setJobRole(q.getOrDefault("jobRole", "General").toString());
            sq.setDifficulty(q.getOrDefault("difficulty", "General").toString());
            sq.setText(q.get("text").toString());
            sq.setType(q.get("type").toString());
            if (q.get("options") instanceof List<?> optList) {
                sq.setOptions(optList.stream().map(Object::toString).toList());
            }
            if (q.get("correctAnswer") != null)
                sq.setCorrectAnswer(q.get("correctAnswer").toString());
            return sq;
        }).toList();

        repository.saveAll(docs);
    }

    public List<SavedQuestion> getSavedQuestions(Long userId, Long sessionId) {
        if (sessionId != null) {
            return repository.findByUserIdAndSessionIdOrderByCreatedAtDesc(userId, sessionId);
        }
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteQuestion(Long userId, Long questionId) {
        SavedQuestion q = repository.findByIdAndUserId(questionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found."));
        repository.delete(q);
    }

    public long countByUserAndSession(Long userId, Long sessionId) {
        return repository.countByUserIdAndSessionId(userId, sessionId);
    }
}
