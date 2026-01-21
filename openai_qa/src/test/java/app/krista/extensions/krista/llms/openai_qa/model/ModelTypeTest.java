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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ModelType enum.
 */
@DisplayName("ModelType Tests")
class ModelTypeTest {

    @Test
    @DisplayName("Should have DEFAULT value")
    void shouldHaveDefaultValue() {
        assertNotNull(ModelType.DEFAULT);
        assertEquals("DEFAULT", ModelType.DEFAULT.name());
    }

    @Test
    @DisplayName("Should have FALLBACK value")
    void shouldHaveFallbackValue() {
        assertNotNull(ModelType.FALLBACK);
        assertEquals("FALLBACK", ModelType.FALLBACK.name());
    }

    @Test
    @DisplayName("Should have exactly 2 values")
    void shouldHaveExactlyTwoValues() {
        assertEquals(2, ModelType.values().length);
    }

    @Test
    @DisplayName("valueOf should return correct enum for DEFAULT")
    void valueOfShouldReturnCorrectEnumForDefault() {
        assertEquals(ModelType.DEFAULT, ModelType.valueOf("DEFAULT"));
    }

    @Test
    @DisplayName("valueOf should return correct enum for FALLBACK")
    void valueOfShouldReturnCorrectEnumForFallback() {
        assertEquals(ModelType.FALLBACK, ModelType.valueOf("FALLBACK"));
    }

    @Test
    @DisplayName("valueOf should throw exception for invalid value")
    void valueOfShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> ModelType.valueOf("INVALID"));
    }
}

