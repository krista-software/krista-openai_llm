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

package app.krista.extensions.krista.llms.openai_qa.service.impl;

import app.krista.extensions.krista.llms.openai_qa.impl.QueryImpl;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QueryImpl class, specifically testing the getAIModelDisplayName() method.
 *
 * This test class verifies that the AI model display name returns exactly what the user
 * selected from the @Field.PickOne annotation for all supported model types and edge cases.
 */
class QueryImplTest {

    private QueryImpl queryImpl;

    @BeforeEach
    void setUp() {
        queryImpl = new QueryImpl();
    }

    @Test
    @DisplayName("Should return 'ChatGPT 4' for GPT_4 model")
    void testGetAIModelDisplayName_GPT4() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4);

        // Assert
        assertEquals("ChatGPT 4", result);
    }

    @Test
    @DisplayName("Should return 'ChatGPT 3.5' for GPT_35 model")
    void testGetAIModelDisplayName_GPT35() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_35);

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should return 'ChatGPT 5.4' for GPT_5_4 model")
    void testGetAIModelDisplayName_GPT54() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4);

        // Assert
        assertEquals("ChatGPT 5.4", result);
    }

    @Test
    @DisplayName("Should return 'ChatGPT 5.4 Mini' for GPT_5_4_MINI model")
    void testGetAIModelDisplayName_GPT54Mini() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4_MINI);

        // Assert
        assertEquals("ChatGPT 5.4 Mini", result);
    }

    @Test
    @DisplayName("Should return 'ChatGPT 5.4 Nano' for GPT_5_4_NANO model")
    void testGetAIModelDisplayName_GPT54Nano() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4_NANO);

        // Assert
        assertEquals("ChatGPT 5.4 Nano", result);
    }

    @Test
    @DisplayName("Should return 'ChatGPT 4.1 Mini' for GPT_4_1_MINI model")
    void testGetAIModelDisplayName_GPT41Mini() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4_1_MINI);

        // Assert
        assertEquals("ChatGPT 4.1 Mini", result);
    }

    @Test
    @DisplayName("Should return default 'ChatGPT 3.5' for unknown model")
    void testGetAIModelDisplayName_UnknownModel() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName("Unknown Model");

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should handle null model gracefully and return default")
    void testGetAIModelDisplayName_NullModel() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(null);

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should handle blank model gracefully and return default")
    void testGetAIModelDisplayName_BlankModel() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName("");

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should handle whitespace-only model gracefully and return default")
    void testGetAIModelDisplayName_WhitespaceModel() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName("   ");

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should handle model with different casing")
    void testGetAIModelDisplayName_DifferentCasing() {
        // Arrange & Act - Model names are case-sensitive, so this should return default
        String result = queryImpl.getAIModelDisplayName("chatgpt 4");

        // Assert
        assertEquals("ChatGPT 3.5", result);
    }

    @Test
    @DisplayName("Should verify all model constants match expected display names")
    void testGetAIModelDisplayName_AllModelsHaveCorrectDisplayNames() {
        // Test GPT_4
        assertEquals("ChatGPT 4",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4));

        // Test GPT_35
        assertEquals("ChatGPT 3.5",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_35));

        // Test GPT_5_4
        assertEquals("ChatGPT 5.4",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4));

        // Test GPT_5_4_MINI
        assertEquals("ChatGPT 5.4 Mini",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4_MINI));

        // Test GPT_5_4_NANO
        assertEquals("ChatGPT 5.4 Nano",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_5_4_NANO));

        // Test GPT_4_1_MINI
        assertEquals("ChatGPT 4.1 Mini",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4_1_MINI));

        // Test GPT_4_1_NANO
        assertEquals("ChatGPT 4.1 Nano",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4_1_NANO));

        // Test GPT_4_1
        assertEquals("ChatGPT 4.1",
                queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4_1));
    }

    @Test
    @DisplayName("Should return exact constant value for GPT_4")
    void testGetAIModelDisplayName_ReturnsExactConstant() {
        // Arrange & Act
        String result = queryImpl.getAIModelDisplayName(OpenAIConstants.GPT_4);

        // Assert - Verify it returns the exact constant value
        assertEquals(OpenAIConstants.GPT_4, result);
    }

    @Test
    @DisplayName("Should not throw exception for any input")
    void testGetAIModelDisplayName_NoExceptionsThrown() {
        // Test various edge cases to ensure no exceptions are thrown
        String[] testCases = {
                null,
                "",
                "   ",
                "invalid",
                "ChatGPT 5",
                "GPT-4",
                OpenAIConstants.GPT_4,
                OpenAIConstants.GPT_35,
                OpenAIConstants.GPT_5_4,
                OpenAIConstants.GPT_5_4_MINI,
                OpenAIConstants.GPT_5_4_NANO,
                OpenAIConstants.GPT_4_1_MINI,
                OpenAIConstants.GPT_4_1_NANO,
                OpenAIConstants.GPT_4_1
        };

        for (String testCase : testCases) {
            assertDoesNotThrow(() -> queryImpl.getAIModelDisplayName(testCase),
                    "Should not throw exception for input: " + testCase);
        }
    }

    @Test
    @DisplayName("Should always return non-null and non-empty string")
    void testGetAIModelDisplayName_AlwaysReturnsValidString() {
        // Test various inputs
        String[] testCases = {null, "", "   ", "invalid", OpenAIConstants.GPT_4};

        for (String testCase : testCases) {
            String result = queryImpl.getAIModelDisplayName(testCase);

            assertNotNull(result, "Result should never be null for input: " + testCase);
            assertFalse(result.isEmpty(), "Result should never be empty for input: " + testCase);
            assertTrue(result.startsWith("ChatGPT "),
                    "Result should always start with 'ChatGPT ' for input: " + testCase);
        }
    }
}
