/*
 * Openai Llm Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.krista.llms.openai_qa.model;

/**
 * Request model for asking questions
 */
public class QuestionRequest {
    
    private final String sessionId;
    private final String question;
    private final String instructions;
    
    private QuestionRequest(Builder builder) {
        this.sessionId = builder.sessionId;
        this.question = builder.question;
        this.instructions = builder.instructions;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String sessionId;
        private String question;
        private String instructions;
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public Builder question(String question) {
            this.question = question;
            return this;
        }
        
        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }
        
        public QuestionRequest build() {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                throw new IllegalArgumentException("Session ID is required");
            }
            if (question == null || question.trim().isEmpty()) {
                throw new IllegalArgumentException("Question is required");
            }
            return new QuestionRequest(this);
        }
    }
}
