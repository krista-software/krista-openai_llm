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

/**
 * Represents a content block for the OpenAI Responses API request.
 * Supports input_image (base64 inline) and input_file (uploaded file ID) blocks.
 */
public class ContentBlock {

    public enum BlockType {
        INPUT_IMAGE,
        INPUT_FILE
    }

    private final BlockType type;
    private final String dataUrl;
    private final String fileId;
    private final String fileName;

    private ContentBlock(BlockType type, String dataUrl, String fileId, String fileName) {
        this.type = type;
        this.dataUrl = dataUrl;
        this.fileId = fileId;
        this.fileName = fileName;
    }

    /**
     * Creates an image content block with a base64 data URL.
     */
    public static ContentBlock imageBlock(String dataUrl, String fileName) {
        return new ContentBlock(BlockType.INPUT_IMAGE, dataUrl, null, fileName);
    }

    /**
     * Creates a document content block with an uploaded file ID.
     */
    public static ContentBlock documentBlock(String fileId, String fileName) {
        return new ContentBlock(BlockType.INPUT_FILE, null, fileId, fileName);
    }

    public BlockType getType() {
        return type;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }
}
