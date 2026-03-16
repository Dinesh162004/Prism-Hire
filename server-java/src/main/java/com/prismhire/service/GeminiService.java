package com.prismhire.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Calls Gemini REST API with the given prompt and returns the text response.
     */
    private String callGemini(String prompt) throws Exception {
        String url = GEMINI_URL + apiKey;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        if (responseBody == null)
            throw new RuntimeException("Empty response from Gemini API");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        @SuppressWarnings("unchecked")
        Map<String, Object> firstCandidate = candidates.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
        Map<String, Object> firstPart = parts.get(0);
        return (String) firstPart.get("text");
    }

    /**
     * Generate MCQ test questions for a job role/difficulty.
     */
    public Map<String, Object> generateTest(String jobRole, String difficulty, int count,
            String yearsOfExperience, String topicsToFocus,
            String jobDescription) {
        try {
            StringBuilder promptCtx = new StringBuilder(
                    "Generate exactly " + count + " multiple choice interview questions for a " +
                            difficulty + " level " + jobRole + " role.");
            if (yearsOfExperience != null && !yearsOfExperience.isBlank()) {
                promptCtx.append(" The candidate has ").append(yearsOfExperience)
                        .append(" of experience, so tailor the complexity accordingly.");
            }
            if (topicsToFocus != null && !topicsToFocus.isBlank()) {
                promptCtx.append(" PRIORITY FOCUS: The questions MUST heavily focus on these specific topics: ")
                        .append(topicsToFocus).append(".");
            }
            if (jobDescription != null && !jobDescription.isBlank()) {
                String jdSnippet = jobDescription.length() > 500 ? jobDescription.substring(0, 500) + "..."
                        : jobDescription;
                promptCtx.append(" Context from Job Description: ").append(jdSnippet)
                        .append(" Use this context to make practical scenario-based questions.");
            }

            long testId = System.currentTimeMillis();
            String prompt = promptCtx + "\nReturn ONLY a JSON object with this structure:\n" +
                    "{\"testId\": \"" + testId + "\", \"title\": \"" + jobRole + " Assessment (" + difficulty + ")\", "
                    +
                    "\"questions\": [{\"id\": 1, \"text\": \"Question text\", \"type\": \"concept|code|behavioral\", " +
                    "\"options\": [\"A\", \"B\", \"C\", \"D\"], \"correctAnswer\": \"A\"}]}\n" +
                    "Do not include markdown formatting (like ```json). Just the raw JSON string.";

            String text = callGemini(prompt);
            String cleaned = text.replaceAll("```json", "").replaceAll("```", "").trim();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(cleaned, Map.class);
            return result;

        } catch (Exception e) {
            // Fallback
            return buildFallbackTest(jobRole, difficulty, count);
        }
    }

    /**
     * Generate interview prep questions with short + long answers.
     */
    public Map<String, Object> generateInterview(String jobRole, String difficulty, int count,
            String yearsOfExperience, String topicsToFocus,
            String jobDescription) {
        try {
            StringBuilder promptCtx = new StringBuilder(
                    "Generate exactly " + count + " in-depth interview questions for a " +
                            difficulty + " level " + jobRole + " role.");
            if (yearsOfExperience != null && !yearsOfExperience.isBlank()) {
                promptCtx.append(" The candidate has ").append(yearsOfExperience).append(" of experience.");
            }
            if (topicsToFocus != null && !topicsToFocus.isBlank()) {
                promptCtx.append(" PRIORITY FOCUS: ").append(topicsToFocus).append(".");
            }
            if (jobDescription != null && !jobDescription.isBlank()) {
                String jdSnippet = jobDescription.length() > 500 ? jobDescription.substring(0, 500) + "..."
                        : jobDescription;
                promptCtx.append(" Context: ").append(jdSnippet);
            }

            long interviewId = System.currentTimeMillis();
            String prompt = promptCtx +
                    "\nFor EACH question, provide: question text, shortAnswer (1-2 sentences), " +
                    "longAnswer (detailed with code example), 4 options, correctAnswer.\n" +
                    "Return ONLY a JSON object:\n" +
                    "{\"interviewId\": \"" + interviewId + "\", \"title\": \"" + jobRole + " Interview Prep ("
                    + difficulty + ")\", " +
                    "\"questions\": [{\"id\":1,\"question\":\"...\",\"shortAnswer\":\"...\",\"longAnswer\":\"...\"," +
                    "\"options\":[\"A\",\"B\",\"C\",\"D\"],\"correctAnswer\":\"A\"}]}\n" +
                    "Do not include markdown formatting. Just the raw JSON string.";

            String text = callGemini(prompt);
            String cleaned = text.replaceAll("```json", "").replaceAll("```", "").trim();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(cleaned, Map.class);
            return result;

        } catch (Exception e) {
            return buildFallbackInterview(jobRole, difficulty, count);
        }
    }

    // Fallback helpers
    private Map<String, Object> buildFallbackTest(String jobRole, String difficulty, int count) {
        List<Map<String, Object>> questions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            questions.add(Map.of(
                    "id", i,
                    "text", "(AI Unavailable) Sample " + difficulty + " question for " + jobRole + " #" + i,
                    "type", i % 2 == 0 ? "code" : "concept",
                    "options", List.of("Option A", "Option B", "Option C", "Option D"),
                    "correctAnswer", "Option A"));
        }
        return Map.of(
                "testId", System.currentTimeMillis(),
                "title", jobRole + " Assessment (" + difficulty + ") [Fallback]",
                "questions", questions);
    }

    private Map<String, Object> buildFallbackInterview(String jobRole, String difficulty, int count) {
        List<Map<String, Object>> questions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            questions.add(Map.of(
                    "id", i,
                    "question",
                    "(AI Unavailable) Sample " + difficulty + " interview question for " + jobRole + " #" + i,
                    "shortAnswer", "This is a placeholder short answer.",
                    "longAnswer", "This is a placeholder long answer with detailed explanation and examples.",
                    "options", List.of("Option A", "Option B", "Option C", "Option D"),
                    "correctAnswer", "Option A"));
        }
        return Map.of(
                "interviewId", System.currentTimeMillis(),
                "title", jobRole + " Interview Prep (" + difficulty + ") [Fallback]",
                "questions", questions);
    }
}
