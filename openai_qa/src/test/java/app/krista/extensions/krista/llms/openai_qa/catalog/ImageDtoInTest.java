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

package app.krista.extensions.krista.llms.openai_qa.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImageDtoIn DTO class.
 */
@DisplayName("ImageDtoIn Tests")
class ImageDtoInTest {

    @Test
    @DisplayName("Should create with all parameters")
    void shouldCreateWithAllParameters() {
        ImageDtoIn dto = new ImageDtoIn("dall-e-3", "A sunset over mountains", 1, "hd", "1024x1024");

        assertEquals("dall-e-3", dto.getModel());
        assertEquals("A sunset over mountains", dto.getPrompt());
        assertEquals(1, dto.getN());
        assertEquals("hd", dto.getQuality());
        assertEquals("1024x1024", dto.getSize());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        ImageDtoIn dto = new ImageDtoIn(null, null, null, null, null);

        assertNull(dto.getModel());
        assertNull(dto.getPrompt());
        assertNull(dto.getN());
        assertNull(dto.getQuality());
        assertNull(dto.getSize());
    }

    @Test
    @DisplayName("Should create with dall-e-2 model")
    void shouldCreateWithDallE2Model() {
        ImageDtoIn dto = new ImageDtoIn("dall-e-2", "A rose painting", 3, "standard", "512x512");

        assertEquals("dall-e-2", dto.getModel());
        assertEquals(3, dto.getN());
        assertEquals("standard", dto.getQuality());
        assertEquals("512x512", dto.getSize());
    }

    @Test
    @DisplayName("Should handle different sizes")
    void shouldHandleDifferentSizes() {
        ImageDtoIn dto256 = new ImageDtoIn("dall-e-2", "test", 1, "standard", "256x256");
        ImageDtoIn dto512 = new ImageDtoIn("dall-e-2", "test", 1, "standard", "512x512");
        ImageDtoIn dto1024 = new ImageDtoIn("dall-e-3", "test", 1, "hd", "1024x1024");

        assertEquals("256x256", dto256.getSize());
        assertEquals("512x512", dto512.getSize());
        assertEquals("1024x1024", dto1024.getSize());
    }
}

