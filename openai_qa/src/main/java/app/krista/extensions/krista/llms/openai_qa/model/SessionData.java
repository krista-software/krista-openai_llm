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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data model for discussion session
 */
public class SessionData {
    
    private final String sessionId;
    private final Map<String, String> documents; // fileName -> fileId
    private final List<String> documentOrder;
    private final List<ConversationMessage> conversationHistory;
    private final long createdAt;
    private String instructions;
    
    public SessionData(String sessionId) {
        this.sessionId = sessionId;
        this.documents = new ConcurrentHashMap<>();
        this.documentOrder = Collections.synchronizedList(new ArrayList<>());
        this.conversationHistory = Collections.synchronizedList(new ArrayList<>());
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * Adds document to session
     */
    public void addDocument(String fileName, String fileId) {
        documents.put(fileName, fileId);
        if (!documentOrder.contains(fileName)) {
            documentOrder.add(fileName);
        }
    }
    
    /**
     * Gets all file IDs in order
     */
    public List<String> getFileIds() {
        List<String> fileIds = new ArrayList<>();
        for (String fileName : documentOrder) {
            String fileId = documents.get(fileName);
            if (fileId != null) {
                fileIds.add(fileId);
            }
        }
        return fileIds;
    }
    
    /**
     * Gets file ID for specific document
     */
    public String getFileId(String fileName) {
        return documents.get(fileName);
    }
    
    /**
     * Gets all document names
     */
    public Set<String> getDocumentNames() {
        return new HashSet<>(documents.keySet());
    }
    
    /**
     * Gets document count
     */
    public int getDocumentCount() {
        return documents.size();
    }
    
    /**
     * Sets instructions for session
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    /**
     * Gets instructions for session
     */
    public String getInstructions() {
        return instructions;
    }
    
    /**
     * Adds conversation message
     */
    public void addConversationMessage(String role, String content) {
        conversationHistory.add(new ConversationMessage(role, content));
    }
    
    /**
     * Gets recent conversation history
     */
    public List<ConversationMessage> getRecentConversationHistory(int maxMessages) {
        int start = Math.max(0, conversationHistory.size() - maxMessages);
        return new ArrayList<>(conversationHistory.subList(start, conversationHistory.size()));
    }
    
    /**
     * Gets all conversation history
     */
    public List<ConversationMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    /**
     * Clears conversation history
     */
    public void clearConversationHistory() {
        conversationHistory.clear();
    }
    
    // Getters
    public String getSessionId() {
        return sessionId;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Conversation message model
     */
    public static class ConversationMessage {
        private final String role;
        private final String content;
        private final long timestamp;
        
        public ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getRole() {
            return role;
        }
        
        public String getContent() {
            return content;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
