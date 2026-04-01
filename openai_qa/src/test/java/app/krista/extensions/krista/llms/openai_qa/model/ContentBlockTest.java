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

@DisplayName("ContentBlock Tests")
class ContentBlockTest {

    @Test
    @DisplayName("Should create image block with correct properties")
    void shouldCreateImageBlock() {
        ContentBlock block = ContentBlock.imageBlock("data:image/png;base64,abc123", "screenshot.png");

        assertEquals(ContentBlock.BlockType.INPUT_IMAGE, block.getType());
        assertEquals("data:image/png;base64,abc123", block.getDataUrl());
        assertEquals("screenshot.png", block.getFileName());
        assertNull(block.getFileId());
    }

    @Test
    @DisplayName("Should create document block with correct properties")
    void shouldCreateDocumentBlock() {
        ContentBlock block = ContentBlock.documentBlock("file-abc123", "report.pdf");

        assertEquals(ContentBlock.BlockType.INPUT_FILE, block.getType());
        assertEquals("file-abc123", block.getFileId());
        assertEquals("report.pdf", block.getFileName());
        assertNull(block.getDataUrl());
    }

    @Test
    @DisplayName("Image and document blocks should have different types")
    void shouldHaveDifferentTypes() {
        ContentBlock image = ContentBlock.imageBlock("data:image/jpeg;base64,xyz", "photo.jpg");
        ContentBlock doc = ContentBlock.documentBlock("file-xyz", "doc.pdf");

        assertNotEquals(image.getType(), doc.getType());
    }
}
