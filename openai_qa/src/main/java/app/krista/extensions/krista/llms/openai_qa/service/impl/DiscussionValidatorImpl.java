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
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.model.base.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of discussion validator for input validation operations.
 * Provides comprehensive validation for session IDs, files, instructions, and questions.
 */
public class DiscussionValidatorImpl implements IDiscussionValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscussionValidatorImpl.class);
    
    // UUID pattern for session ID validation
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    @Override
    public ValidationResult validateSessionId(String sessionId) {
        logger.debug("Validating session ID format");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ValidationResult.failure("Session ID is required. Please provide a valid session ID from 'Create Discussion' request.");
        }
        
        if (!isValidSessionIdFormat(sessionId)) {
            return ValidationResult.failure("Invalid session ID format. Please provide a valid session ID from 'Create Discussion' request.");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public ValidationResult validateFiles(List<File> files) {
        logger.debug("Validating {} files for discussion upload", files != null ? files.size() : 0);
        
        if (files == null || files.isEmpty()) {
            return ValidationResult.failure("At least one file is required for document upload.");
        }
        
        if (files.size() > OpenAiConfiguration.MAX_FILES_PER_SESSION) {
            return ValidationResult.failure("Too many files. Maximum " + OpenAiConfiguration.MAX_FILES_PER_SESSION + " files allowed per session.");
        }
        
        for (File file : files) {
            ValidationResult fileResult = validateSingleFile(file);
            if (fileResult.hasError()) {
                return fileResult;
            }
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public ValidationResult validateInstructions(String instructions) {
        logger.debug("Validating instruction text length");
        
        if (instructions == null || instructions.trim().isEmpty()) {
            return ValidationResult.failure("Instructions cannot be empty. Please provide guidance for the AI assistant.");
        }
        
        return validateTextLength(instructions, OpenAiConfiguration.MAX_INSTRUCTION_LENGTH, "Instructions");
    }
    
    @Override
    public ValidationResult validateQuestion(String question) {
        logger.debug("Validating question text length");
        
        if (question == null || question.trim().isEmpty()) {
            return ValidationResult.failure("Question is required. Please enter a question about your uploaded documents.");
        }
        
        return validateTextLength(question, OpenAiConfiguration.MAX_MESSAGE_LENGTH, "Question");
    }
    
    @Override
    public boolean isValidSessionIdFormat(String sessionId) {
        return sessionId != null && UUID_PATTERN.matcher(sessionId).matches();
    }
    
    @Override
    public ValidationResult validateTextLength(String text, int maxLength, String fieldName) {
        if (text == null) {
            return ValidationResult.failure(fieldName + " cannot be null.");
        }
        
        if (text.length() > maxLength) {
            return ValidationResult.failure(fieldName + " is too long. Please limit to " + maxLength + " characters or less.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates a single file for discussion upload.
     * 
     * @param file The file to validate
     * @return ValidationResult containing validation status and error message
     */
    private ValidationResult validateSingleFile(File file) {
        if (file == null) {
            return ValidationResult.failure("File cannot be null.");
        }
        
        if (file.getFileName() == null || file.getFileName().isBlank()) {
            return ValidationResult.failure("File name cannot be empty.");
        }
        
        // Check file format
        if (!isSupportedFormat(file.getFileName())) {
            return ValidationResult.failure("File '" + file.getFileName() + "' has unsupported format. Only PDF files are supported.");
        }
        
        // Note: File size validation will be done during file processing
        // since the Krista File object doesn't expose size directly
        // The actual size check happens when the file is downloaded and processed
        
        return ValidationResult.success();
    }
    
    /**
     * Checks if file format is supported for discussion upload.
     * 
     * @param fileName The name of the file to check
     * @return true if format is supported, false otherwise
     */
    private boolean isSupportedFormat(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(OpenAiConfiguration.PDF_EXTENSION);
    }
}
