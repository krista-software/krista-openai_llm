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

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.model.base.File;
import app.krista.extension.executor.RemediationAction;
import app.krista.extension.executor.RemediationActions;
import app.krista.extension.executor.impl.InformAPersonAction;
import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionCatalogService;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionService;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.extensions.krista.llms.openai_qa.service.IFileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of discussion catalog service for business logic operations.
 * Handles all validation, processing, and error handling for discussion catalog requests.
 */
public class DiscussionCatalogServiceImpl implements IDiscussionCatalogService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscussionCatalogServiceImpl.class);
    
    private final IDiscussionService discussionService;
    private final IDiscussionValidator validator;
    private final IFileProcessor fileProcessor;
    
    /**
     * Constructor with dependency injection.
     * 
     * @param discussionService Service for discussion operations
     * @param validator Validator for input validation
     * @param fileProcessor Processor for file operations
     */
    public DiscussionCatalogServiceImpl(IDiscussionService discussionService,
                                      IDiscussionValidator validator,
                                      IFileProcessor fileProcessor) {
        this.discussionService = discussionService;
        this.validator = validator;
        this.fileProcessor = fileProcessor;
    }
    
    @Override
    public ExtensionResponse createDiscussionSession() {
        try {
            String sessionId = discussionService.createSession();
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                Map.of("Session Id", sessionId, 
                       "Message", "Discussion session created successfully. Use this Session ID to upload documents and ask questions."),
                null, null, null);
                
        } catch (OpenAiException e) {
            return handleExceptionResponse("creating discussion session", e);
        }
    }
    
    @Override
    public ExtensionResponse addDocumentsToSession(String sessionId, List<File> files) {
        try {
            // Validate session ID
            ValidationResult sessionValidation = validator.validateSessionId(sessionId);
            if (sessionValidation.hasError()) {
                return createErrorResponse(sessionValidation.getErrorMessage());
            }
            
            // Validate files
            ValidationResult fileValidation = validator.validateFiles(files);
            if (fileValidation.hasError()) {
                return createErrorResponse(fileValidation.getErrorMessage());
            }
            
            // Process files
            List<DocumentSet> documents = fileProcessor.processFiles(files);
            
            // Add documents to session
            boolean success = discussionService.addDocuments(sessionId, documents);
            
            if (success) {
                return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Successful", true, 
                           "Message", "Successfully uploaded " + files.size() + " document(s) to the discussion session."),
                    null, null, null);
            } else {
                return createErrorResponse("Failed to upload documents. Please try again or contact support.");
            }
            
        } catch (OpenAiException e) {
            return handleExceptionResponse("adding documents to discussion", e);
        }
    }
    
    @Override
    public ExtensionResponse addInstructionsToSession(String sessionId, String instructions) {
        try {
            // Validate session ID
            ValidationResult sessionValidation = validator.validateSessionId(sessionId);
            if (sessionValidation.hasError()) {
                return createErrorResponse(sessionValidation.getErrorMessage());
            }
            
            // Validate instructions
            ValidationResult instructionValidation = validator.validateInstructions(instructions);
            if (instructionValidation.hasError()) {
                return createErrorResponse(instructionValidation.getErrorMessage());
            }
            
            // Add instructions to session
            boolean success = discussionService.addInstructions(sessionId, List.of(instructions));
            
            if (success) {
                return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Successful", true, 
                           "Message", "Instructions successfully added to the discussion session."),
                    null, null, null);
            } else {
                return createErrorResponse("Failed to add instructions. Please try again or contact support.");
            }
            
        } catch (OpenAiException e) {
            return handleExceptionResponse("adding instructions to discussion", e);
        }
    }
    
    @Override
    public ExtensionResponse deleteDiscussionSession(String sessionId) {
        try {
            // Validate session ID
            ValidationResult sessionValidation = validator.validateSessionId(sessionId);
            if (sessionValidation.hasError()) {
                return createErrorResponse(sessionValidation.getErrorMessage());
            }
            
            // Delete session
            boolean success = discussionService.deleteSession(sessionId);
            
            if (success) {
                return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Successful", true, 
                           "Message", "Discussion session deleted successfully."),
                    null, null, null);
            } else {
                return createErrorResponse("Failed to delete discussion session. The session may not exist or there was a system error.");
            }
            
        } catch (OpenAiException e) {
            return handleExceptionResponse("deleting discussion session", e);
        }
    }
    
    /**
     * Creates a simple error response.
     * 
     * @param errorMessage The error message
     * @return ExtensionResponse with error details
     */
    private ExtensionResponse createErrorResponse(String errorMessage) {
        return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
            Map.of("Successful", false, "Error", errorMessage),
            null, null, null);
    }
    
    /**
     * Handle exceptions and create appropriate error responses with logging and remediation actions.
     * 
     * @param operation Description of the operation that failed
     * @param cause The exception that occurred
     * @return ExtensionResponse with error details and remediation actions
     */
    private ExtensionResponse handleExceptionResponse(String operation, Exception cause) {
        String errorMessage = "An error occurred while " + operation;
        LOGGER.error("Error during {}: {}", operation, cause.getMessage(), cause);

        List<RemediationAction> actions = new ArrayList<>();
        
        // Context-aware remediation messages
        String remediationMessage;
        if (operation.contains("document") || operation.contains("file")) {
            long maxSizeMB = OpenAiConfiguration.MAX_FILE_SIZE_BYTES / (1024 * 1024);
            remediationMessage = "Please check the file format (PDF only) and size (max " + maxSizeMB + "MB), then try again. If the issue persists, contact support.";
        } else if (operation.contains("session") || operation.contains("discussion")) {
            remediationMessage = "Please verify the session ID is valid and try again. If the issue persists, contact support.";
        } else if (operation.contains("instruction")) {
            remediationMessage = "Please check the instruction length (max " + OpenAiConfiguration.MAX_INSTRUCTION_LENGTH + " characters) and try again. If the issue persists, contact support.";
        } else {
            remediationMessage = "Please check the input parameters and try again. If the issue persists, contact support.";
        }
        
        actions.add(InformAPersonAction.create(remediationMessage,
                RemediationAction.RecipientType.ALL_PARTICIPANTS, null));

        RemediationActions remediationActions = new RemediationActions(actions, null);

        return new ExtensionResponseBuilder()
                .success(Collections.emptyMap(), remediationActions, Map.of("ERROR", errorMessage))
                .build();
    }
}
