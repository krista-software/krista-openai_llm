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
import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.OpenAiAttributes;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for making HTTP requests to OpenAI API
 * Follows Single Responsibility Principle - only handles HTTP communication
 * Gets API key and model from injected OpenAiAttributes
 */
@Service
public class HttpRequestService {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestService.class);

    private final OkHttpClient httpClient;
    private final OpenAiAttributes openAiAttributes;
    private final ResponseParserService responseParser;

    @Inject
    public HttpRequestService(Invoker invoker) {
        this.openAiAttributes = new OpenAiAttributes(invoker);
        this.httpClient = createHttpClient();
        this.responseParser = new ResponseParserService();

        logger.info("HttpRequestService initialized with OpenAI attributes");
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    /**
     * Sends request to Chat Completions API
     */
    public String sendChatCompletionsRequest(ObjectNode payload) throws OpenAiException {
        logger.info("Sending Chat Completions API request");
        logger.debug("Chat Completions payload: {}", payload.toString());
        
        String responseBody = sendRequest(OpenAiConfiguration.OPENAI_CHAT_COMPLETIONS_API_URL, payload);
        return responseParser.parseChatCompletionsResponse(responseBody);
    }
    
    /**
     * Sends request to Responses API
     */
    public String sendResponsesApiRequest(ObjectNode payload) throws OpenAiException {
        logger.info("Sending Responses API request");
        logger.debug("Responses API payload: {}", payload.toString());
        
        String responseBody = sendRequest(OpenAiConfiguration.OPENAI_RESPONSES_API_URL, payload);
        return responseParser.parseResponsesApiResponse(responseBody);
    }
    
    /**
     * Generic method to send HTTP POST request to OpenAI API
     */
    private String sendRequest(String url, ObjectNode payload) throws OpenAiException {
        Request request = buildHttpRequest(url, payload);
        
        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response);
            
        } catch (IOException cause) {
            logger.error("Network error during API call to {}: {}", url, cause.getMessage());
            throw new OpenAiException("Network error during API call: " + cause.getMessage(), cause);
        }
    }
    
    /**
     * Builds HTTP request with proper headers and body
     */
    private Request buildHttpRequest(String url, ObjectNode payload) {
        RequestBody requestBody = RequestBody.create(
            MediaType.parse(OpenAiConfiguration.CONTENT_TYPE_JSON),
            payload.toString());

        return new Request.Builder()
                .url(url)
                .addHeader(OpenAiConfiguration.HEADER_CONTENT_TYPE, OpenAiConfiguration.CONTENT_TYPE_JSON)
                .addHeader(OpenAiConfiguration.HEADER_AUTHORIZATION,
                    OpenAiConfiguration.BEARER_PREFIX + openAiAttributes.getApiKey())
                .post(requestBody)
                .build();
    }
    
    /**
     * Handles HTTP response and extracts body
     */
    private String handleResponse(Response response) throws OpenAiException, IOException {
        if (!response.isSuccessful()) {
            return handleErrorResponse(response);
        }
        
        if (response.body() == null) {
            throw new OpenAiException("Empty response from OpenAI API");
        }
        
        String responseBody = response.body().string();
        responseParser.validateResponseBody(responseBody);
        
        logger.debug("Received successful response, length: {}", responseBody.length());
        return responseBody;
    }
    
    /**
     * Handles error responses from OpenAI API
     */
    private String handleErrorResponse(Response response) throws OpenAiException, IOException {
        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
        String errorMessage = responseParser.extractErrorMessage(errorBody);
        
        logger.error("API call failed (HTTP {}): {}", response.code(), errorMessage);
        
        throw new OpenAiException(String.format("API call failed (HTTP %d): %s", 
            response.code(), errorMessage));
    }
    
    /**
     * Validates API key before making requests
     */
    public void validateApiKey() throws OpenAiException {
        String apiKey = openAiAttributes.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new OpenAiException("API key is required but not provided");
        }

        if (!apiKey.startsWith("sk-")) {
            logger.warn("API key doesn't start with 'sk-', this might be invalid");
        }
    }
    
    /**
     * Gets HTTP client statistics for monitoring
     */
    public String getHttpClientStats() {
        if (httpClient != null) {
            return String.format("HTTP Client - Connection Pool: %d idle, %d total", 
                httpClient.connectionPool().idleConnectionCount(),
                httpClient.connectionPool().connectionCount());
        }
        return "HTTP Client not initialized";
    }
}
