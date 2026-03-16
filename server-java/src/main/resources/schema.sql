-- PrismHire MySQL Schema
-- Run this script once to set up the database

CREATE DATABASE IF NOT EXISTS prismhire;
USE prismhire;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile_picture TEXT DEFAULT '',
    theme ENUM('light', 'dark') DEFAULT 'light',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sessions Table
CREATE TABLE IF NOT EXISTS sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_name VARCHAR(255) NOT NULL,
    job_role VARCHAR(255) NOT NULL,
    years_of_experience VARCHAR(100) DEFAULT '',
    topics_to_focus TEXT DEFAULT '',
    job_description TEXT DEFAULT '',
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Saved Questions (MCQ Test Questions)
CREATE TABLE IF NOT EXISTS saved_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT,
    job_role VARCHAR(255) NOT NULL,
    difficulty VARCHAR(100) NOT NULL,
    text TEXT NOT NULL,
    type VARCHAR(100) NOT NULL,
    options JSON,
    correct_answer VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE SET NULL
);

-- Saved Interview Questions
CREATE TABLE IF NOT EXISTS saved_interview_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    job_role VARCHAR(255) NOT NULL,
    difficulty VARCHAR(100) NOT NULL,
    question TEXT NOT NULL,
    short_answer TEXT NOT NULL,
    long_answer TEXT NOT NULL,
    options JSON,
    correct_answer VARCHAR(500),
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- Resume Analyses
CREATE TABLE IF NOT EXISTS resume_analyses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    job_description TEXT,
    resume_text TEXT,
    resume_file_name VARCHAR(255),
    ats_score DOUBLE DEFAULT 0,
    skill_match_score DOUBLE DEFAULT 0,
    keyword_match_score DOUBLE DEFAULT 0,
    role_relevance_score DOUBLE DEFAULT 0,
    matched_skills JSON,
    missing_skills JSON,
    matched_keywords_percent DOUBLE DEFAULT 0,
    suggestions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
