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
        
        String responseBody = sendRequest(OpenAiConfiguration.OPENAI_CHAT_COMPLETIONS_API_URL, payload);
        return responseParser.parseChatCompletionsResponse(responseBody);
    }
    
    /**
     * Sends request to Responses API
     */
    public String sendResponsesApiRequest(ObjectNode payload) throws OpenAiException {
        logger.info("Sending Responses API request");
        
        String responseBody = sendRequest(OpenAiConfiguration.OPENAI_RESPONSES_API_URL, payload);
        return responseParser.parseResponsesApiResponse(responseBody);
    }
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;

    /**
     * Sends HTTP POST request to OpenAI API with retry logic for transient failures.
     * Retries on: network errors (IOException), rate limits (HTTP 429), server errors (HTTP 5xx).
     * Does NOT retry on: client errors (4xx except 429) — these fail immediately.
     */
    private String sendRequest(String url, ObjectNode payload) throws OpenAiException {
        Request request = buildHttpRequest(url, payload);
        IOException lastNetworkError = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (Response response = httpClient.newCall(request).execute()) {
                int statusCode = response.code();

                if (response.isSuccessful()) {
                    return handleResponse(response);
                }

                // Rate limit or server error — retry
                if (statusCode == 429 || statusCode >= 500) {
                    logger.warn("Retryable error (HTTP {}) on attempt {}/{} to {}", statusCode, attempt, MAX_RETRIES, url);
                    if (attempt < MAX_RETRIES) {
                        pauseBeforeRetry(attempt);
                        continue;
                    }
                }

                // Client error (4xx except 429) — fail immediately, no retry
                return handleErrorResponse(response);

            } catch (IOException cause) {
                lastNetworkError = cause;
                logger.warn("Network error on attempt {}/{} to {}: {}", attempt, MAX_RETRIES, url, cause.getMessage());
                if (attempt < MAX_RETRIES) {
                    pauseBeforeRetry(attempt);
                }
            }
        }

        // All retries exhausted
        logger.error("All {} attempts failed for {}", MAX_RETRIES, url);
        throw new OpenAiException("Network error during API call after " + MAX_RETRIES + " attempts: "
                + (lastNetworkError != null ? lastNetworkError.getMessage() : "unknown error"), lastNetworkError);
    }

    /**
     * Pauses before retry with increasing delay.
     */
    private void pauseBeforeRetry(int attempt) {
        try {
            long delay = RETRY_DELAY_MS * attempt;
            logger.debug("Waiting {}ms before retry", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Retry delay interrupted");
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
     * Handles error responses from OpenAI API.
     * Detects well-known error codes and provides user-friendly messages at INFO level.
     * Unexpected errors are logged at ERROR level.
     */
    private String handleErrorResponse(Response response) throws OpenAiException, IOException {
        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
        String errorCode = responseParser.extractErrorCode(errorBody);
        String errorMessage = responseParser.extractErrorMessage(errorBody);
        int httpStatus = response.code();

        String userFriendlyMessage = buildUserFriendlyMessage(httpStatus, errorCode, errorMessage);

        if (isExpectedApiError(errorCode)) {
            logger.info("API request rejected (HTTP {}): {} [code={}]", httpStatus, userFriendlyMessage, errorCode);
        } else {
            logger.error("API call failed (HTTP {}): {} [code={}]", httpStatus, errorMessage, errorCode);
        }

        throw new OpenAiException(userFriendlyMessage);
    }

    /**
     * Returns true for error codes that represent expected, non-critical API rejections.
     * These are logged at INFO level since they indicate user/input issues, not system failures.
     */
    private boolean isExpectedApiError(String errorCode) {
        if (errorCode == null) return false;
        switch (errorCode) {
            case "context_length_exceeded":
            case "rate_limit_exceeded":
            case "invalid_api_key":
            case "model_not_found":
            case "invalid_image":
                return true;
            default:
                return false;
        }
    }

    /**
     * Builds a clear, actionable message for the end user based on the API error code.
     */
    private String buildUserFriendlyMessage(int httpStatus, String errorCode, String rawMessage) {
        if (errorCode == null) {
            return String.format("API call failed (HTTP %d): %s", httpStatus, rawMessage);
        }

        switch (errorCode) {
            case "context_length_exceeded":
                return "The uploaded file(s) exceed the context window of the selected model. "
                        + "Try using a model with a larger context (e.g., gpt-4.1 supports 1M tokens), "
                        + "upload a smaller file, or split the document into smaller parts.";
            case "rate_limit_exceeded":
                return "OpenAI rate limit reached. Please wait a moment and try again.";
            case "invalid_api_key":
                return "The configured OpenAI API key is invalid. "
                        + "Please update the API key in the extension settings.";
            case "model_not_found":
                return "The selected model is not available. "
                        + "Please choose a different model in the extension settings.";
            case "invalid_image":
                return "One or more uploaded images could not be processed. "
                        + "Please ensure images are valid JPG, PNG, GIF, or WebP files.";
            default:
                return String.format("API call failed (HTTP %d): %s", httpStatus, rawMessage);
        }
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
