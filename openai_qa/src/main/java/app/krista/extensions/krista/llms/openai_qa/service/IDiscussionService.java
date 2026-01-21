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

package app.krista.extensions.krista.llms.openai_qa.service;

import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;

import java.util.List;

/**
 * Interface for discussion management operations.
 * Provides abstraction for session lifecycle, document handling, and instruction management.
 */
public interface IDiscussionService {
    
    /**
     * Creates a new discussion session.
     * 
     * @return Session ID for the created discussion
     * @throws OpenAiException if session creation fails
     */
    String createSession() throws OpenAiException;
    
    /**
     * Adds documents to an existing discussion session.
     * 
     * @param sessionId The session identifier
     * @param documents List of documents to add
     * @return true if documents were added successfully, false otherwise
     * @throws OpenAiException if document addition fails
     */
    boolean addDocuments(String sessionId, List<DocumentSet> documents) throws OpenAiException;
    
    /**
     * Adds instructions to an existing discussion session.
     * 
     * @param sessionId The session identifier
     * @param instructions List of instruction strings
     * @return true if instructions were added successfully, false otherwise
     * @throws OpenAiException if instruction addition fails
     */
    boolean addInstructions(String sessionId, List<String> instructions) throws OpenAiException;
    
    /**
     * Processes a question within a discussion session context.
     * 
     * @param sessionId The session identifier
     * @param question The question to process
     * @return AI-generated response to the question
     * @throws OpenAiException if question processing fails
     */
    String askQuestion(String sessionId, String question) throws OpenAiException;
    
    /**
     * Deletes a discussion session and all associated data.
     * 
     * @param sessionId The session identifier
     * @return true if session was deleted successfully, false otherwise
     * @throws OpenAiException if session deletion fails
     */
    boolean deleteSession(String sessionId) throws OpenAiException;
    
    /**
     * Validates if a session exists and is accessible.
     * 
     * @param sessionId The session identifier to validate
     * @return true if session exists and is valid, false otherwise
     */
    boolean isValidSession(String sessionId);
}
