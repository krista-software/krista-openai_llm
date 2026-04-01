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

package app.krista.extensions.krista.llms.openai_qa.service;

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;

/**
 * Simplified service for validating uploaded files
 * Only checks PDF extension and file size
 */
public class FileValidationService {
    
    /**
     * Validates file before upload - only checks extension and size
     */
    public void validateFile(byte[] fileBytes, String fileName) throws OpenAiException {
        validateFileBytes(fileBytes);
        validatePdfExtension(fileName);
        validateFileSize(fileBytes);
    }
    
    /**
     * Validates file for generic (non-PDF-only) upload.
     * Checks bytes not empty, file name not empty, and size within limit.
     * Does NOT enforce PDF-only extension.
     */
    public void validateFileGeneric(byte[] fileBytes, String fileName) throws OpenAiException {
        validateFileBytes(fileBytes);
        validateFileName(fileName);
        validateFileSize(fileBytes);
    }

    private void validateFileName(String fileName) throws OpenAiException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new OpenAiException("File name cannot be empty");
        }
    }

    private void validateFileBytes(byte[] fileBytes) throws OpenAiException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new OpenAiException("File cannot be empty");
        }
    }
    
    private void validatePdfExtension(String fileName) throws OpenAiException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new OpenAiException("File name cannot be empty");
        }

        // Simple case-insensitive PDF extension check
        String lowerFileName = fileName.toLowerCase();
        if (!lowerFileName.endsWith(".pdf")) {
            throw new OpenAiException("Only PDF files are supported");
        }
    }
    
    private void validateFileSize(byte[] fileBytes) throws OpenAiException {
        if (fileBytes.length > OpenAiConfiguration.MAX_FILE_SIZE_BYTES) {
            long maxSizeMB = OpenAiConfiguration.MAX_FILE_SIZE_BYTES / (1024 * 1024);
            throw new OpenAiException(String.format("File size exceeds maximum allowed size of %d MB", maxSizeMB));
        }
    }
    
}
