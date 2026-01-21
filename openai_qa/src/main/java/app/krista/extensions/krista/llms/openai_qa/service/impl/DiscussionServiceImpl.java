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

package app.krista.extensions.krista.llms.openai_qa.service.impl;

import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.dto.DiscussionHandler;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionService;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of discussion service for session management operations.
 * Provides session lifecycle management with proper validation and error handling.
 */
public class DiscussionServiceImpl implements IDiscussionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscussionServiceImpl.class);
    
    private final DiscussionHandler discussionHandler;
    private final IDiscussionValidator validator;

    /**
     * Creates a discussion service with required dependencies.
     *
     * @param discussionHandler The discussion handler for core operations
     * @param validator The validator for input validation
     */
    public DiscussionServiceImpl(DiscussionHandler discussionHandler,
                               IDiscussionValidator validator) {
        this.discussionHandler = discussionHandler;
        this.validator = validator;
    }
    
    @Override
    public String createSession() throws OpenAiException {
        logger.info("Creating new discussion session");
        
        try {
            String sessionId = discussionHandler.createDocumentSet();
            logger.info("Discussion session created successfully: {}", maskSessionId(sessionId));
            return sessionId;

        } catch (Exception e) {
            logger.error("Failed to create discussion session: {}", e.getMessage());
            throw new OpenAiException("Failed to create discussion session: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean addDocuments(String sessionId, List<DocumentSet> documents) throws OpenAiException {
        logger.info("Adding {} documents to session: {}", documents.size(), maskSessionId(sessionId));
        
        // Validate session ID
        ValidationResult sessionValidation = validator.validateSessionId(sessionId);
        if (sessionValidation.hasError()) {
            throw new OpenAiException(sessionValidation.getErrorMessage());
        }
        
        // Validate documents
        if (documents == null || documents.isEmpty()) {
            throw new OpenAiException("No documents provided for upload");
        }
        
        try {
            boolean result = discussionHandler.addDocuments(sessionId, documents);
            if (result) {
                logger.info("Successfully added {} documents to session: {}", documents.size(), maskSessionId(sessionId));
            } else {
                logger.warn("Failed to add documents to session: {}", maskSessionId(sessionId));
            }
            return result;
            
        } catch (Exception e) {
            logger.error("Error adding documents to session {}: {}", maskSessionId(sessionId), e.getMessage());
            throw new OpenAiException("Failed to add documents: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean addInstructions(String sessionId, List<String> instructions) throws OpenAiException {
        logger.info("Adding instructions to session: {}", maskSessionId(sessionId));

        // Validate session ID
        ValidationResult sessionValidation = validator.validateSessionId(sessionId);
        if (sessionValidation.hasError()) {
            throw new OpenAiException(sessionValidation.getErrorMessage());
        }

        // Validate instructions
        if (instructions == null || instructions.isEmpty()) {
            throw new OpenAiException("No instructions provided");
        }

        for (String instruction : instructions) {
            ValidationResult instructionValidation = validator.validateInstructions(instruction);
            if (instructionValidation.hasError()) {
                throw new OpenAiException(instructionValidation.getErrorMessage());
            }
        }

        try {
            boolean result = discussionHandler.addInstructions(sessionId, instructions);
            if (result) {
                logger.info("Successfully added instructions to session: {}", maskSessionId(sessionId));
            } else {
                logger.warn("Failed to add instructions to session: {}", maskSessionId(sessionId));
            }
            return result;
            
        } catch (Exception e) {
            logger.error("Error adding instructions to session {}: {}", maskSessionId(sessionId), e.getMessage());
            throw new OpenAiException("Failed to add instructions: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String askQuestion(String sessionId, String question) throws OpenAiException {
        logger.info("Processing question for session: {}", maskSessionId(sessionId));
        
        // Validate session ID
        ValidationResult sessionValidation = validator.validateSessionId(sessionId);
        if (sessionValidation.hasError()) {
            throw new OpenAiException(sessionValidation.getErrorMessage());
        }
        
        // Validate question
        ValidationResult questionValidation = validator.validateQuestion(question);
        if (questionValidation.hasError()) {
            throw new OpenAiException(questionValidation.getErrorMessage());
        }
        
        try {
            String response = discussionHandler.askQuestion(sessionId, question);
            
            if (response == null || response.trim().isEmpty()) {
                throw new OpenAiException("No response generated. Please check if documents are uploaded and try again.");
            }
            
            logger.info("Question processed successfully for session: {}", maskSessionId(sessionId));
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing question for session {}: {}", maskSessionId(sessionId), e.getMessage());
            throw new OpenAiException("Failed to process question: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteSession(String sessionId) throws OpenAiException {
        logger.info("Deleting session: {}", maskSessionId(sessionId));
        
        // Validate session ID
        ValidationResult sessionValidation = validator.validateSessionId(sessionId);
        if (sessionValidation.hasError()) {
            throw new OpenAiException(sessionValidation.getErrorMessage());
        }
        
        try {
            discussionHandler.closeSession(sessionId);
            logger.info("Successfully deleted session: {}", maskSessionId(sessionId));
            return true;

        } catch (Exception e) {
            logger.error("Error deleting session {}: {}", maskSessionId(sessionId), e.getMessage());
            throw new OpenAiException("Failed to delete session: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isValidSession(String sessionId) {
        ValidationResult result = validator.validateSessionId(sessionId);
        return result.isValid();
    }
    
    /**
     * Masks session ID for secure logging (shows only first and last 4 characters).
     * 
     * @param sessionId The session ID to mask
     * @return Masked session ID for logging
     */
    private String maskSessionId(String sessionId) {
        if (sessionId == null || sessionId.length() < 8) {
            return "****";
        }
        return sessionId.substring(0, 4) + "****" + sessionId.substring(sessionId.length() - 4);
    }
}
