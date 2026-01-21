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

import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.model.base.File;
import java.util.List;

/**
 * Interface for file processing operations.
 * Handles file validation, conversion, and preparation for discussion sessions.
 */
public interface IFileProcessor {
    
    /**
     * Processes a single file for discussion use.
     * 
     * @param file The file to process
     * @return DocumentSet containing processed file data
     * @throws OpenAiException if file processing fails
     */
    DocumentSet processFile(File file) throws OpenAiException;
    
    /**
     * Processes multiple files for discussion use.
     * 
     * @param files List of files to process
     * @return List of DocumentSet objects containing processed file data
     * @throws OpenAiException if any file processing fails
     */
    List<DocumentSet> processFiles(List<File> files) throws OpenAiException;
    
    /**
     * Validates a file meets discussion requirements.
     * 
     * @param file The file to validate
     * @return true if file is valid, false otherwise
     */
    boolean validateFile(File file);
    
    /**
     * Gets validation error message for a file.
     * 
     * @param file The file to validate
     * @return Error message if file is invalid, null if valid
     */
    String getValidationError(File file);
    
    /**
     * Checks if file format is supported.
     * 
     * @param fileName The name of the file to check
     * @return true if format is supported, false otherwise
     */
    boolean isSupportedFormat(String fileName);

}
