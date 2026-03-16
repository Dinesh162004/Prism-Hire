package com.prismhire.repository;

import com.prismhire.entity.SavedInterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SavedInterviewQuestionRepository extends JpaRepository<SavedInterviewQuestion, Long> {
    List<SavedInterviewQuestion> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SavedInterviewQuestion> findByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);

    List<SavedInterviewQuestion> findByUserIdAndIsCompletedOrderByCreatedAtDesc(Long userId, boolean isCompleted);

    Optional<SavedInterviewQuestion> findByUserIdAndSessionIdAndQuestion(Long userId, Long sessionId, String question);

    long countByUserIdAndSessionId(Long userId, Long sessionId);

    @Modifying
    @Transactional
    @Query("UPDATE SavedInterviewQuestion q SET q.isCompleted = true WHERE q.userId = :userId AND q.sessionId = :sessionId AND q.question = :question")
    int markAsCompleted(Long userId, Long sessionId, String question);
}
