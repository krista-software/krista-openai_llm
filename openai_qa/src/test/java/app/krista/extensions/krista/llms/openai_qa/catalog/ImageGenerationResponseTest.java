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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImageGenerationResponse DTO class.
 */
@DisplayName("ImageGenerationResponse Tests")
class ImageGenerationResponseTest {

    @Test
    @DisplayName("Should create with created timestamp and data")
    void shouldCreateWithCreatedTimestampAndData() {
        List<ImageMetaData> images = Arrays.asList(
                new ImageMetaData("http://example.com/image1.png"),
                new ImageMetaData("http://example.com/image2.png")
        );
        ImageGenerationResponse response = new ImageGenerationResponse(1234567890L, images);

        assertEquals(1234567890L, response.getCreated());
        assertEquals(2, response.getData().size());
    }

    @Test
    @DisplayName("Should handle empty data list")
    void shouldHandleEmptyDataList() {
        ImageGenerationResponse response = new ImageGenerationResponse(1234567890L, Collections.emptyList());

        assertEquals(1234567890L, response.getCreated());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    @DisplayName("Should handle null data list")
    void shouldHandleNullDataList() {
        ImageGenerationResponse response = new ImageGenerationResponse(1234567890L, null);

        assertEquals(1234567890L, response.getCreated());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Should handle single image")
    void shouldHandleSingleImage() {
        List<ImageMetaData> images = Collections.singletonList(
                new ImageMetaData("http://example.com/single.png")
        );
        ImageGenerationResponse response = new ImageGenerationResponse(System.currentTimeMillis(), images);

        assertEquals(1, response.getData().size());
        assertEquals("http://example.com/single.png", response.getData().get(0).getUrl());
    }
}

