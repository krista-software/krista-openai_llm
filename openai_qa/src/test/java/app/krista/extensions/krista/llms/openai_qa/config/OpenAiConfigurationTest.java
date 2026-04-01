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

package app.krista.extensions.krista.llms.openai_qa.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OpenAiConfiguration class.
 */
@DisplayName("OpenAiConfiguration Tests")
class OpenAiConfigurationTest {

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("Should have correct API URLs")
        void shouldHaveCorrectApiUrls() {
            assertEquals("https://api.openai.com/v1/files", OpenAiConfiguration.OPENAI_FILES_API_URL);
            assertEquals("https://api.openai.com/v1/responses", OpenAiConfiguration.OPENAI_RESPONSES_API_URL);
            assertEquals("https://api.openai.com/v1/chat/completions", OpenAiConfiguration.OPENAI_CHAT_COMPLETIONS_API_URL);
        }

        @Test
        @DisplayName("Should have correct file processing constants")
        void shouldHaveCorrectFileProcessingConstants() {
            assertEquals("application/pdf", OpenAiConfiguration.PDF_MIME_TYPE);
            assertEquals("pdf", OpenAiConfiguration.PDF_FILE_TYPE);
            assertEquals(".pdf", OpenAiConfiguration.PDF_EXTENSION);
            assertEquals(512 * 1024 * 1024, OpenAiConfiguration.MAX_FILE_SIZE_BYTES);
        }

        @Test
        @DisplayName("Should have correct token limits")
        void shouldHaveCorrectTokenLimits() {
            assertEquals(16384, OpenAiConfiguration.MAX_TOKENS_GPT_4O);
            assertEquals(32768, OpenAiConfiguration.MAX_TOKENS_GPT_4_1);
            assertEquals(4096, OpenAiConfiguration.MAX_TOKENS_GPT_35);
            assertEquals(16384, OpenAiConfiguration.DEFAULT_MAX_TOKENS);
        }

        @Test
        @DisplayName("Should have correct HTTP settings")
        void shouldHaveCorrectHttpSettings() {
            assertEquals(5, OpenAiConfiguration.CONNECT_TIMEOUT_SECONDS);
            assertEquals(20, OpenAiConfiguration.READ_TIMEOUT_SECONDS);
        }

        @Test
        @DisplayName("Should have default model set")
        void shouldHaveDefaultModelSet() {
            assertEquals("gpt-4o", OpenAiConfiguration.DEFAULT_MODEL);
        }
    }

    @Nested
    @DisplayName("isValidModel Tests")
    class IsValidModelTests {

        @Test
        @DisplayName("Should return true for valid models")
        void shouldReturnTrueForValidModels() {
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4o"));
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4o-mini"));
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4-turbo"));
            assertTrue(OpenAiConfiguration.isValidModel("gpt-3.5-turbo"));
        }

        @Test
        @DisplayName("Should return true for GPT-4.1 family models")
        void shouldReturnTrueForGpt41FamilyModels() {
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4.1"));
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4.1-mini"));
            assertTrue(OpenAiConfiguration.isValidModel("gpt-4.1-nano"));
            assertTrue(OpenAiConfiguration.isValidModel("chatgpt-4o-latest"));
        }

        @Test
        @DisplayName("Should return false for null model")
        void shouldReturnFalseForNullModel() {
            assertFalse(OpenAiConfiguration.isValidModel(null));
        }

        @Test
        @DisplayName("Should return false for empty model")
        void shouldReturnFalseForEmptyModel() {
            assertFalse(OpenAiConfiguration.isValidModel(""));
            assertFalse(OpenAiConfiguration.isValidModel("   "));
        }

        @Test
        @DisplayName("Should return false for invalid model")
        void shouldReturnFalseForInvalidModel() {
            assertFalse(OpenAiConfiguration.isValidModel("invalid-model"));
            assertFalse(OpenAiConfiguration.isValidModel("gpt-5"));
        }

        @Test
        @DisplayName("Should handle model with whitespace")
        void shouldHandleModelWithWhitespace() {
            assertTrue(OpenAiConfiguration.isValidModel("  gpt-4o  "));
        }
    }

    @Nested
    @DisplayName("getValidModel Tests")
    class GetValidModelTests {

        @Test
        @DisplayName("Should return model if valid")
        void shouldReturnModelIfValid() {
            assertEquals("gpt-4o", OpenAiConfiguration.getValidModel("gpt-4o"));
            assertEquals("gpt-3.5-turbo", OpenAiConfiguration.getValidModel("gpt-3.5-turbo"));
        }

        @Test
        @DisplayName("Should return default model for null")
        void shouldReturnDefaultModelForNull() {
            assertEquals(OpenAiConfiguration.DEFAULT_MODEL, OpenAiConfiguration.getValidModel(null));
        }

        @Test
        @DisplayName("Should return default model for invalid")
        void shouldReturnDefaultModelForInvalid() {
            assertEquals(OpenAiConfiguration.DEFAULT_MODEL, OpenAiConfiguration.getValidModel("invalid"));
        }

        @Test
        @DisplayName("Should trim valid model")
        void shouldTrimValidModel() {
            assertEquals("gpt-4o", OpenAiConfiguration.getValidModel("  gpt-4o  "));
        }
    }

    @Nested
    @DisplayName("getMaxTokensForModel Tests")
    class GetMaxTokensForModelTests {

        @Test
        @DisplayName("Should return GPT-4o tokens for gpt-4o models")
        void shouldReturnGpt4oTokensForGpt4oModels() {
            assertEquals(OpenAiConfiguration.MAX_TOKENS_GPT_4O, OpenAiConfiguration.getMaxTokensForModel("gpt-4o"));
            assertEquals(OpenAiConfiguration.MAX_TOKENS_GPT_4O, OpenAiConfiguration.getMaxTokensForModel("gpt-4-turbo"));
        }

        @Test
        @DisplayName("Should return GPT-3.5 tokens for gpt-3.5 models")
        void shouldReturnGpt35TokensForGpt35Models() {
            assertEquals(OpenAiConfiguration.MAX_TOKENS_GPT_35, OpenAiConfiguration.getMaxTokensForModel("gpt-3.5-turbo"));
        }

        @Test
        @DisplayName("Should return default tokens for null model")
        void shouldReturnDefaultTokensForNullModel() {
            assertEquals(OpenAiConfiguration.DEFAULT_MAX_TOKENS, OpenAiConfiguration.getMaxTokensForModel(null));
        }

        @Test
        @DisplayName("Should return GPT-4.1 tokens for gpt-4.1 models")
        void shouldReturnGpt41TokensForGpt41Models() {
            assertEquals(OpenAiConfiguration.MAX_TOKENS_GPT_4_1, OpenAiConfiguration.getMaxTokensForModel("gpt-4.1"));
            assertEquals(OpenAiConfiguration.MAX_TOKENS_GPT_4_1, OpenAiConfiguration.getMaxTokensForModel("gpt-4-1-preview"));
        }
    }
}
