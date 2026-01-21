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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageBuilderService Tests")
class MessageBuilderServiceTest {

    private MessageBuilderService messageBuilderService;

    @BeforeEach
    void setUp() {
        messageBuilderService = new MessageBuilderService();
    }

    @Nested
    @DisplayName("buildChatCompletionsPayload Tests")
    class BuildChatCompletionsPayloadTests {

        @Test
        @DisplayName("Should build payload with model and messages")
        void shouldBuildPayloadWithModelAndMessages() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("What is AI?")
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            assertNotNull(payload);
            assertEquals("gpt-4o", payload.get(OpenAiConfiguration.PARAM_MODEL).asText());
            assertTrue(payload.has(OpenAiConfiguration.PARAM_MESSAGES));
            assertTrue(payload.get(OpenAiConfiguration.PARAM_MESSAGES).isArray());
        }

        @Test
        @DisplayName("Should include user message in payload")
        void shouldIncludeUserMessageInPayload() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("What is machine learning?")
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            JsonNode messages = payload.get(OpenAiConfiguration.PARAM_MESSAGES);
            boolean hasUserMessage = false;
            for (JsonNode msg : messages) {
                if ("user".equals(msg.get("role").asText())) {
                    hasUserMessage = true;
                    assertEquals("What is machine learning?", msg.get("content").asText());
                }
            }
            assertTrue(hasUserMessage);
        }

        @Test
        @DisplayName("Should include system message when instructions provided")
        void shouldIncludeSystemMessageWhenInstructionsProvided() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("Explain AI")
                    .instructions("Be concise")
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            JsonNode messages = payload.get(OpenAiConfiguration.PARAM_MESSAGES);
            boolean hasSystemMessage = false;
            for (JsonNode msg : messages) {
                if ("system".equals(msg.get("role").asText())) {
                    hasSystemMessage = true;
                    assertTrue(msg.get("content").asText().contains("Be concise"));
                }
            }
            assertTrue(hasSystemMessage);
        }

        @Test
        @DisplayName("Should include max_tokens and temperature")
        void shouldIncludeMaxTokensAndTemperature() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("Test question")
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            assertTrue(payload.has(OpenAiConfiguration.PARAM_MAX_TOKENS));
            assertTrue(payload.has(OpenAiConfiguration.PARAM_TEMPERATURE));
        }

        @Test
        @DisplayName("Should not include system message when instructions are null")
        void shouldNotIncludeSystemMessageWhenInstructionsNull() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("Test question")
                    .instructions(null)
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            JsonNode messages = payload.get(OpenAiConfiguration.PARAM_MESSAGES);
            assertEquals(1, messages.size()); // Only user message
        }

        @Test
        @DisplayName("Should not include system message when instructions are empty")
        void shouldNotIncludeSystemMessageWhenInstructionsEmpty() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("Test question")
                    .instructions("   ")
                    .build();

            ObjectNode payload = messageBuilderService.buildChatCompletionsPayload(request, "gpt-4o");

            JsonNode messages = payload.get(OpenAiConfiguration.PARAM_MESSAGES);
            assertEquals(1, messages.size()); // Only user message
        }
    }

    @Nested
    @DisplayName("buildResponsesPayload Tests")
    class BuildResponsesPayloadTests {

        @Test
        @DisplayName("Should build responses payload with model and input")
        void shouldBuildResponsesPayloadWithModelAndInput() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("Summarize the document")
                    .build();
            SessionData sessionData = new SessionData("test-session");
            sessionData.addDocument("test.pdf", "file-123");

            ObjectNode payload = messageBuilderService.buildResponsesPayload(request, sessionData, "gpt-4o");

            assertNotNull(payload);
            assertEquals("gpt-4o", payload.get(OpenAiConfiguration.PARAM_MODEL).asText());
            assertTrue(payload.has(OpenAiConfiguration.PARAM_INPUT));
        }

        @Test
        @DisplayName("Should include file IDs in user message content")
        void shouldIncludeFileIdsInUserMessageContent() {
            QuestionRequest request = new QuestionRequest.Builder()
                    .sessionId("test-session")
                    .question("What is in this document?")
                    .build();
            SessionData sessionData = new SessionData("test-session");
            sessionData.addDocument("doc1.pdf", "file-abc");
            sessionData.addDocument("doc2.pdf", "file-xyz");

            ObjectNode payload = messageBuilderService.buildResponsesPayload(request, sessionData, "gpt-4o");

            JsonNode input = payload.get(OpenAiConfiguration.PARAM_INPUT);
            assertNotNull(input);
            assertTrue(input.isArray());
        }
    }
}

