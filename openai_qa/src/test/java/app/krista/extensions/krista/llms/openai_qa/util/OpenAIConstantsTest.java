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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenAIConstants class.
 * Tests constant values, immutability, and naming conventions.
 */
class OpenAIConstantsTest {

    @Test
    @DisplayName("Should have correct API key constant")
    void testApiKeyConstant() {
        assertEquals("API Key", OpenAIConstants.API_KEY);
    }

    @Test
    @DisplayName("Should have correct model constant")
    void testModelConstant() {
        assertEquals("Model Name", OpenAIConstants.MODEL);
    }

    @Test
    @DisplayName("Should have correct GPT model constants")
    void testGptModelConstants() {
        assertEquals("ChatGPT 3.5", OpenAIConstants.GPT_35);
        assertEquals("ChatGPT 4", OpenAIConstants.GPT_4);
        assertEquals("ChatGPT 4.1 Nano", OpenAIConstants.GPT_4_1_NANO);
        assertEquals("ChatGPT 4.1 Mini", OpenAIConstants.GPT_4_1_MINI);
        assertEquals("ChatGPT 4.1", OpenAIConstants.GPT_4_1);
        assertEquals("ChatGPT 5.4", OpenAIConstants.GPT_5_4);
        assertEquals("ChatGPT 5.4 Mini", OpenAIConstants.GPT_5_4_MINI);
        assertEquals("ChatGPT 5.4 Nano", OpenAIConstants.GPT_5_4_NANO);
    }

    @Test
    @DisplayName("Should have correct base model name constant")
    void testBaseModelNameConstant() {
        assertEquals("OpenAI ChatGPT ", OpenAIConstants.BASE_MODEL_NAME);
    }

    @Test
    @DisplayName("Should have correct version constants")
    void testVersionConstants() {
        assertEquals("4", OpenAIConstants.VERSION_4);
        assertEquals("3.5", OpenAIConstants.VERSION_35);
        assertEquals("4.1 Mini", OpenAIConstants.VERSION_4_1_MINI);
        assertEquals("4.1 Nano", OpenAIConstants.VERSION_4_1_NANO);
        assertEquals("4.1", OpenAIConstants.VERSION_4_1);
        assertEquals("5.4", OpenAIConstants.VERSION_5_4);
        assertEquals("5.4 Mini", OpenAIConstants.VERSION_5_4_MINI);
        assertEquals("5.4 Nano", OpenAIConstants.VERSION_5_4_NANO);
    }

    @Test
    @DisplayName("Should have all constants as public static final")
    void testConstantsArePublicStaticFinal() {
        Field[] fields = OpenAIConstants.class.getDeclaredFields();

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            assertTrue(Modifier.isPublic(modifiers),
                      "Field " + field.getName() + " should be public");
            assertTrue(Modifier.isStatic(modifiers),
                      "Field " + field.getName() + " should be static");
            assertTrue(Modifier.isFinal(modifiers),
                      "Field " + field.getName() + " should be final");
        }
    }

    @Test
    @DisplayName("Should have all string constants non-null and non-empty")
    void testStringConstantsNonNullAndNonEmpty() {
        Field[] fields = OpenAIConstants.class.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == String.class) {
                try {
                    String value = (String) field.get(null);
                    assertNotNull(value, "String constant " + field.getName() + " should not be null");
                    assertFalse(value.trim().isEmpty(),
                               "String constant " + field.getName() + " should not be empty");
                } catch (IllegalAccessException e) {
                    fail("Could not access field " + field.getName());
                }
            }
        }
    }

    @Test
    @DisplayName("Should have consistent model naming convention")
    void testModelNamingConvention() {
        List<String> models = Arrays.asList(
            OpenAIConstants.GPT_35,
            OpenAIConstants.GPT_4,
            OpenAIConstants.GPT_4_1_NANO,
            OpenAIConstants.GPT_4_1_MINI,
            OpenAIConstants.GPT_4_1,
            OpenAIConstants.GPT_5_4,
            OpenAIConstants.GPT_5_4_MINI,
            OpenAIConstants.GPT_5_4_NANO
        );

        for (String model : models) {
            assertTrue(model.startsWith("ChatGPT"),
                      "Model " + model + " should start with 'ChatGPT'");
            assertTrue(model.contains(" "),
                       "Model " + model + " should contain spaces for readability");
        }
    }

    @Test
    @DisplayName("Should have consistent version naming convention")
    void testVersionNamingConvention() {
        List<String> versions = Arrays.asList(
            OpenAIConstants.VERSION_4,
            OpenAIConstants.VERSION_35,
            OpenAIConstants.VERSION_4_1_MINI,
            OpenAIConstants.VERSION_4_1_NANO,
            OpenAIConstants.VERSION_4_1,
            OpenAIConstants.VERSION_5_4,
            OpenAIConstants.VERSION_5_4_MINI,
            OpenAIConstants.VERSION_5_4_NANO
        );

        for (String version : versions) {
            assertNotNull(version, "Version should not be null");
            assertFalse(version.trim().isEmpty(), "Version should not be empty");
        }
    }

    @Test
    @DisplayName("Should have base model name with trailing space")
    void testBaseModelNameFormat() {
        assertTrue(OpenAIConstants.BASE_MODEL_NAME.endsWith(" "),
                  "Base model name should end with space for concatenation");
        assertTrue(OpenAIConstants.BASE_MODEL_NAME.contains("OpenAI"),
                  "Base model name should contain 'OpenAI'");
        assertTrue(OpenAIConstants.BASE_MODEL_NAME.contains("ChatGPT"),
                  "Base model name should contain 'ChatGPT'");
    }


}
