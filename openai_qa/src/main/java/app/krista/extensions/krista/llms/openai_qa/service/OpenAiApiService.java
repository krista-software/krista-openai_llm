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

import app.krista.extension.executor.Invoker;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.factory.DiscussionFactory;
import app.krista.extensions.krista.llms.openai_qa.model.QuestionRequest;
import app.krista.extensions.krista.llms.openai_qa.model.SessionData;
import app.krista.extensions.krista.llms.openai_qa.OpenAiAttributes;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Refactored OpenAI API Service following Single Responsibility Principle
 * Orchestrates specialized services for different concerns:
 * - MessageBuilderService: Constructs API payloads
 * - HttpRequestService: Handles HTTP communication
 * - API routing logic: Determines which API to use
 */
@Service
public class OpenAiApiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiApiService.class);

    private final String model;
    private final MessageBuilderService messageBuilder;
    private final HttpRequestService httpRequestService;
    private final IHttpClientFactory httpClientFactory;
    private final OpenAiAttributes openAiAttributes;

    /**
     * Constructor with dependency injection
     * Initializes specialized services for different responsibilities
     *
     * @param httpRequestService Injected HTTP request service with OpenAI attributes
     */
    @Inject
    public OpenAiApiService(HttpRequestService httpRequestService,Invoker invoker) {
        this.openAiAttributes = new OpenAiAttributes(invoker);
        this.model = openAiAttributes.getModel(); // Get model from attributes
        this.httpClientFactory = DiscussionFactory.getInstance().createHttpClientFactory();

        // Initialize specialized services
        this.messageBuilder = new MessageBuilderService();
        this.httpRequestService = httpRequestService;

        logger.info("OpenAI API Service initialized with model: {}", model);
    }
    
    /**
     * Main entry point: Processes question using appropriate API
     * Implements API routing logic based on document presence
     */
    public String processQuestion(QuestionRequest request, SessionData sessionData) throws OpenAiException {
        validateRequest(request);

        List<String> fileIds = sessionData.getFileIds();

        if (fileIds.isEmpty()) {
            return processSimpleQuestion(request);
        } else {
            return processDocumentBasedQuestion(request, sessionData);
        }
    }

    /**
     * Processes simple questions without documents using Chat Completions API
     */
    private String processSimpleQuestion(QuestionRequest request) throws OpenAiException {
        logger.info("Processing simple question using Chat Completions API");

        ObjectNode payload = messageBuilder.buildChatCompletionsPayload(request, model);
        return httpRequestService.sendChatCompletionsRequest(payload);
    }

    /**
     * Processes document-based questions using Responses API with fallback
     */
    private String processDocumentBasedQuestion(QuestionRequest request, SessionData sessionData) throws OpenAiException {
        logger.info("Processing document-based question with {} files", sessionData.getFileIds().size());

        try {
            // Try Responses API first
            ObjectNode payload = messageBuilder.buildResponsesPayload(request, sessionData, model);
            return httpRequestService.sendResponsesApiRequest(payload);

        } catch (OpenAiException e) {
            logger.warn("Responses API failed, falling back to Chat Completions: {}", e.getMessage());
            return processFallbackRequest(request, sessionData);
        }
    }

    /**
     * Fallback method using Chat Completions API for document-based questions
     */
    private String processFallbackRequest(QuestionRequest request, SessionData sessionData) throws OpenAiException {
        logger.info("Using Chat Completions fallback for document-based question");

        ObjectNode payload = messageBuilder.buildChatFallbackPayload(request, sessionData, model);
        return httpRequestService.sendChatCompletionsRequest(payload);
    }
    
    /**
     * Validates the incoming request
     */
    private void validateRequest(QuestionRequest request) throws OpenAiException {
        if (request == null) {
            throw new OpenAiException("Question request cannot be null");
        }

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new OpenAiException("Question cannot be null or empty");
        }

        if (model == null || model.trim().isEmpty()) {
            throw new OpenAiException("Model cannot be null or empty");
        }

        logger.debug("Request validation passed for question length: {}", request.getQuestion().length());
    }

    /**
     * Gets current model being used
     */
    public String getCurrentModel() {
        return model;
    }

    /**
     * Gets HTTP client statistics for monitoring
     */
    public String getHttpClientStats() {
        return httpRequestService.getHttpClientStats();
    }

    /**
     * Cleanup resources using HTTP client factory
     */
    public void cleanup() {
        if (httpClientFactory != null) {
            httpClientFactory.cleanup(null); // HttpRequestService manages its own client
        }
        logger.debug("OpenAI API Service cleanup completed");
    }
}
