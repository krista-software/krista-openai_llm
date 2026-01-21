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

import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.model.base.File;

import java.util.List;

/**
 * Interface for discussion-related validation operations.
 * Provides validation for session IDs, files, instructions, and questions.
 */
public interface IDiscussionValidator {
    
    /**
     * Validates a session ID format and existence.
     * 
     * @param sessionId The session ID to validate
     * @return ValidationResult containing validation status and error message
     */
    ValidationResult validateSessionId(String sessionId);
    
    /**
     * Validates a list of files for discussion upload.
     * 
     * @param files The files to validate
     * @return ValidationResult containing validation status and error message
     */
    ValidationResult validateFiles(List<File> files);
    
    /**
     * Validates instruction text for discussion use.
     * 
     * @param instructions The instruction text to validate
     * @return ValidationResult containing validation status and error message
     */
    ValidationResult validateInstructions(String instructions);
    
    /**
     * Validates a question for discussion processing.
     * 
     * @param question The question text to validate
     * @return ValidationResult containing validation status and error message
     */
    ValidationResult validateQuestion(String question);
    
    /**
     * Validates session ID format (UUID pattern).
     * 
     * @param sessionId The session ID to check
     * @return true if format is valid, false otherwise
     */
    boolean isValidSessionIdFormat(String sessionId);
    
    /**
     * Validates text length against specified limit.
     * 
     * @param text The text to validate
     * @param maxLength Maximum allowed length
     * @param fieldName Name of the field for error messages
     * @return ValidationResult containing validation status and error message
     */
    ValidationResult validateTextLength(String text, int maxLength, String fieldName);
}
