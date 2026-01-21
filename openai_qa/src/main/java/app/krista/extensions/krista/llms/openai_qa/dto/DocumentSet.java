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

package app.krista.extensions.krista.llms.openai_qa.dto;

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import java.io.File;

/**
 * Represents a document that can be uploaded to OpenAI for processing.
 * Supports both file-based and byte array-based document handling.
 */
public class DocumentSet {
    private final String name;      // Document name, e.g. "contract.pdf"
    private final File file;        // The actual file reference
    private final byte[] fileBytes; // File content as bytes (optional)
    private final String mimeType;  // MIME type of the document

    // Constructor with name and file
    public DocumentSet(String name, File file) {
        this.name = name;
        this.file = file;
        this.fileBytes = null;
        this.mimeType = determineMimeType(file.getName());
    }

    // Constructor with file and bytes (used by DiscussionArea)
    public DocumentSet(File file, byte[] fileBytes) {
        this.name = file.getName();
        this.file = file;
        this.fileBytes = fileBytes;
        this.mimeType = determineMimeType(file.getName());
    }

    // Constructor with all parameters
    public DocumentSet(String name, File file, byte[] fileBytes, String mimeType) {
        this.name = name;
        this.file = file;
        this.fileBytes = fileBytes;
        this.mimeType = mimeType != null ? mimeType : determineMimeType(name);
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    // ✅ NEW: Additional methods for multi-document session support

    /**
     * Get file size in bytes
     */
    public long getFileSize() {
        if (fileBytes != null) {
            return fileBytes.length;
        }
        if (file != null && file.exists()) {
            return file.length();
        }
        return 0;
    }

    /**
     * Get file extension
     */
    public String getFileExtension() {
        if (name == null) return "";
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * Check if this is a PDF document (only supported format)
     */
    public boolean isPdfDocument() {
        return OpenAiConfiguration.PDF_FILE_TYPE.equals(getFileExtension());
    }





    /**
     * Determines MIME type - only PDF files are supported
     */
    private String determineMimeType(String fileName) {
        if (fileName == null) return OpenAiConfiguration.PDF_MIME_TYPE;

        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(OpenAiConfiguration.PDF_EXTENSION)) {
            return OpenAiConfiguration.PDF_MIME_TYPE;
        }

        // Default to PDF since that's the only supported format
        return OpenAiConfiguration.PDF_MIME_TYPE;
    }

    @Override
    public String toString() {
        return "DocumentSet{" +
                "name='" + name + '\'' +
                ", file=" + (file != null ? file.getName() : "null") +
                ", hasBytes=" + (fileBytes != null) +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
