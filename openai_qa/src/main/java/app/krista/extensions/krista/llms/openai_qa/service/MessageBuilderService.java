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
import app.krista.extensions.krista.llms.openai_qa.model.QuestionRequest;
import app.krista.extensions.krista.llms.openai_qa.model.SessionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service responsible for building OpenAI API message payloads
 * Follows Single Responsibility Principle - only handles message construction
 */
public class MessageBuilderService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageBuilderService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * Builds Chat Completions API payload for simple questions
     */
    public ObjectNode buildChatCompletionsPayload(QuestionRequest request, String model) {
        logger.debug("Building Chat Completions payload for model: {}", model);
        
        ArrayNode messagesArray = createMessagesArray();
        addSystemMessage(messagesArray, request.getInstructions());
        addUserMessage(messagesArray, request.getQuestion());
        
        return createChatPayload(model, messagesArray);
    }
    
    /**
     * Builds Responses API payload for document-based questions
     */
    public ObjectNode buildResponsesPayload(QuestionRequest request, SessionData sessionData, String model) {
        logger.debug("Building Responses API payload for model: {} with {} documents", 
                    model, sessionData.getFileIds().size());
        
        ArrayNode inputArray = createInputArray();
        addInstructionsToInput(inputArray, request.getInstructions());
        addConversationHistory(inputArray, sessionData);
        addUserMessageWithFiles(inputArray, request.getQuestion(), sessionData.getFileIds());
        
        return createResponsesPayload(model, inputArray);
    }
    
    /**
     * Builds Chat Completions fallback payload for file-based requests
     */
    public ObjectNode buildChatFallbackPayload(QuestionRequest request, SessionData sessionData, String model) {
        logger.debug("Building Chat Completions fallback payload for {} documents", 
                    sessionData.getFileIds().size());
        
        ArrayNode messagesArray = createMessagesArray();
        addSystemMessageWithFileContext(messagesArray, request.getInstructions(), sessionData);
        addConversationHistoryAsMessages(messagesArray, sessionData);
        addUserMessage(messagesArray, request.getQuestion());
        
        return createChatPayload(model, messagesArray);
    }
    
    // Private helper methods for message construction
    
    private ArrayNode createMessagesArray() {
        return OBJECT_MAPPER.createArrayNode();
    }
    
    private ArrayNode createInputArray() {
        return OBJECT_MAPPER.createArrayNode();
    }
    
    private void addSystemMessage(ArrayNode messagesArray, String instructions) {
        if (instructions != null && !instructions.trim().isEmpty()) {
            ObjectNode systemMessage = OBJECT_MAPPER.createObjectNode();
            systemMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_SYSTEM);
            systemMessage.put(OpenAiConfiguration.PARAM_CONTENT, 
                "You are an intelligent AI assistant. " + instructions);
            messagesArray.add(systemMessage);
            logger.debug("Added system message with instructions");
        }
    }
    
    private void addUserMessage(ArrayNode messagesArray, String question) {
        ObjectNode userMessage = OBJECT_MAPPER.createObjectNode();
        userMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_USER);
        userMessage.put(OpenAiConfiguration.PARAM_CONTENT, question);
        messagesArray.add(userMessage);
        logger.debug("Added user message");
    }
    
    private void addInstructionsToInput(ArrayNode inputArray, String instructions) {
        if (instructions != null && !instructions.trim().isEmpty()) {
            ObjectNode systemMessage = OBJECT_MAPPER.createObjectNode();
            systemMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_SYSTEM);
            systemMessage.put(OpenAiConfiguration.PARAM_CONTENT, instructions);
            inputArray.add(systemMessage);
            logger.debug("Added instructions to Responses API input");
        }
    }
    
    private void addConversationHistory(ArrayNode inputArray, SessionData sessionData) {
        List<SessionData.ConversationMessage> history = sessionData.getRecentConversationHistory(4);
        for (SessionData.ConversationMessage msg : history) {
            ObjectNode historyMessage = OBJECT_MAPPER.createObjectNode();
            historyMessage.put(OpenAiConfiguration.PARAM_ROLE, msg.getRole());
            historyMessage.put(OpenAiConfiguration.PARAM_CONTENT, msg.getContent());
            inputArray.add(historyMessage);
        }
        logger.debug("Added {} conversation history messages", history.size());
    }
    
    private void addUserMessageWithFiles(ArrayNode inputArray, String question, List<String> fileIds) {
        ObjectNode userMessage = OBJECT_MAPPER.createObjectNode();
        userMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_USER);
        
        ArrayNode contentArray = OBJECT_MAPPER.createArrayNode();
        
        // Add file inputs
        for (String fileId : fileIds) {
            ObjectNode fileInput = OBJECT_MAPPER.createObjectNode();
            fileInput.put(OpenAiConfiguration.PARAM_TYPE, OpenAiConfiguration.TYPE_INPUT_FILE);
            fileInput.put(OpenAiConfiguration.PARAM_FILE_ID, fileId);
            contentArray.add(fileInput);
        }
        
        // Add text input
        ObjectNode textInput = OBJECT_MAPPER.createObjectNode();
        textInput.put(OpenAiConfiguration.PARAM_TYPE, OpenAiConfiguration.TYPE_INPUT_TEXT);
        textInput.put(OpenAiConfiguration.PARAM_TEXT, question);
        contentArray.add(textInput);
        
        userMessage.set(OpenAiConfiguration.PARAM_CONTENT, contentArray);
        inputArray.add(userMessage);
        logger.debug("Added user message with {} file attachments", fileIds.size());
    }
    
    private void addSystemMessageWithFileContext(ArrayNode messagesArray, String instructions, SessionData sessionData) {
        StringBuilder systemPrompt = new StringBuilder();
        if (instructions != null && !instructions.trim().isEmpty()) {
            systemPrompt.append(instructions).append("\n\n");
        }
        systemPrompt.append("You are analyzing uploaded documents. ");
        systemPrompt.append("The user has uploaded ").append(sessionData.getFileIds().size()).append(" document(s). ");
        systemPrompt.append("Please answer questions based on the document content.");

        ObjectNode systemMessage = OBJECT_MAPPER.createObjectNode();
        systemMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_SYSTEM);
        systemMessage.put(OpenAiConfiguration.PARAM_CONTENT, systemPrompt.toString());
        messagesArray.add(systemMessage);
        logger.debug("Added system message with file context for {} documents", sessionData.getFileIds().size());
    }
    
    private void addConversationHistoryAsMessages(ArrayNode messagesArray, SessionData sessionData) {
        List<SessionData.ConversationMessage> history = sessionData.getRecentConversationHistory(4);
        for (SessionData.ConversationMessage msg : history) {
            ObjectNode historyMessage = OBJECT_MAPPER.createObjectNode();
            historyMessage.put(OpenAiConfiguration.PARAM_ROLE, msg.getRole());
            historyMessage.put(OpenAiConfiguration.PARAM_CONTENT, msg.getContent());
            messagesArray.add(historyMessage);
        }
        logger.debug("Added {} conversation history messages", history.size());
    }
    
    private ObjectNode createChatPayload(String model, ArrayNode messagesArray) {
        ObjectNode payload = OBJECT_MAPPER.createObjectNode();
        payload.put(OpenAiConfiguration.PARAM_MODEL, model);
        payload.set(OpenAiConfiguration.PARAM_MESSAGES, messagesArray);
        payload.put(OpenAiConfiguration.PARAM_MAX_TOKENS, OpenAiConfiguration.MAX_TOKENS);
        payload.put(OpenAiConfiguration.PARAM_TEMPERATURE, OpenAiConfiguration.TEMPERATURE);
        return payload;
    }
    
    private ObjectNode createResponsesPayload(String model, ArrayNode inputArray) {
        ObjectNode payload = OBJECT_MAPPER.createObjectNode();
        payload.put(OpenAiConfiguration.PARAM_MODEL, model);
        payload.set(OpenAiConfiguration.PARAM_INPUT, inputArray);
        return payload;
    }
}
