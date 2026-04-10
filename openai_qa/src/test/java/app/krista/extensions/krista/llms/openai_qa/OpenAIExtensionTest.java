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

import app.krista.extension.impl.anno.*;
import app.krista.extension.util.InvokerAttributeProvider;
import app.krista.extensions.krista.llms.openai_qa.impl.QueryImpl;
import app.krista.extensions.krista.llms.openai_qa.impl.ChatGPTResponse;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Comprehensive unit tests for OpenAIExtension class.
 * Tests extension configuration, validation, connection testing, lifecycle methods,
 * dependency injection, and error handling scenarios.
 */
@ExtendWith(MockitoExtension.class)
class OpenAIExtensionTest {

    @Mock
    private QueryImpl mockQueryImpl;

    @Mock
    private InvokerAttributeProvider<String> mockApiKeyProvider;

    @Mock
    private InvokerAttributeProvider<String> mockModelNameProvider;

    private OpenAIExtension extension;

    @BeforeEach
    void setUp() {
        extension = new OpenAIExtension(mockQueryImpl);

        // Inject mocked providers using reflection for testConnection() method tests
        try {
            Field apiKeyField = OpenAIExtension.class.getDeclaredField("apiKey");
            apiKeyField.setAccessible(true);
            apiKeyField.set(extension, mockApiKeyProvider);

            Field modelNameField = OpenAIExtension.class.getDeclaredField("modelName");
            modelNameField.setAccessible(true);
            modelNameField.set(extension, mockModelNameProvider);
        } catch (Exception e) {
            fail("Failed to inject mocked providers: " + e.getMessage());
        }
    }

    /**
     * Helper method to create a mock ChatGPTResponse for testing.
     */
    private ChatGPTResponse createMockResponse() {
        ChatGPTResponse response = new ChatGPTResponse();
        response.id = "test-id";
        response.object = "chat.completion";
        response.created = System.currentTimeMillis();
        response.model = "gpt-4";

        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        message.content = "Test response";
        choice.message = message;
        choice.finish_reason = "stop";
        choice.index = 0;

        response.choices = List.of(choice);
        return response;
    }

    @Test
    @DisplayName("Should validate attributes successfully with valid API key and model")
    void testValidateAttributes_ValidConfiguration_Success() throws Exception {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

        // Mock the execute method to simulate successful connection
        when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

        // When/Then - Should not throw exception
        assertDoesNotThrow(() -> extension.validateAttributes(attributes));
    }

    @Test
    @DisplayName("Should throw exception when API key is missing")
    void testValidateAttributes_MissingApiKey_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(API_KEY_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when API key is null")
    void testValidateAttributes_NullApiKey_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, null);
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(API_KEY_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when API key is not a string")
    void testValidateAttributes_NonStringApiKey_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, 12345); // Integer instead of String
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(API_KEY_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when model is missing")
    void testValidateAttributes_MissingModel_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(MODEL_NOT_SELECTED, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when model is null")
    void testValidateAttributes_NullModel_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
        attributes.put(OpenAIConstants.MODEL, null);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(MODEL_NOT_SELECTED, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when model is not a string")
    void testValidateAttributes_NonStringModel_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
        attributes.put(OpenAIConstants.MODEL, 12345); // Integer instead of String

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(MODEL_NOT_SELECTED, exception.getMessage());
    }

    @Test
    @DisplayName("Should validate all supported model types")
    void testValidateAttributes_AllSupportedModels_Success() throws Exception {
        // Given
        String[] supportedModels = {
            OpenAIConstants.GPT_35,
            OpenAIConstants.GPT_4,
            OpenAIConstants.GPT_4_1_NANO,
            OpenAIConstants.GPT_4_1_MINI,
            OpenAIConstants.GPT_4_1,
            OpenAIConstants.GPT_5_4,
            OpenAIConstants.GPT_5_4_MINI,
            OpenAIConstants.GPT_5_4_NANO
        };

        // Mock the execute method to simulate successful connection
        when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

        // When/Then
        for (String model : supportedModels) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
            attributes.put(OpenAIConstants.MODEL, model);

            assertDoesNotThrow(
                () -> extension.validateAttributes(attributes),
                "Should accept model: " + model
            );
        }
    }

    @Test
    @DisplayName("Should handle empty string API key")
    void testValidateAttributes_EmptyApiKey_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "");
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

        // When/Then - Empty string should fail in testConnection
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(API_KEY_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle empty string model")
    void testValidateAttributes_EmptyModel_ThrowsException() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
        attributes.put(OpenAIConstants.MODEL, "");

        // When/Then - Empty string should fail in testConnection
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes)
        );
        assertEquals(MODEL_NOT_SELECTED, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle attributes with extra fields")
    void testValidateAttributes_ExtraFields_Success() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);
        attributes.put("extra_field", "extra_value");
        attributes.put("another_field", 123);

        // When/Then - Should ignore extra fields
        assertDoesNotThrow(() -> extension.validateAttributes(attributes));
    }

    // Note: testConnection method is private and cannot be tested directly
    // These tests would require reflection or making the method package-private

    @Test
    @DisplayName("Should have extension class properly configured")
    void testExtensionClass_ProperlyConfigured() {
        // Given
        Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

        // Then - Basic class structure tests
        assertNotNull(extensionClass);
        assertEquals("OpenAIExtension", extensionClass.getSimpleName());
        assertEquals("app.krista.extensions.krista.llms.openai_qa", extensionClass.getPackageName());
    }

    @Test
    @DisplayName("Should have constructor with QueryImpl parameter")
    void testExtensionConstructor_HasCorrectParameters() throws Exception {
        // Given/When
        var constructor = OpenAIExtension.class.getConstructor(QueryImpl.class);

        // Then
        assertNotNull(constructor);
        assertEquals(1, constructor.getParameterCount());
        assertEquals(QueryImpl.class, constructor.getParameterTypes()[0]);
    }

    @Test
    @DisplayName("Should have validateAttributes method")
    void testExtensionMethods_ValidateAttributesExists() throws Exception {
        // Given/When
        var method = OpenAIExtension.class.getMethod("validateAttributes", Map.class);

        // Then
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("Should have testConnection method")
    void testExtensionMethods_TestConnectionExists() throws Exception {
        // Given/When
        var method = OpenAIExtension.class.getMethod("testConnection");

        // Then
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
        assertEquals(0, method.getParameterCount());
    }

    @Test
    @DisplayName("Should handle various API key formats")
    void testValidateAttributes_VariousApiKeyFormats_Success() throws Exception {
        // Given
        String[] apiKeyFormats = {
            "sk-test123",
            "sk-1234567890abcdef1234567890abcdef12345678",
            "sk-proj-1234567890abcdef",
            "test-api-key-format"
        };

        // Mock the execute method to simulate successful connection
        when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

        // When/Then
        for (String apiKey : apiKeyFormats) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, apiKey);
            attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

            assertDoesNotThrow(
                () -> extension.validateAttributes(attributes),
                "Should accept API key format: " + apiKey
            );
        }
    }

    // ========================================
    // Test Connection Method Tests
    // ========================================

    @Nested
    @DisplayName("Test Connection Method Tests")
    class TestConnectionMethodTests {

        @Test
        @DisplayName("Should test connection successfully with valid credentials")
        void testConnection_ValidCredentials_Success() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("sk-test-api-key-12345");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);
            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

            // When/Then - Should not throw exception
            assertDoesNotThrow(() -> extension.testConnection());

            // Verify the execute method was called with correct parameters
            verify(mockQueryImpl).execute(any(), eq("sk-test-api-key-12345"), eq(OpenAIConstants.GPT_4), eq(false));
        }

        @Test
        @DisplayName("Should handle connection failure with IOException")
        void testConnection_IOException_ThrowsIllegalArgumentException() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("sk-invalid-key");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);
            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean()))
                .thenThrow(new IOException("Connection failed"));

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(CONNECTION_TEST_FAILED));
            assertTrue(exception.getMessage().contains("Connection failed"));
        }

        @Test
        @DisplayName("Should handle null API key from provider")
        void testConnection_NullApiKey_ThrowsIllegalArgumentException() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn(null);
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(API_KEY_MISSING));
        }

        @Test
        @DisplayName("Should handle empty API key from provider")
        void testConnection_EmptyApiKey_ThrowsIllegalArgumentException() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(API_KEY_MISSING));
        }

        @Test
        @DisplayName("Should handle null model name from provider")
        void testConnection_NullModelName_ThrowsIllegalArgumentException() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("sk-test-api-key-12345");
            when(mockModelNameProvider.get()).thenReturn(null);

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(MODEL_NOT_SELECTED));
        }

        @Test
        @DisplayName("Should handle empty model name from provider")
        void testConnection_EmptyModelName_ThrowsIllegalArgumentException() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("sk-test-api-key-12345");
            when(mockModelNameProvider.get()).thenReturn("");

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(MODEL_NOT_SELECTED));
        }

        @Test
        @DisplayName("Should test connection with all supported models")
        void testConnection_AllSupportedModels_Success() throws Exception {
            // Given
            String[] supportedModels = {
                OpenAIConstants.GPT_35,
                OpenAIConstants.GPT_4,
                OpenAIConstants.GPT_4_1_NANO,
                OpenAIConstants.GPT_4_1_MINI,
                OpenAIConstants.GPT_4_1,
                OpenAIConstants.GPT_5_4,
                OpenAIConstants.GPT_5_4_MINI,
                OpenAIConstants.GPT_5_4_NANO
            };

            when(mockApiKeyProvider.get()).thenReturn("sk-test-api-key-12345");
            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

            // When/Then
            for (String model : supportedModels) {
                when(mockModelNameProvider.get()).thenReturn(model);

                assertDoesNotThrow(
                    () -> extension.testConnection(),
                    "Should test connection successfully with model: " + model
                );
            }
        }

        @Test
        @DisplayName("Should handle various IOException messages")
        void testConnection_VariousIOExceptions_ProperErrorMessages() throws Exception {
            // Given
            String[] errorMessages = {
                "Network timeout",
                "Invalid API key",
                "Service unavailable",
                "Rate limit exceeded"
            };

            when(mockApiKeyProvider.get()).thenReturn("sk-test-key");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);

            // When/Then
            for (String errorMessage : errorMessages) {
                // Reset the mock for each iteration
                reset(mockQueryImpl);
                when(mockQueryImpl.execute(any(), any(), any(), anyBoolean()))
                    .thenThrow(new IOException(errorMessage));

                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> extension.testConnection()
                );

                assertTrue(exception.getMessage().contains(CONNECTION_TEST_FAILED));
                assertTrue(exception.getMessage().contains(errorMessage));
            }
        }
    }

    // ========================================
    // Custom Tabs Method Tests
    // ========================================

    @Nested
    @DisplayName("Custom Tabs Method Tests")
    class CustomTabsMethodTests {

        @Test
        @DisplayName("Should return correct custom tabs configuration")
        void customTabs_ReturnsCorrectConfiguration() {
            // When
            Map<String, String> tabs = extension.customTabs();

            // Then
            assertNotNull(tabs);
            assertEquals(1, tabs.size());
            assertTrue(tabs.containsKey("Documentation"));
            assertEquals("static/docs", tabs.get("Documentation"));
        }

        @Test
        @DisplayName("Should return immutable map")
        void customTabs_ReturnsImmutableMap() {
            // When
            Map<String, String> tabs = extension.customTabs();

            // Then
            assertThrows(UnsupportedOperationException.class, () -> {
                tabs.put("NewTab", "new/path");
            });
        }

        @Test
        @DisplayName("Should return consistent results on multiple calls")
        void customTabs_ConsistentResults() {
            // When
            Map<String, String> tabs1 = extension.customTabs();
            Map<String, String> tabs2 = extension.customTabs();

            // Then
            assertEquals(tabs1, tabs2);
            assertEquals(tabs1.size(), tabs2.size());
            assertEquals(tabs1.get("Documentation"), tabs2.get("Documentation"));
        }
    }

    // ========================================
    // Annotation and Configuration Tests
    // ========================================

    @Nested
    @DisplayName("Annotation and Configuration Tests")
    class AnnotationConfigurationTests {

        @Test
        @DisplayName("Should have runtime-available annotations")
        void testRuntimeAnnotations() {
            // Given
            Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

            // When - Check available runtime annotations
            java.lang.annotation.Annotation[] annotations = extensionClass.getAnnotations();

            // Then - Verify that Field annotations are present (these are runtime-retained)
            boolean hasFieldTextAnnotation = false;
            boolean hasFieldPickOneAnnotation = false;

            for (java.lang.annotation.Annotation ann : annotations) {
                String annotationType = ann.annotationType().getName();
                if (annotationType.equals("app.krista.extension.impl.anno.Field$Text")) {
                    hasFieldTextAnnotation = true;
                }
                if (annotationType.equals("app.krista.extension.impl.anno.Field$PickOne")) {
                    hasFieldPickOneAnnotation = true;
                }
            }

            assertTrue(hasFieldTextAnnotation, "Field.Text annotation should be present at runtime");
            assertTrue(hasFieldPickOneAnnotation, "Field.PickOne annotation should be present at runtime");
        }

        @Test
        @DisplayName("Should have correct Field.Text annotation for API key")
        void testApiKeyFieldAnnotation() {
            // Given
            Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

            // When
            app.krista.extension.impl.anno.Field.Text[] annotations =
                extensionClass.getAnnotationsByType(app.krista.extension.impl.anno.Field.Text.class);

            // Then
            assertNotNull(annotations);
            assertEquals(1, annotations.length);

            app.krista.extension.impl.anno.Field.Text apiKeyAnnotation = annotations[0];
            assertEquals(OpenAIConstants.API_KEY, apiKeyAnnotation.value());
            assertTrue(apiKeyAnnotation.isSecured());
        }

        @Test
        @DisplayName("Should have correct Field.PickOne annotation for model")
        void testModelFieldAnnotation() {
            // Given
            Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

            // When
            app.krista.extension.impl.anno.Field.PickOne annotation =
                extensionClass.getAnnotation(app.krista.extension.impl.anno.Field.PickOne.class);

            // Then
            assertNotNull(annotation);
            assertEquals(OpenAIConstants.MODEL, annotation.value());

            String[] expectedValues = {
                OpenAIConstants.GPT_35,
                OpenAIConstants.GPT_4,
                OpenAIConstants.GPT_4_1_NANO,
                OpenAIConstants.GPT_4_1_MINI,
                OpenAIConstants.GPT_4_1,
                OpenAIConstants.GPT_5_4,
                OpenAIConstants.GPT_5_4_MINI,
                OpenAIConstants.GPT_5_4_NANO
            };

            assertArrayEquals(expectedValues, annotation.values());
        }

        @Test
        @DisplayName("Should have required lifecycle methods")
        void testLifecycleMethods() throws Exception {
            // Given
            Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

            // When/Then - Verify validateAttributes method exists
            Method validateAttributesMethod = extensionClass.getMethod("validateAttributes", Map.class);
            assertNotNull(validateAttributesMethod);
            assertEquals(void.class, validateAttributesMethod.getReturnType());

            // When/Then - Verify testConnection method exists
            Method testConnectionMethod = extensionClass.getMethod("testConnection");
            assertNotNull(testConnectionMethod);
            assertEquals(void.class, testConnectionMethod.getReturnType());

            // When/Then - Verify customTabs method exists
            Method customTabsMethod = extensionClass.getMethod("customTabs");
            assertNotNull(customTabsMethod);
            assertEquals(Map.class, customTabsMethod.getReturnType());
        }

        @Test
        @DisplayName("Should have proper class structure")
        void testClassStructure() {
            // Given
            Class<OpenAIExtension> extensionClass = OpenAIExtension.class;

            // Then - Verify class is public
            assertTrue(java.lang.reflect.Modifier.isPublic(extensionClass.getModifiers()));

            // Verify class has proper constructor
            boolean hasQueryImplConstructor = false;
            for (var constructor : extensionClass.getConstructors()) {
                if (constructor.getParameterCount() == 1 &&
                    constructor.getParameterTypes()[0].equals(QueryImpl.class)) {
                    hasQueryImplConstructor = true;
                    break;
                }
            }
            assertTrue(hasQueryImplConstructor, "Should have constructor that takes QueryImpl parameter");
        }
    }

    // ========================================
    // Dependency Injection Tests
    // ========================================

    @Nested
    @DisplayName("Dependency Injection Tests")
    class DependencyInjectionTests {

        @Test
        @DisplayName("Should have correct @Inject annotation on constructor")
        void testConstructorInjectAnnotation() throws Exception {
            // Given
            var constructor = OpenAIExtension.class.getConstructor(QueryImpl.class);

            // When
            var injectAnnotation = constructor.getAnnotation(javax.inject.Inject.class);

            // Then
            assertNotNull(injectAnnotation);
        }

        @Test
        @DisplayName("Should have correct @Inject and @Named annotations on apiKey field")
        void testApiKeyFieldAnnotations() throws Exception {
            // Given
            Field apiKeyField = OpenAIExtension.class.getDeclaredField("apiKey");

            // When
            var injectAnnotation = apiKeyField.getAnnotation(javax.inject.Inject.class);
            var namedAnnotation = apiKeyField.getAnnotation(javax.inject.Named.class);

            // Then
            assertNotNull(injectAnnotation);
            assertNotNull(namedAnnotation);
            assertEquals(OpenAIConstants.API_KEY, namedAnnotation.value());
        }

        @Test
        @DisplayName("Should have correct @Inject and @Named annotations on modelName field")
        void testModelNameFieldAnnotations() throws Exception {
            // Given
            Field modelNameField = OpenAIExtension.class.getDeclaredField("modelName");

            // When
            var injectAnnotation = modelNameField.getAnnotation(javax.inject.Inject.class);
            var namedAnnotation = modelNameField.getAnnotation(javax.inject.Named.class);

            // Then
            assertNotNull(injectAnnotation);
            assertNotNull(namedAnnotation);
            assertEquals(OpenAIConstants.MODEL, namedAnnotation.value());
        }

        @Test
        @DisplayName("Should have correct field types for dependency injection")
        void testFieldTypes() throws Exception {
            // Given/When
            Field apiKeyField = OpenAIExtension.class.getDeclaredField("apiKey");
            Field modelNameField = OpenAIExtension.class.getDeclaredField("modelName");
            Field queryField = OpenAIExtension.class.getDeclaredField("query");

            // Then
            assertEquals(InvokerAttributeProvider.class, apiKeyField.getType());
            assertEquals(InvokerAttributeProvider.class, modelNameField.getType());
            assertEquals(QueryImpl.class, queryField.getType());
        }

        @Test
        @DisplayName("Should properly initialize with QueryImpl dependency")
        void testConstructorInitialization() {
            // Given
            QueryImpl testQueryImpl = mock(QueryImpl.class);

            // When
            OpenAIExtension testExtension = new OpenAIExtension(testQueryImpl);

            // Then
            assertNotNull(testExtension);
            // Verify the query field is set (using reflection)
            try {
                Field queryField = OpenAIExtension.class.getDeclaredField("query");
                queryField.setAccessible(true);
                assertEquals(testQueryImpl, queryField.get(testExtension));
            } catch (Exception e) {
                fail("Failed to verify query field initialization: " + e.getMessage());
            }
        }
    }

    // ========================================
    // Edge Cases and Error Handling Tests
    // ========================================

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesErrorHandlingTests {

        @Test
        @DisplayName("Should handle connection test with very long error message")
        void testConnection_VeryLongErrorMessage_HandledCorrectly() throws Exception {
            // Given
            String longErrorMessage = "A".repeat(1000) + " - Very long error message";
            when(mockApiKeyProvider.get()).thenReturn("sk-test-key");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);
            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean()))
                .thenThrow(new IOException(longErrorMessage));

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(CONNECTION_TEST_FAILED));
            assertTrue(exception.getMessage().contains(longErrorMessage));
        }

        @Test
        @DisplayName("Should handle connection test with null error message")
        void testConnection_NullErrorMessage_HandledCorrectly() throws Exception {
            // Given
            when(mockApiKeyProvider.get()).thenReturn("sk-test-key");
            when(mockModelNameProvider.get()).thenReturn(OpenAIConstants.GPT_4);
            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean()))
                .thenThrow(new IOException((String) null));

            // When/Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> extension.testConnection()
            );

            assertTrue(exception.getMessage().contains(CONNECTION_TEST_FAILED));
        }

        @Test
        @DisplayName("Should handle validateAttributes with concurrent modification")
        void validateAttributes_ConcurrentModification_HandledCorrectly() throws Exception {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
            attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

            // When - Modify map during validation (simulate concurrent access)
            assertDoesNotThrow(() -> {
                extension.validateAttributes(attributes);
                attributes.put("new_key", "new_value"); // This should not affect validation
            });
        }

        @Test
        @DisplayName("Should handle validateAttributes with very large attribute map")
        void validateAttributes_LargeAttributeMap_HandledCorrectly() throws Exception {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key-12345");
            attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

            // Add many extra attributes
            for (int i = 0; i < 1000; i++) {
                attributes.put("extra_key_" + i, "extra_value_" + i);
            }

            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

            // When/Then - Should handle large map efficiently
            assertDoesNotThrow(() -> extension.validateAttributes(attributes));
        }

        @Test
        @DisplayName("Should handle special characters in API key and model")
        void validateAttributes_SpecialCharacters_HandledCorrectly() throws Exception {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(OpenAIConstants.API_KEY, "sk-test-key-with-special-chars-!@#$%^&*()");
            attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);

            when(mockQueryImpl.execute(any(), any(), any(), anyBoolean())).thenReturn(createMockResponse());

            // When/Then
            assertDoesNotThrow(() -> extension.validateAttributes(attributes));
        }
    }

    // ========================================
    // Logger and Logging Tests
    // ========================================

    @Nested
    @DisplayName("Logger and Logging Tests")
    class LoggerLoggingTests {

        @Test
        @DisplayName("Should have static logger field")
        void testLoggerField() throws Exception {
            // Given/When
            Field loggerField = OpenAIExtension.class.getDeclaredField("log");

            // Then
            assertNotNull(loggerField);
            assertEquals(Logger.class, loggerField.getType());
            assertTrue(java.lang.reflect.Modifier.isStatic(loggerField.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isFinal(loggerField.getModifiers()));
        }

        @Test
        @DisplayName("Should have logger properly initialized")
        void testLoggerInitialization() throws Exception {
            // Given
            Field loggerField = OpenAIExtension.class.getDeclaredField("log");
            loggerField.setAccessible(true);

            // When
            Logger logger = (Logger) loggerField.get(null);

            // Then
            assertNotNull(logger);
            assertEquals("app.krista.extensions.krista.llms.openai_qa.OpenAIExtension", logger.getName());
        }
    }
}
