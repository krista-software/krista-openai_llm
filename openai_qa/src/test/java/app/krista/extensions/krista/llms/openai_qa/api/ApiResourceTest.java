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

package app.krista.extensions.krista.llms.openai_qa.api;

import app.krista.extensions.krista.llms.openai_qa.model.ModelType;
import app.krista.extensions.krista.llms.openai_qa.model.QARegistry;
import app.krista.extensions.krista.llms.openai_qa.store.QAStore;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApiResource class.
 * Uses mocked QAStore to test without Krista SDK dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApiResource Tests")
class ApiResourceTest {

    @Mock
    private QAStore mockQaStore;

    private ApiResource apiResource;

    @BeforeEach
    void setUp() {
        apiResource = new ApiResource(mockQaStore);
    }

    @Nested
    @DisplayName("registerQA Tests")
    class RegisterQATests {

        @Test
        @DisplayName("Should register QA with DEFAULT model type")
        void shouldRegisterQAWithDefaultModelType() {
            QARegistry registry = new QARegistry();
            when(mockQaStore.get()).thenReturn(registry);

            JsonObject payload = new JsonObject();
            payload.addProperty("callback", "http://callback.url");
            payload.addProperty("modelType", "DEFAULT");

            Map<String, Object> response = apiResource.registerQA(payload);

            assertNotNull(response);
            assertTrue((Boolean) response.get("success"));
            verify(mockQaStore).get();
            verify(mockQaStore).put(any(QARegistry.class));
        }

        @Test
        @DisplayName("Should register QA with FALLBACK model type")
        void shouldRegisterQAWithFallbackModelType() {
            QARegistry registry = new QARegistry();
            when(mockQaStore.get()).thenReturn(registry);

            JsonObject payload = new JsonObject();
            payload.addProperty("callback", "http://fallback.url");
            payload.addProperty("modelType", "FALLBACK");

            Map<String, Object> response = apiResource.registerQA(payload);

            assertNotNull(response);
            assertTrue((Boolean) response.get("success"));
            verify(mockQaStore).get();
            verify(mockQaStore).put(any(QARegistry.class));
        }

        @Test
        @DisplayName("Should add connection to existing registry")
        void shouldAddConnectionToExistingRegistry() {
            QARegistry registry = new QARegistry();
            registry.addConnection("http://existing.url", ModelType.DEFAULT);
            when(mockQaStore.get()).thenReturn(registry);

            JsonObject payload = new JsonObject();
            payload.addProperty("callback", "http://new.url");
            payload.addProperty("modelType", "DEFAULT");

            Map<String, Object> response = apiResource.registerQA(payload);

            assertNotNull(response);
            assertTrue((Boolean) response.get("success"));
            assertEquals(2, registry.getConnections().size());
        }

        @Test
        @DisplayName("Should throw exception for invalid model type")
        void shouldThrowExceptionForInvalidModelType() {
            // Note: No mock setup needed - exception thrown before store is accessed

            JsonObject payload = new JsonObject();
            payload.addProperty("callback", "http://callback.url");
            payload.addProperty("modelType", "INVALID_TYPE");

            assertThrows(IllegalArgumentException.class,
                () -> apiResource.registerQA(payload));
        }
    }
}

