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

package app.krista.extensions.krista.llms.openai_qa.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for public exception classes in the OpenAI extension.
 * Note: DocumentUploadException, SessionNotFoundException, FileValidationException,
 * and ApiCallException are package-private and tested within the same package.
 */
@DisplayName("Exception Tests")
class ExceptionTest {

    @Nested
    @DisplayName("ConfigurationException Tests")
    class ConfigurationExceptionTests {

        @Test
        @DisplayName("Should create exception with message")
        void shouldCreateExceptionWithMessage() {
            ConfigurationException exception = new ConfigurationException("Config error");

            assertEquals("Config error", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should be instance of Exception")
        void shouldBeInstanceOfException() {
            ConfigurationException exception = new ConfigurationException("Error");

            assertTrue(exception instanceof Exception);
        }
    }

    @Nested
    @DisplayName("OpenAiException Tests")
    class OpenAiExceptionTests {

        @Test
        @DisplayName("Should create exception with message only")
        void shouldCreateExceptionWithMessageOnly() {
            OpenAiException exception = new OpenAiException("OpenAI error");

            assertEquals("OpenAI error", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            RuntimeException cause = new RuntimeException("Root cause");
            OpenAiException exception = new OpenAiException("OpenAI error", cause);

            assertEquals("OpenAI error", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should be instance of Exception")
        void shouldBeInstanceOfException() {
            OpenAiException exception = new OpenAiException("Error");

            assertTrue(exception instanceof Exception);
        }
    }

    // Package-private exception tests - these are accessible since test is in same package
    @Nested
    @DisplayName("DocumentUploadException Tests")
    class DocumentUploadExceptionTests {

        @Test
        @DisplayName("Should create exception with file name, status, and message")
        void shouldCreateExceptionWithAllParameters() {
            DocumentUploadException exception = new DocumentUploadException("test.pdf", 400, "Bad request");

            assertEquals("test.pdf", exception.getFileName());
            assertEquals(400, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains("test.pdf"));
            assertTrue(exception.getMessage().contains("400"));
            assertTrue(exception.getMessage().contains("Bad request"));
        }

        @Test
        @DisplayName("Should extend OpenAiException")
        void shouldExtendOpenAiException() {
            DocumentUploadException exception = new DocumentUploadException("file.pdf", 500, "Error");

            assertTrue(exception instanceof OpenAiException);
        }
    }

    @Nested
    @DisplayName("SessionNotFoundException Tests")
    class SessionNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with session ID")
        void shouldCreateExceptionWithSessionId() {
            SessionNotFoundException exception = new SessionNotFoundException("session-123");

            assertEquals("session-123", exception.getSessionId());
            assertTrue(exception.getMessage().contains("session-123"));
        }

        @Test
        @DisplayName("Should extend OpenAiException")
        void shouldExtendOpenAiException() {
            SessionNotFoundException exception = new SessionNotFoundException("session-456");

            assertTrue(exception instanceof OpenAiException);
        }
    }

    @Nested
    @DisplayName("FileValidationException Tests")
    class FileValidationExceptionTests {

        @Test
        @DisplayName("Should create exception with file name and message")
        void shouldCreateExceptionWithFileNameAndMessage() {
            FileValidationException exception = new FileValidationException("invalid.txt", "Invalid format");

            assertEquals("invalid.txt", exception.getFileName());
            assertTrue(exception.getMessage().contains("invalid.txt"));
            assertTrue(exception.getMessage().contains("Invalid format"));
        }

        @Test
        @DisplayName("Should extend OpenAiException")
        void shouldExtendOpenAiException() {
            FileValidationException exception = new FileValidationException("file.txt", "Error");

            assertTrue(exception instanceof OpenAiException);
        }
    }

    @Nested
    @DisplayName("ApiCallException Tests")
    class ApiCallExceptionTests {

        @Test
        @DisplayName("Should create exception with endpoint, status, and message")
        void shouldCreateExceptionWithAllParameters() {
            ApiCallException exception = new ApiCallException("/v1/chat", 503, "Service unavailable");

            assertEquals("/v1/chat", exception.getApiEndpoint());
            assertEquals(503, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains("/v1/chat"));
            assertTrue(exception.getMessage().contains("503"));
            assertTrue(exception.getMessage().contains("Service unavailable"));
        }

        @Test
        @DisplayName("Should extend OpenAiException")
        void shouldExtendOpenAiException() {
            ApiCallException exception = new ApiCallException("/api", 500, "Error");

            assertTrue(exception instanceof OpenAiException);
        }
    }
}

