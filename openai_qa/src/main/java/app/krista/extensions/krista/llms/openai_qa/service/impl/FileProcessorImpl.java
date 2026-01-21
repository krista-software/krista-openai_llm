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

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.extensions.krista.llms.openai_qa.service.IFileProcessor;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.model.base.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of file processor for discussion file operations.
 * Handles file validation, conversion, and preparation for discussion sessions.
 */
public class FileProcessorImpl implements IFileProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorImpl.class);
    
    private final KristaMediaClientUtil mediaClientUtil;
    private final MediaServerFileAccess mediaServerFileAccess;
    private final IDiscussionValidator validator;
    /**
     * Creates a file processor with required dependencies.
     * 
     * @param mediaClientUtil Media client utility for file operations
     * @param mediaServerFileAccess Media server file access utility
     * @param validator Discussion validator for file validation
     */
    public FileProcessorImpl(KristaMediaClientUtil mediaClientUtil,
                             MediaServerFileAccess mediaServerFileAccess,
                             IDiscussionValidator validator) {
        this.mediaClientUtil = mediaClientUtil;
        this.mediaServerFileAccess = mediaServerFileAccess;
        this.validator = validator;
    }
    
    @Override
    public DocumentSet processFile(File file) throws OpenAiException {
        logger.debug("Processing single file: {}", file != null ? file.getFileName() : "null");

        try {
            // Convert to Java File object
            java.io.File javaFile = convertToJavaFile(file);

            // Validate file size
            validateFileSize(javaFile, file.getFileName());

            // Read file bytes
            byte[] fileBytes = readFileAsBytes(javaFile);

            // Create DocumentSet
            return new DocumentSet(javaFile, fileBytes);

        } catch (IOException e) {
            logger.error("Error processing file {}: {}", file.getFileName(), e.getMessage());
            throw new OpenAiException("Failed to process file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<DocumentSet> processFiles(List<File> files) throws OpenAiException {
        logger.debug("Processing {} files for discussion", files != null ? files.size() : 0);

        List<DocumentSet> documents = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (File file : files) {
            try {
                DocumentSet document = processFile(file);
                documents.add(document);
                logger.debug("Successfully processed file: {}", file.getFileName());
                
            } catch (OpenAiException e) {
                String error = "Failed to process file '" + file.getFileName() + "': " + e.getMessage();
                errors.add(error);
                logger.warn(error);
            }
        }
        
        if (!errors.isEmpty()) {
            throw new OpenAiException("File processing errors: " + String.join("; ", errors));
        }
        
        return documents;
    }
    
    @Override
    public boolean validateFile(File file) {
        ValidationResult result = validator.validateFiles(List.of(file));
        return result.isValid();
    }
    
    @Override
    public String getValidationError(File file) {
        ValidationResult result = validator.validateFiles(List.of(file));
        return result.getErrorMessage();
    }
    
    @Override
    public boolean isSupportedFormat(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(OpenAiConfiguration.PDF_EXTENSION);
    }


    /**
     * Validates file size against maximum allowed size.
     *
     * @param javaFile The file to validate
     * @param fileName The name of the file for error messages
     * @throws OpenAiException if file exceeds maximum size
     */
    private void validateFileSize(java.io.File javaFile, String fileName) throws OpenAiException {
        long fileSize = javaFile.length();

        if (fileSize > OpenAiConfiguration.MAX_FILE_SIZE_BYTES) {
            long maxSizeMB = OpenAiConfiguration.MAX_FILE_SIZE_BYTES / (1024 * 1024);
            long actualSizeMB = fileSize / (1024 * 1024);

            String errorMessage = String.format(
                "File '%s' exceeds maximum size limit. File size: %d MB, Maximum allowed: %d MB. " +
                "Please reduce the file size or split into smaller files.",
                fileName, actualSizeMB, maxSizeMB
            );

            logger.warn("File size validation failed: {}", errorMessage);
            throw new OpenAiException(errorMessage);
        }

        logger.debug("File '{}' size validation passed: {} bytes ({} MB)",
            fileName, fileSize, fileSize / (1024 * 1024));
    }

    /**
     * Converts Krista File to Java File object.
     *
     * @param file The Krista File to convert
     * @return Java File object
     * @throws IOException if conversion fails
     */
    private java.io.File convertToJavaFile(File file) throws IOException {
            return mediaServerFileAccess.downloadFileFromMediaServer(file);
    }

    /**
     * Reads file content as byte array.
     *
     * @param javaFile The Java File to read
     * @return File content as byte array
     * @throws IOException if reading fails
     */
    private byte[] readFileAsBytes(java.io.File javaFile) throws IOException {
            return mediaClientUtil.readFileAsBytes(javaFile);
    }
}
