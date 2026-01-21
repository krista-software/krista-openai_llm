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

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing discussion sessions
 */
public class SessionService {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    
    /**
     * Creates a new discussion session
     */
    public SessionData createSession(String sessionId) {
        SessionData sessionData = new SessionData(sessionId);
        sessions.put(sessionId, sessionData);
        logger.info("Created new session: {}", sessionId);
        return sessionData;
    }
    
    /**
     * Gets session data by ID
     */
    public SessionData getSession(String sessionId) throws OpenAiException {
        SessionData session = sessions.get(sessionId);
        if (session == null) {
            throw new OpenAiException("Session not found: " + sessionId);
        }
        return session;
    }
    
    /**
     * Checks if session exists
     */
    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    /**
     * Adds document to session
     */
    public void addDocumentToSession(String sessionId, String fileName, String fileId) throws OpenAiException {
        SessionData session = getSession(sessionId);
        session.addDocument(fileName, fileId);
        logger.info("Added document to session {}: {} (File ID: {})", sessionId, fileName, fileId);
    }
    
    /**
     * Adds instructions to session
     */
    public void addInstructionsToSession(String sessionId, String instructions) throws OpenAiException {
        SessionData session = getSession(sessionId);
        session.setInstructions(instructions);
        logger.info("Added instructions to session {}: {}", sessionId, instructions);
    }
    
    /**
     * Gets instructions for session
     */
    public String getInstructions(String sessionId) throws OpenAiException {
        SessionData session = getSession(sessionId);
        return session.getInstructions();
    }
    
    /**
     * Gets file IDs for session
     */
    public List<String> getFileIds(String sessionId) throws OpenAiException {
        SessionData session = getSession(sessionId);
        return session.getFileIds();
    }
    
    /**
     * Adds conversation message to session
     */
    public void addConversationMessage(String sessionId, String role, String content) throws OpenAiException {
        SessionData session = getSession(sessionId);
        session.addConversationMessage(role, content);
    }
    
    /**
     * Gets recent conversation history
     */
    public List<SessionData.ConversationMessage> getRecentConversationHistory(String sessionId) throws OpenAiException {
        SessionData session = getSession(sessionId);
        return session.getRecentConversationHistory(OpenAiConfiguration.MAX_CONVERSATION_HISTORY);
    }
    
    /**
     * Deletes session and all associated data
     */
    public void deleteSession(String sessionId) {
        SessionData session = sessions.remove(sessionId);
        if (session != null) {
            logger.info("Deleted session: {}", sessionId);
        } else {
            logger.warn("Attempted to delete non-existent session: {}", sessionId);
        }
    }
    
    /**
     * Gets session count for monitoring
     */
    public int getSessionCount() {
        return sessions.size();
    }
}
