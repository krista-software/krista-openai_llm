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
 * Tests for ImageMetaData DTO class.
 */
@DisplayName("ImageMetaData Tests")
class ImageMetaDataTest {

    @Test
    @DisplayName("Should create with URL")
    void shouldCreateWithUrl() {
        ImageMetaData metadata = new ImageMetaData("http://example.com/image.png");

        assertEquals("http://example.com/image.png", metadata.getUrl());
    }

    @Test
    @DisplayName("Should handle null URL")
    void shouldHandleNullUrl() {
        ImageMetaData metadata = new ImageMetaData(null);

        assertNull(metadata.getUrl());
    }

    @Test
    @DisplayName("Should handle empty URL")
    void shouldHandleEmptyUrl() {
        ImageMetaData metadata = new ImageMetaData("");

        assertEquals("", metadata.getUrl());
    }

    @Test
    @DisplayName("Should handle HTTPS URL")
    void shouldHandleHttpsUrl() {
        ImageMetaData metadata = new ImageMetaData("https://secure.example.com/image.jpg");

        assertEquals("https://secure.example.com/image.jpg", metadata.getUrl());
    }

    @Test
    @DisplayName("Should handle URL with query parameters")
    void shouldHandleUrlWithQueryParameters() {
        String urlWithParams = "https://example.com/image.png?token=abc123&size=large";
        ImageMetaData metadata = new ImageMetaData(urlWithParams);

        assertEquals(urlWithParams, metadata.getUrl());
    }
}

