package com.prismhire.repository;

import com.prismhire.entity.SavedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedQuestionRepository extends JpaRepository<SavedQuestion, Long> {
    List<SavedQuestion> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SavedQuestion> findByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);

    Optional<SavedQuestion> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndSessionId(Long userId, Long sessionId);
}
