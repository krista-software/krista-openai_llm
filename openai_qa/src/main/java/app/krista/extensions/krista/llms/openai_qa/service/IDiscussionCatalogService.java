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

import app.krista.extension.executor.ExtensionResponse;
import app.krista.model.base.File;

import java.util.List;

/**
 * Interface for discussion catalog business logic operations.
 * Handles all business logic for discussion catalog requests, keeping the catalog layer clean.
 */
public interface IDiscussionCatalogService {
    
    /**
     * Creates a new discussion session with proper validation and error handling.
     * 
     * @return ExtensionResponse containing session ID or error information
     */
    ExtensionResponse createDiscussionSession();
    
    /**
     * Adds documents to a discussion session with comprehensive validation.
     * 
     * @param sessionId The session identifier
     * @param files List of files to upload
     * @return ExtensionResponse indicating success or failure with details
     */
    ExtensionResponse addDocumentsToSession(String sessionId, List<File> files);
    
    /**
     * Adds instructions to a discussion session with validation.
     * 
     * @param sessionId The session identifier
     * @param instructions The instruction text
     * @return ExtensionResponse indicating success or failure with details
     */
    ExtensionResponse addInstructionsToSession(String sessionId, String instructions);
    
    /**
     * Deletes a discussion session with proper cleanup.
     * 
     * @param sessionId The session identifier
     * @return ExtensionResponse indicating success or failure with details
     */
    ExtensionResponse deleteDiscussionSession(String sessionId);
}
