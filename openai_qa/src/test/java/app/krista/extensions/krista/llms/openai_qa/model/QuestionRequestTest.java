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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for QuestionRequest model class with Builder pattern.
 */
@DisplayName("QuestionRequest Tests")
class QuestionRequestTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build request with all fields")
        void shouldBuildRequestWithAllFields() {
            QuestionRequest request = QuestionRequest.builder()
                    .sessionId("session-123")
                    .question("What is AI?")
                    .instructions("Be concise")
                    .build();

            assertEquals("session-123", request.getSessionId());
            assertEquals("What is AI?", request.getQuestion());
            assertEquals("Be concise", request.getInstructions());
        }

        @Test
        @DisplayName("Should build request without instructions")
        void shouldBuildRequestWithoutInstructions() {
            QuestionRequest request = QuestionRequest.builder()
                    .sessionId("session-123")
                    .question("What is AI?")
                    .build();

            assertEquals("session-123", request.getSessionId());
            assertEquals("What is AI?", request.getQuestion());
            assertNull(request.getInstructions());
        }

        @Test
        @DisplayName("Should throw exception when sessionId is null")
        void shouldThrowExceptionWhenSessionIdIsNull() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .question("What is AI?");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Session ID"));
        }

        @Test
        @DisplayName("Should throw exception when sessionId is empty")
        void shouldThrowExceptionWhenSessionIdIsEmpty() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .sessionId("")
                    .question("What is AI?");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Session ID"));
        }

        @Test
        @DisplayName("Should throw exception when sessionId is whitespace only")
        void shouldThrowExceptionWhenSessionIdIsWhitespace() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .sessionId("   ")
                    .question("What is AI?");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Session ID"));
        }

        @Test
        @DisplayName("Should throw exception when question is null")
        void shouldThrowExceptionWhenQuestionIsNull() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .sessionId("session-123");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Question"));
        }

        @Test
        @DisplayName("Should throw exception when question is empty")
        void shouldThrowExceptionWhenQuestionIsEmpty() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .sessionId("session-123")
                    .question("");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Question"));
        }

        @Test
        @DisplayName("Should throw exception when question is whitespace only")
        void shouldThrowExceptionWhenQuestionIsWhitespace() {
            QuestionRequest.Builder builder = QuestionRequest.builder()
                    .sessionId("session-123")
                    .question("   ");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("Question"));
        }
    }

    @Nested
    @DisplayName("Static Factory Method Tests")
    class StaticFactoryTests {

        @Test
        @DisplayName("builder() should return new Builder instance")
        void builderShouldReturnNewBuilderInstance() {
            QuestionRequest.Builder builder1 = QuestionRequest.builder();
            QuestionRequest.Builder builder2 = QuestionRequest.builder();

            assertNotSame(builder1, builder2);
        }
    }
}

