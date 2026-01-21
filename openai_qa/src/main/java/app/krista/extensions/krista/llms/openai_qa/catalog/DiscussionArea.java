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

import app.krista.extension.executor.*;
import app.krista.extension.impl.anno.*;

import app.krista.extensions.krista.llms.openai_qa.dto.DiscussionHandler;
import app.krista.extensions.krista.llms.openai_qa.factory.DiscussionFactory;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionCatalogService;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.model.base.File;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;


@Domain(id = "catEntryDomain_6568c455-2416-4fcf-8d7a-2f3e0bef059b",
        name = "LLMs",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "2e2a0479-87e7-49a3-a779-8f00a665acd3")
public class DiscussionArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscussionArea.class);
        // Single service dependency following Single Responsibility Principle
    private final IDiscussionCatalogService catalogService;

    /**
     * Constructor with dependency injection for required services.
     *
     * <p>Initializes the DiscussionArea with a single catalog service dependency,
     * following the Single Responsibility Principle. The catalog service handles
     * all business logic, validation, and error handling.</p>
     *
     * @param mediaClientUtil Utility for media client operations and file handling
     * @param discussionHandler Core discussion handler for session management
     * @param mediaServerFileAccess Utility for accessing files from media server
     */
    @Inject
    public DiscussionArea(KristaMediaClientUtil mediaClientUtil, DiscussionHandler discussionHandler, MediaServerFileAccess mediaServerFileAccess) {
        // Initialize single catalog service using factory pattern
        DiscussionFactory factory = DiscussionFactory.getInstance();
        this.catalogService = factory.createDiscussionCatalogService(discussionHandler, mediaClientUtil, mediaServerFileAccess);
    }

    @CatalogRequest(
            id = "localDomainRequest_d738560f-007a-47d5-95e0-955a1aa90f0f",
            name = "Add Documents to Discussion",
            description = "Upload PDF documents to an existing discussion session. The AI will analyze these documents and use them to answer questions. Supports multiple file uploads with a maximum size of 50MB per file. Only PDF format is supported.",
            area = "Discussion",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Boolean(name = "Successful", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S"),
                         @Attribute(name = "tooltip", value = "Returns true if all documents were successfully uploaded and processed, false otherwise")},
            options = {})
    /**
     * Adds documents to a discussion session.
     *
     * <p>This method delegates to the catalog service which handles all validation,
     * file processing, and error handling. The catalog layer only orchestrates the request.</p>
     *
     * @param files List of PDF files to upload
     * @param sessionId The session identifier
     * @return ExtensionResponse indicating success or failure with details
     */
    public ExtensionResponse addDocumentsToDiscussion(
            @Field.File(name = "Files", multipleFileUpload = true, required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Select one or more PDF files to upload. Supported format: PDF only. Maximum file size: 512MB per file. Files will be analyzed by AI for question answering.")},
                    options = {}) List<File> files,
            @Field.Text(name = "Session Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Enter the unique session ID returned from 'Create Discussion' request. This identifies which discussion to add documents to.")},
                    options = {}) String sessionId) {

        LOGGER.info("Adding {} documents to session", files != null ? files.size() : 0);
        return catalogService.addDocumentsToSession(sessionId, files);
    }



    @CatalogRequest(
            id = "localDomainRequest_3e5f3358-d891-4466-a882-414879bcbf07",
            name = "Add Instructions to the Discussion",
            description = "Add custom instructions to guide the AI's behavior when answering questions about the uploaded documents. These instructions will be applied to all future questions in this discussion session. Examples: 'Summarize key points', 'Focus on technical details', 'Provide simple explanations'.",
            area = "Discussion",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Boolean(name = "Successful", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S"),
                         @Attribute(name = "tooltip", value = "Returns true if instructions were successfully added to the discussion session")},
            options = {})
    /**
     * Adds instructions to a discussion session.
     *
     * <p>This method delegates to the catalog service which handles all validation
     * and error handling. The catalog layer only orchestrates the request.</p>
     *
     * @param sessionId The session identifier
     * @param instructions The instruction text
     * @return ExtensionResponse indicating success or failure with details
     */
    public ExtensionResponse addInstructionsToDiscussion(
            @Field.Text(name = "Session Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Enter the session ID from 'Create Discussion' request to add instructions to")},
                    options = {}) String sessionId,
            @Field(name = "Instructions", type = "Paragraph", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "L"),
                                 @Attribute(name = "tooltip", value = "Enter detailed instructions to guide the AI's responses. Examples: 'Summarize key points', 'Focus on technical details', 'Provide simple explanations'. Maximum 10,000 characters.")},
                    options = {}) String instructions) {

        LOGGER.info("Adding instructions to session");
        return catalogService.addInstructionsToSession(sessionId, instructions);
    }

    @CatalogRequest(
            id = "localDomainRequest_56f8f968-8b4b-4abd-9013-330e165ab3c2",
            name = "Create Discussion",
            description = "Create a new discussion session for document-based Q&A. This generates a unique session ID that you'll use to upload documents, add instructions, and ask questions. Each session maintains its own context and conversation history.",
            area = "Discussion",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Text(name = "Session Id", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S"),
                         @Attribute(name = "tooltip", value = "Unique identifier for the new discussion session. Use this ID for all subsequent operations like uploading documents and asking questions.")},
            options = {})
    /**
     * Creates a new discussion session.
     *
     * <p>This method delegates to the catalog service which handles all business logic,
     * validation, and error handling. The catalog layer only orchestrates the request.</p>
     *
     * @return ExtensionResponse containing session ID or error information
     */
    public ExtensionResponse createDiscussion() {
        LOGGER.info("Creating new discussion session");
        return catalogService.createDiscussionSession();
    }

    @CatalogRequest(
            id = "localDomainRequest_6dabadf0-3965-4f83-b5f8-7b581bd11fb0",
            name = "Delete Discussion",
            description = "Permanently delete a discussion session and all its associated data including uploaded documents, conversation history, and instructions. This action cannot be undone. Use this to clean up completed discussions or free up resources.",
            area = "Discussion",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Boolean(name = "Successful", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S"),
                         @Attribute(name = "tooltip", value = "Returns true if the discussion session was successfully deleted, false otherwise")},
            options = {})
    /**
     * Deletes a discussion session.
     *
     * <p>This method delegates to the catalog service which handles all validation
     * and error handling. The catalog layer only orchestrates the request.</p>
     *
     * @param sessionId The session identifier
     * @return ExtensionResponse indicating success or failure with details
     */
    public ExtensionResponse deleteDiscussion(
            @Field.Text(name = "Session Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Enter the session ID of the discussion to delete. Warning: This will permanently remove all documents and conversation history.")},
                    options = {}) String sessionId) {

        LOGGER.info("Deleting discussion session");
        return catalogService.deleteDiscussionSession(sessionId);
    }


}
