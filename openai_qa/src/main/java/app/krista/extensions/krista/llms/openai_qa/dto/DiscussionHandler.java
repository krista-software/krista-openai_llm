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


package app.krista.extensions.krista.llms.openai_qa.dto;

import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.service.OpenAiClientService;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Refactored DiscussionHandler using modular services
 */
@Service
public class DiscussionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionHandler.class);
    private final OpenAiClientService openAiClientService;

    @Inject
    public DiscussionHandler(OpenAiClientService openAiClientService) {
        this.openAiClientService = openAiClientService;
        logger.info("DiscussionHandler initialized with dependency injection");
    }

    public String askQuestion(String sessionId, String question) throws IOException {
        try {

            Map<String, Object> sessionInfo = openAiClientService.getSessionInfo(sessionId);
            if (!sessionInfo.get("exists").equals(true)) {
                throw new IllegalStateException("Session not found: " + sessionId);
            }

            String response = openAiClientService.askQuestion(sessionId, question, null);
            logger.debug("Successfully processed question in session: {}", sessionId);
            return response;

        } catch (OpenAiException cause) {
            logger.error("Failed to process question in session {}: {}", sessionId, cause.getMessage());
            throw new IOException("Failed to process question", cause);
        }
    }

    public String createDocumentSet() {

        String sessionId = openAiClientService.createSession();
        logger.info("Created session: {}", sessionId);
        return sessionId;
    }

    public boolean addDocuments(String sessionId, List<DocumentSet> documents) throws IOException {
        try {

            for (DocumentSet document : documents) {
                openAiClientService.addDocumentToSession(sessionId, document);
                logger.debug("Added document to session {}: {}", sessionId, document.getName());
            }

            logger.info("Added {} documents to session: {}", documents.size(), sessionId);
            return true;

        } catch (OpenAiException cause) {
            logger.error("Failed to add documents to session {}: {}", sessionId, cause.getMessage());
            throw new IOException("Failed to add documents", cause);
        }
    }

    public boolean addInstructions(String sessionId, List<String> instructions) throws IOException {
        try {

            if (instructions != null && !instructions.isEmpty()) {
                String joinedInstructions = String.join("\n", instructions);
                openAiClientService.addInstructionsToSession(sessionId, joinedInstructions);
                logger.info("Added instructions to session {}: {}", sessionId, joinedInstructions);
            }
            return true;

        } catch (OpenAiException cause) {
            logger.error("Failed to add instructions to session {}: {}", sessionId, cause.getMessage());
            throw new IOException("Failed to add instructions", cause);
        }
    }

    public void closeSession(String sessionId) {
        try {
            openAiClientService.closeSession(sessionId, false);
            logger.info("Closed session: {}", sessionId);
        } catch (Exception cause) {
            logger.warn("Error closing session {}: {}", sessionId, cause.getMessage());
        }
    }
}
