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

package app.krista.extensions.krista.llms.openai_qa;

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenAiAttributes class.
 * Tests attribute management using the Map constructor (no Krista SDK dependency).
 */
@DisplayName("OpenAiAttributes Tests")
class OpenAiAttributesTest {

    @Nested
    @DisplayName("Constructor with Map Tests")
    class ConstructorWithMapTests {

        @Test
        @DisplayName("Should initialize with valid attributes")
        void shouldInitializeWithValidAttributes() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
            attributes.put(OpenAIConstants.MODEL, "gpt-4o");

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertEquals("sk-test-api-key", openAiAttributes.getApiKey());
            assertEquals(OpenAiConfiguration.DEFAULT_MODEL, openAiAttributes.getModel());
        }

        @Test
        @DisplayName("Should use empty string for missing API key")
        void shouldUseEmptyStringForMissingApiKey() {
            Map<String, Object> attributes = new HashMap<>();

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertEquals("", openAiAttributes.getApiKey());
        }

        @Test
        @DisplayName("Should use default model")
        void shouldUseDefaultModel() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-key");

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertEquals(OpenAiConfiguration.DEFAULT_MODEL, openAiAttributes.getModel());
        }

        @Test
        @DisplayName("Should handle null API key value")
        void shouldHandleNullApiKeyValue() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, null);

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertEquals("", openAiAttributes.getApiKey());
        }
    }

    @Nested
    @DisplayName("Update Method Tests")
    class UpdateMethodTests {

        @Test
        @DisplayName("Should update API key")
        void shouldUpdateApiKey() {
            Map<String, Object> initialAttributes = new HashMap<>();
            initialAttributes.put(OpenAIConstants.API_KEY, "initial-key");

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(initialAttributes);
            assertEquals("initial-key", openAiAttributes.getApiKey());

            Map<String, Object> newAttributes = new HashMap<>();
            newAttributes.put(OpenAIConstants.API_KEY, "updated-key");
            openAiAttributes.update(newAttributes);

            assertEquals("updated-key", openAiAttributes.getApiKey());
        }

        @Test
        @DisplayName("Should handle empty attributes on update")
        void shouldHandleEmptyAttributesOnUpdate() {
            Map<String, Object> initialAttributes = new HashMap<>();
            initialAttributes.put(OpenAIConstants.API_KEY, "initial-key");

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(initialAttributes);

            Map<String, Object> emptyAttributes = new HashMap<>();
            openAiAttributes.update(emptyAttributes);

            assertEquals("", openAiAttributes.getApiKey());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getApiKey should return API key")
        void getApiKeyShouldReturnApiKey() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "test-api-key-123");

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertEquals("test-api-key-123", openAiAttributes.getApiKey());
        }

        @Test
        @DisplayName("getModel should return default model")
        void getModelShouldReturnDefaultModel() {
            Map<String, Object> attributes = new HashMap<>();

            OpenAiAttributes openAiAttributes = new OpenAiAttributes(attributes);

            assertNotNull(openAiAttributes.getModel());
            assertEquals(OpenAiConfiguration.DEFAULT_MODEL, openAiAttributes.getModel());
        }
    }
}

