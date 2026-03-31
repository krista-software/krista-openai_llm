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

import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Service responsible for parsing OpenAI API responses
 * Follows Single Responsibility Principle - only handles response parsing
 */
public class ResponseParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseParserService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * Parses Chat Completions API response
     */
    public String parseChatCompletionsResponse(String responseBody) throws OpenAiException {
        logger.debug("Parsing Chat Completions response");
        
        try {
            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);
            JsonNode choicesArray = responseJson.path("choices");
            
            if (choicesArray.isArray() && !choicesArray.isEmpty()) {
                JsonNode firstChoice = choicesArray.get(0);
                JsonNode message = firstChoice.path("message");
                String content = message.path("content").asText();
                
                if (content != null && !content.trim().isEmpty()) {
                    logger.debug("Successfully parsed Chat Completions response, content length: {}", content.length());
                    return content.trim();
                }
            }
            
            logger.warn("No content found in Chat Completions response");
            return "No content found in Chat Completions response.";
            
        } catch (IOException cause) {
            logger.error("Failed to parse Chat Completions response", cause);
            throw new OpenAiException("Failed to parse Chat Completions response: " + cause.getMessage(), cause);
        }
    }
    
    /**
     * Parses Responses API response
     */
    public String parseResponsesApiResponse(String responseBody) throws OpenAiException {
        logger.debug("Parsing Responses API response");
        
        try {
            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);
            JsonNode outputArray = responseJson.path("output");
            
            if (outputArray.isArray() && !outputArray.isEmpty()) {
                StringBuilder result = new StringBuilder();

                for (JsonNode outputItem : outputArray) {
                    JsonNode contentArray = outputItem.path("content");
                    if (contentArray.isArray()) {
                        for (JsonNode contentItem : contentArray) {
                            String type = contentItem.path("type").asText();
                            if ("output_text".equals(type)) {
                                String text = contentItem.path("text").asText();
                                result.append(text).append("\n");
                            }
                        }
                    }
                }
                
                String finalResult = result.toString().trim();
                if (!finalResult.isEmpty()) {
                    logger.debug("Successfully parsed Responses API response, content length: {}", finalResult.length());
                    return finalResult;
                }
            }
            
            logger.warn("No content found in Responses API response");
            return "No content found in Responses API response.";
            
        } catch (IOException cause) {
            logger.error("Failed to parse Responses API response", cause);
            throw new OpenAiException("Failed to parse Responses API response: " + cause.getMessage(), cause);
        }
    }
    
    /**
     * Validates response body before parsing
     */
    public void validateResponseBody(String responseBody) throws OpenAiException {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new OpenAiException("Response body is null or empty");
        }
        
        // Check if response looks like JSON
        String trimmed = responseBody.trim();
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            logger.warn("Response body doesn't appear to be JSON: {}", 
                       trimmed.length() > 100 ? trimmed.substring(0, 100) + "..." : trimmed);
        }
    }
    
    /**
     * Extracts the error code from an OpenAI error response (e.g., "context_length_exceeded").
     * Returns null if the code cannot be parsed.
     */
    public String extractErrorCode(String responseBody) {
        try {
            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);
            JsonNode codeNode = responseJson.path("error").path("code");
            if (!codeNode.isMissingNode() && !codeNode.isNull()) {
                String code = codeNode.asText();
                return code.isEmpty() ? null : code;
            }
        } catch (IOException cause) {
            logger.debug("Could not parse error code from response: {}", cause.getMessage());
        }
        return null;
    }

    /**
     * Extracts error message from error response
     */
    public String extractErrorMessage(String responseBody) {
        try {
            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);
            JsonNode errorNode = responseJson.path("error");
            
            if (!errorNode.isMissingNode()) {
                String message = errorNode.path("message").asText();
                String type = errorNode.path("type").asText();
                String code = errorNode.path("code").asText();
                
                StringBuilder errorMsg = new StringBuilder();
                if (!message.isEmpty()) {
                    errorMsg.append(message);
                }
                if (!type.isEmpty()) {
                    errorMsg.append(" (Type: ").append(type).append(")");
                }
                if (!code.isEmpty()) {
                    errorMsg.append(" (Code: ").append(code).append(")");
                }
                
                return errorMsg.toString();
            }
            
        } catch (IOException cause) {
            logger.debug("Could not parse error response as JSON: {}", cause.getMessage());
        }
        
        // Return raw response if we can't parse it
        return responseBody;
    }
}
