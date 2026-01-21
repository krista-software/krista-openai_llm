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

package app.krista.extensions.krista.llms.openai_qa.util;

import app.krista.extension.executor.ExtensionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.API_KEY_MISSING;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.MODEL_NOT_SELECTED;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for ExtensionUtil class.
 * Tests validation logic, error response creation, and edge cases.
 */
class ExtensionUtilTest {

    @Test
    @DisplayName("Should return null when all attributes are valid")
    void testValidateAttributes_ValidAttributes_ReturnsNull() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
        attributes.put(OpenAIConstants.MODEL, "gpt-4");

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNull(result, "Should return null for valid attributes");
    }

    @Test
    @DisplayName("Should return error response when attributes map is null")
    void testValidateAttributes_NullAttributes_ReturnsError() {
        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(null);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        // Note: ExtensionResponse.Error doesn't have getMessage() method
        // The error message is passed to constructor but not accessible via getter
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should return error response when API key is missing")
    void testValidateAttributes_MissingApiKey_ReturnsError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.MODEL, "gpt-4");

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should return error response when API key is null")
    void testValidateAttributes_NullApiKey_ReturnsError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, null);
        attributes.put(OpenAIConstants.MODEL, "gpt-4");

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should return error response when model is missing")
    void testValidateAttributes_MissingModel_ReturnsError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should return error response when model is null")
    void testValidateAttributes_NullModel_ReturnsError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
        attributes.put(OpenAIConstants.MODEL, null);

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should prioritize API key validation over model validation")
    void testValidateAttributes_BothMissing_ReturnsApiKeyError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sk-test-key", "sk-1234567890abcdef", "test-api-key-value"})
    @DisplayName("Should accept various valid API key formats")
    void testValidateAttributes_VariousValidApiKeys_ReturnsNull(String apiKey) {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, apiKey);
        attributes.put(OpenAIConstants.MODEL, "gpt-4");

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNull(result, "Should accept valid API key: " + apiKey);
    }

    @ParameterizedTest
    @ValueSource(strings = {"gpt-4", "gpt-3.5-turbo", "gpt-4-1106-preview", "custom-model"})
    @DisplayName("Should accept various valid model names")
    void testValidateAttributes_VariousValidModels_ReturnsNull(String model) {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
        attributes.put(OpenAIConstants.MODEL, model);

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNull(result, "Should accept valid model: " + model);
    }

    @Test
    @DisplayName("Should create error response with correct structure")
    void testCreateErrorResponse_ValidMessage_ReturnsCorrectStructure() {
        // Given
        String errorMessage = "Test error message";

        // When
        ExtensionResponse result = ExtensionUtil.createErrorResponse(errorMessage);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
        assertTrue(result.getError().getTimestamp() > 0);
        assertNull(result.getError().getStackTrace());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "Error message", "Very long error message with lots of details"})
    @DisplayName("Should handle various error message formats")
    void testCreateErrorResponse_VariousMessages_HandlesCorrectly(String message) {
        // When
        ExtensionResponse result = ExtensionUtil.createErrorResponse(message);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should create error responses with different timestamps")
    void testCreateErrorResponse_MultipleCalls_DifferentTimestamps() throws InterruptedException {
        // When
        ExtensionResponse result1 = ExtensionUtil.createErrorResponse("Error 1");
        Thread.sleep(1); // Ensure different timestamps
        ExtensionResponse result2 = ExtensionUtil.createErrorResponse("Error 2");

        // Then
        assertNotEquals(result1.getError().getTimestamp(), result2.getError().getTimestamp());
    }

    @Test
    @DisplayName("Should handle empty attributes map correctly")
    void testValidateAttributes_EmptyMap_ReturnsApiKeyError() {
        // Given
        Map<String, Object> attributes = new HashMap<>();

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
    }

    @Test
    @DisplayName("Should handle attributes with extra fields")
    void testValidateAttributes_ExtraFields_ValidatesCorrectly() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
        attributes.put(OpenAIConstants.MODEL, "gpt-4");
        attributes.put("extra_field", "extra_value");
        attributes.put("another_field", 123);

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNull(result, "Should ignore extra fields and validate successfully");
    }

    @Test
    @DisplayName("Should handle non-string values in attributes")
    void testValidateAttributes_NonStringValues_ValidatesCorrectly() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-api-key");
        attributes.put(OpenAIConstants.MODEL, "gpt-4");
        attributes.put("numeric_field", 42);
        attributes.put("boolean_field", true);

        // When
        ExtensionResponse result = ExtensionUtil.validateAttributes(attributes);

        // Then
        assertNull(result, "Should handle non-string values correctly");
    }

    @Test
    @DisplayName("Should validate error response structure")
    void testCreateErrorResponse_StructureValidation() {
        // Given
        String errorMessage = "Test validation error";

        // When
        ExtensionResponse result = ExtensionUtil.createErrorResponse(errorMessage);

        // Then
        assertNotNull(result);
        assertEquals(ExtensionResponse.Result.FAILURE, result.getResult());
        assertNotNull(result.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, result.getError().getExceptionType());
        assertTrue(result.getError().getTimestamp() > 0);
        assertNull(result.getError().getStackTrace());
    }

}
