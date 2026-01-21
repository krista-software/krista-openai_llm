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
import app.krista.extensions.krista.llms.openai_qa.model.QuestionRequest;
import app.krista.extensions.krista.llms.openai_qa.model.SessionData;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Refactored OpenAI Client Service following Single Responsibility Principle
 * <p>
 * This service orchestrates specialized services for different business operations:
 * - SessionService: Session lifecycle management
 * - FileUploadService: Document upload operations
 * - OpenAiApiService: AI question processing
 * <p>
 * Each method has a single, clear responsibility and is developer-friendly:
 * - createSession(): Creates new discussion session
 * - addDocumentToSession(): Uploads and attaches documents
 * - addInstructionsToSession(): Sets behavior instructions
 * - askQuestion(): Processes questions with context
 * - getSessionInfo(): Retrieves session metadata
 * - closeSession(): Cleans up session resources
 */
@Service
public class OpenAiClientService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiClientService.class);

    private final SessionService sessionService;
    private final FileUploadService fileUploadService;
    private final OpenAiApiService apiService;

    @Inject
    public OpenAiClientService(FileUploadService fileUploadService,OpenAiApiService apiService) {

        this.sessionService = new SessionService();
        this.fileUploadService = fileUploadService;
        this.apiService = apiService;
    }

    /**
     * Creates a new discussion session
     */
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessionService.createSession(sessionId);
        return sessionId;
    }

    /**
     * Adds document to session
     */
    public String addDocumentToSession(String sessionId, DocumentSet document) throws OpenAiException {
        // Upload file to OpenAI
        String fileId = fileUploadService.uploadFile(document.getFileBytes(), document.getName());

        // Add to session
        sessionService.addDocumentToSession(sessionId, document.getName(), fileId);

        return fileId;
    }

    /**
     * Adds instructions to session
     */
    public void addInstructionsToSession(String sessionId, String instructions) throws OpenAiException {
        sessionService.addInstructionsToSession(sessionId, instructions);
    }

    /**
     * Asks question in session
     */
    public String askQuestion(String sessionId, String question, String instructions) throws OpenAiException {
        // Get session data
        SessionData sessionData = sessionService.getSession(sessionId);

        // Use session instructions if no specific instructions provided
        String effectiveInstructions = instructions;
        if (effectiveInstructions == null || effectiveInstructions.trim().isEmpty()) {
            effectiveInstructions = sessionData.getInstructions();
        }

        // Build request
        QuestionRequest request = QuestionRequest.builder()
                .sessionId(sessionId)
                .question(question)
                .instructions(effectiveInstructions)
                .build();

        // Process question
        String response = apiService.processQuestion(request, sessionData);

        // Save conversation history
        sessionService.addConversationMessage(sessionId, "user", question);
        sessionService.addConversationMessage(sessionId, "assistant", response);

        return response;
    }

    /**
     * Gets session information
     */
    public Map<String, Object> getSessionInfo(String sessionId) {
        Map<String, Object> info = new HashMap<>();
        info.put("exists", sessionService.sessionExists(sessionId));

        if (sessionService.sessionExists(sessionId)) {
            try {
                SessionData session = sessionService.getSession(sessionId);
                info.put("documentCount", session.getDocumentCount());
                info.put("hasInstructions", session.getInstructions() != null);
                info.put("conversationCount", session.getConversationHistory().size());
                info.put("createdAt", session.getCreatedAt());
            } catch (OpenAiException cause) {
                logger.warn("Error getting session info for {}: {}", sessionId, cause.getMessage());
            }
        }

        return info;
    }

    /**
     * Closes session
     */
    public void closeSession(String sessionId, boolean deleteFiles) {
        sessionService.deleteSession(sessionId);
    }

    /**
     * Gets session count for monitoring
     */
    public int getSessionCount() {
        return sessionService.getSessionCount();
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        try {
            fileUploadService.cleanup();
            apiService.cleanup();
            logger.info("OpenAI client service cleanup completed");
        } catch (Exception cause) {
            logger.error("Error during cleanup: {}", cause.getMessage(), cause);
        }
    }
}
