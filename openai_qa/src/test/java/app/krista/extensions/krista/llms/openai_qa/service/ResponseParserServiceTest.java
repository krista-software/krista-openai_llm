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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResponseParserService Tests")
class ResponseParserServiceTest {

    private ResponseParserService responseParserService;

    @BeforeEach
    void setUp() {
        responseParserService = new ResponseParserService();
    }

    @Nested
    @DisplayName("parseChatCompletionsResponse Tests")
    class ParseChatCompletionsResponseTests {

        @Test
        @DisplayName("Should parse valid Chat Completions response")
        void shouldParseValidChatCompletionsResponse() throws OpenAiException {
            String validResponse = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "Hello, how can I help you?"
                            }
                        }
                    ]
                }
                """;
            
            String result = responseParserService.parseChatCompletionsResponse(validResponse);
            
            assertEquals("Hello, how can I help you?", result);
        }

        @Test
        @DisplayName("Should return default message when no content found")
        void shouldReturnDefaultMessageWhenNoContentFound() throws OpenAiException {
            String emptyChoicesResponse = """
                {
                    "choices": []
                }
                """;
            
            String result = responseParserService.parseChatCompletionsResponse(emptyChoicesResponse);
            
            assertEquals("No content found in Chat Completions response.", result);
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON")
        void shouldThrowExceptionForInvalidJson() {
            String invalidJson = "not valid json";
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> responseParserService.parseChatCompletionsResponse(invalidJson));
            
            assertTrue(exception.getMessage().contains("Failed to parse"));
        }

        @Test
        @DisplayName("Should trim whitespace from content")
        void shouldTrimWhitespaceFromContent() throws OpenAiException {
            String responseWithWhitespace = """
                {
                    "choices": [
                        {
                            "message": {
                                "content": "  trimmed content  "
                            }
                        }
                    ]
                }
                """;
            
            String result = responseParserService.parseChatCompletionsResponse(responseWithWhitespace);
            
            assertEquals("trimmed content", result);
        }
    }

    @Nested
    @DisplayName("parseResponsesApiResponse Tests")
    class ParseResponsesApiResponseTests {

        @Test
        @DisplayName("Should parse valid Responses API response")
        void shouldParseValidResponsesApiResponse() throws OpenAiException {
            String validResponse = """
                {
                    "output": [
                        {
                            "content": [
                                {
                                    "type": "output_text",
                                    "text": "This is the response text."
                                }
                            ]
                        }
                    ]
                }
                """;
            
            String result = responseParserService.parseResponsesApiResponse(validResponse);
            
            assertEquals("This is the response text.", result);
        }

        @Test
        @DisplayName("Should return default message when no output found")
        void shouldReturnDefaultMessageWhenNoOutputFound() throws OpenAiException {
            String emptyOutputResponse = """
                {
                    "output": []
                }
                """;
            
            String result = responseParserService.parseResponsesApiResponse(emptyOutputResponse);
            
            assertEquals("No content found in Responses API response.", result);
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON")
        void shouldThrowExceptionForInvalidJson() {
            String invalidJson = "invalid json";
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> responseParserService.parseResponsesApiResponse(invalidJson));
            
            assertTrue(exception.getMessage().contains("Failed to parse"));
        }

        @Test
        @DisplayName("Should concatenate multiple output texts")
        void shouldConcatenateMultipleOutputTexts() throws OpenAiException {
            String multipleOutputsResponse = """
                {
                    "output": [
                        {
                            "content": [
                                {"type": "output_text", "text": "First part."},
                                {"type": "output_text", "text": "Second part."}
                            ]
                        }
                    ]
                }
                """;
            
            String result = responseParserService.parseResponsesApiResponse(multipleOutputsResponse);
            
            assertTrue(result.contains("First part."));
            assertTrue(result.contains("Second part."));
        }
    }

    @Nested
    @DisplayName("validateResponseBody Tests")
    class ValidateResponseBodyTests {

        @Test
        @DisplayName("Should throw exception for null response body")
        void shouldThrowExceptionForNullResponseBody() {
            OpenAiException exception = assertThrows(OpenAiException.class,
                () -> responseParserService.validateResponseBody(null));

            assertEquals("Response body is null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty response body")
        void shouldThrowExceptionForEmptyResponseBody() {
            OpenAiException exception = assertThrows(OpenAiException.class,
                () -> responseParserService.validateResponseBody(""));

            assertEquals("Response body is null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for whitespace-only response body")
        void shouldThrowExceptionForWhitespaceResponseBody() {
            OpenAiException exception = assertThrows(OpenAiException.class,
                () -> responseParserService.validateResponseBody("   "));

            assertEquals("Response body is null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should not throw for valid JSON object")
        void shouldNotThrowForValidJsonObject() {
            assertDoesNotThrow(() -> responseParserService.validateResponseBody("{\"key\": \"value\"}"));
        }

        @Test
        @DisplayName("Should not throw for valid JSON array")
        void shouldNotThrowForValidJsonArray() {
            assertDoesNotThrow(() -> responseParserService.validateResponseBody("[1, 2, 3]"));
        }
    }

    @Nested
    @DisplayName("extractErrorMessage Tests")
    class ExtractErrorMessageTests {

        @Test
        @DisplayName("Should extract error message from error response")
        void shouldExtractErrorMessageFromErrorResponse() {
            String errorResponse = """
                {
                    "error": {
                        "message": "Invalid API key",
                        "type": "invalid_request_error",
                        "code": "invalid_api_key"
                    }
                }
                """;

            String result = responseParserService.extractErrorMessage(errorResponse);

            assertTrue(result.contains("Invalid API key"));
            assertTrue(result.contains("invalid_request_error"));
            assertTrue(result.contains("invalid_api_key"));
        }

        @Test
        @DisplayName("Should return raw response when no error node")
        void shouldReturnRawResponseWhenNoErrorNode() {
            String noErrorResponse = "{\"status\": \"ok\"}";

            String result = responseParserService.extractErrorMessage(noErrorResponse);

            assertEquals(noErrorResponse, result);
        }

        @Test
        @DisplayName("Should return raw response for invalid JSON")
        void shouldReturnRawResponseForInvalidJson() {
            String invalidJson = "not json at all";

            String result = responseParserService.extractErrorMessage(invalidJson);

            assertEquals(invalidJson, result);
        }

        @Test
        @DisplayName("Should handle error with only message")
        void shouldHandleErrorWithOnlyMessage() {
            String errorResponse = """
                {
                    "error": {
                        "message": "Something went wrong"
                    }
                }
                """;

            String result = responseParserService.extractErrorMessage(errorResponse);

            assertTrue(result.contains("Something went wrong"));
        }
    }
}

