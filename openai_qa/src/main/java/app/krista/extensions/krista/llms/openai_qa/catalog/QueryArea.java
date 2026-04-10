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
import app.krista.extension.executor.impl.InformAPersonAction;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;

import app.krista.extensions.krista.llms.openai_qa.dto.DiscussionHandler;
import app.krista.extensions.krista.llms.openai_qa.factory.DiscussionFactory;
import app.krista.extensions.krista.llms.openai_qa.impl.QueryImpl;
import app.krista.extensions.krista.llms.openai_qa.service.CapabilitiesService;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionService;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.util.ExtensionUtil.validateAttributes;
import static app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants.API_KEY;

/**
 * Query Area catalog requests for LLM operations and document-based Q&A.
 *
 * <p>This class provides catalog requests for querying Large Language Models (LLMs) and
 * processing questions about uploaded documents. It implements enterprise architecture patterns
 * including dependency injection, service abstraction, and comprehensive error handling.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Direct LLM querying with prompt engineering</li>
 *   <li>Document-based Q&A through discussion sessions</li>
 *   <li>Model capability information and configuration</li>
 *   <li>Secure logging with session ID masking</li>
 *   <li>Enhanced input validation (10K character questions)</li>
 *   <li>Comprehensive error handling with remediation actions</li>
 * </ul>
 *
 * <p><strong>Architecture:</strong></p>
 * <ul>
 *   <li>Uses dependency injection for service components</li>
 *   <li>Implements factory pattern for object creation</li>
 *   <li>Follows interface-based design for loose coupling</li>
 *   <li>Provides consistent exception handling across all operations</li>
 * </ul>
 *
 * <p><strong>Main Catalog Request:</strong></p>
 * <ul>
 *   <li><strong>Ask a question from discussion</strong> - Primary method for document-based Q&A</li>
 * </ul>
 *
 * @author OpenAI Extension Team
 * @version 2.1.3
 * @since 1.0.0
 */
@Domain(id = "catEntryDomain_6568c455-2416-4fcf-8d7a-2f3e0bef059b",
        name = "LLMs",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "2e2aa098-8c84-42b8-9e0b-64c7e9f64a3c")
public class QueryArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryArea.class);

    private final QueryImpl queryImpl;
    private final Map<String, Object> attributes;
    private final CapabilitiesService capabilitiesService;
    private final DiscussionHandler discussionHandler;

    // Service dependencies with dependency injection
    private final IDiscussionService discussionService;
    private final IDiscussionValidator discussionValidator;

    /**
     * Constructor with dependency injection for required services.
     *
     * <p>Initializes QueryArea with all required dependencies and creates
     * service instances using the factory pattern for enterprise architecture.</p>
     *
     * @param queryImpl Core query implementation for LLM operations
     * @param invoker Invoker for accessing request attributes
     * @param capabilitiesService Service for LLM capability information
     * @param discussionHandler Handler for discussion-based operations
     */
    @Inject
    public QueryArea(QueryImpl queryImpl, Invoker invoker, CapabilitiesService capabilitiesService, DiscussionHandler discussionHandler) {
        this.queryImpl = queryImpl;
        this.capabilitiesService = capabilitiesService;
        this.discussionHandler = discussionHandler;
        this.attributes = invoker.getAttributes();

        // Initialize services using factory pattern
        DiscussionFactory factory = DiscussionFactory.getInstance();
        this.discussionService = factory.createDiscussionService(discussionHandler);
        this.discussionValidator = factory.createDiscussionValidator();
    }

    @CatalogRequest(
            id = "localDomainRequest_ead52dea-aabc-4d65-abab-8f23284dece2",
            name = "Ask LLM",
            description = "Queries a LLM with the given prompt",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "LLM Response", required = false, attributes = {
            @Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse askLLM(
            @Field.Text(name = "Prompt", required = true, attributes = {
                    @Attribute(name = "visualWidth", value = "S")}, options = {}) String prompt) {
        try {
            ExtensionResponse validationResponse = validateAttributes(attributes);
            if (validationResponse != null) {
                return validationResponse;
            }
            String answer = queryImpl.queryModel(prompt, false);
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("LLM Response", answer),
                    null, null, null);
        } catch (Exception cause) {
            return handleExceptionResponse("querying LLM with API", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_1059f8f1-ee8a-4183-a0cb-e94e40f47a57",
            name = "Get AI Model name",
            description = "Returns the name of the AI model that is being queried",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "AI Model", required = false, attributes = {
            @Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse getAIModelName() {
        try {
            String modelName = queryImpl.getAIModel();
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("AI Model", modelName),
                    null, null, null);
        } catch (Exception cause) {
            return handleExceptionResponse("getting AI model name", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_e9833c97-1337-4666-afbf-314444f03f27",
            name = "Ask a question",
            description = "Catalog Request for querying Open AI with a simple query and an optional context to the query without any prompt engineering",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Response", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse askAQuestion(
            @Field.Text(name = "Query", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String query,
            @Field.Text(name = "Context", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String context) {
        try {
            String response = queryImpl.constructPromptAndQueryModel(query, context);
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("Response", response),
                    null, null, null);
        } catch (Exception cause) {
            return handleExceptionResponse("processing question with API", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_5c27fb7f-8757-4faa-afa8-3cb6a5781bf2",
            name = "Get LLM Capabilities",
            description = "Gets the capabilities of a given generative AI provider.",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Boolean(name = "Multimodal support", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    @Field.Boolean(name = "Hard Task support", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    @Field.Boolean(name = "Medium Task support", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    @Field.Boolean(name = "Easy Task support", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse getLLMCapabilities() {
        try {
            Map<String, Object> capabilities = capabilitiesService.getCapabilities();
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, capabilities,
                    null, null, null);
        } catch (Exception cause) {
            return handleExceptionResponse("getting LLM capabilities", cause);
        }
    }

    /**
     * Ask a question from discussion - Main catalog request for document-based Q&A.
     *
     * <p>This is the primary method for querying uploaded documents within a discussion session.
     * It uses enterprise architecture with proper validation, error handling, and security measures.</p>
     *
     * <p><strong>Features:</strong></p>
     * <ul>
     *   <li>Session validation with UUID format checking</li>
     *   <li>Question length validation (up to 10,000 characters)</li>
     *   <li>Secure logging with session ID masking</li>
     *   <li>Comprehensive error handling with remediation actions</li>
     *   <li>Service-layer architecture with dependency injection</li>
     * </ul>
     *
     * @param sessionId The discussion session ID containing uploaded documents
     * @param queryOrJsonSchema The question to ask about the documents
     * @return ExtensionResponse containing the AI-generated answer or error information
     */
    @CatalogRequest(
            id = "localDomainRequest_45a669a7-4ff2-41c2-8b10-4a4fac6f47b1",
            name = "Ask a question from discussion",
            description = "Ask questions about uploaded documents in a discussion session. The AI will analyze the document content and provide relevant answers based on the uploaded files and any instructions you've provided. Requires an active session with uploaded documents. Supports up to 10,000 character questions.",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field(name = "Response", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L"),
                         @Attribute(name = "tooltip", value = "AI-generated response based on the uploaded documents and your question. The response will reference specific content from your documents.")},
            options = {})
    public ExtensionResponse askAQuestionFromDiscussion(
            @Field.Text(name = "Session Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Enter the session ID from 'Create Discussion' request. This session must have documents uploaded to answer questions.")},
                    options = {}) String sessionId,
            @Field.Text(name = "Query Or Json Schema", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                                 @Attribute(name = "tooltip", value = "Enter your question about the uploaded documents. Be specific for better results. Examples: 'What are the key findings?', 'Summarize the main points', 'What does the document say about X?' Maximum 10,000 characters.")},
                    options = {}) String queryOrJsonSchema) {

        LOGGER.info("Processing question for session: {}", maskSessionId(sessionId));
        LOGGER.debug("Question length: {} characters", queryOrJsonSchema != null ? queryOrJsonSchema.length() : 0);

        try {
            // Use service-layer validation instead of manual validation
            var sessionValidation = discussionValidator.validateSessionId(sessionId);
            if (sessionValidation.hasError()) {
                return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Error", sessionValidation.getErrorMessage()),
                    null, null, null);
            }

            var questionValidation = discussionValidator.validateQuestion(queryOrJsonSchema);
            if (questionValidation.hasError()) {
                return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Error", questionValidation.getErrorMessage()),
                    null, null, null);
            }

            ExtensionResponse validationResponse = validateAttributes(attributes);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Use service layer for question processing
            String answer = discussionService.askQuestion(sessionId, queryOrJsonSchema);

            LOGGER.info("Question processed successfully for session: {}", maskSessionId(sessionId));
            LOGGER.debug("Response length: {} characters", answer.length());

            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Response", answer), null, null, null);

        } catch (Exception cause) {
            return handleExceptionResponse("processing discussion question", cause);
        }
    }
    private boolean isConfigured() {
        return attributes != null && attributes.get(API_KEY) != null;
    }

    @CatalogRequest(
            id = "localDomainRequest_ae56d004-a74b-4cf6-94c8-3daddf5eecc0",
            name = "Ask A Question From A Discussion Using Settings",
            description = "This request takes in parameters like user prompt, system prompt, both being simple plain text strings along with temperature, top k and top p as well as max tokens. To be used by more technical users who know what they are doing.",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field(name = "LLM Response", type = "Paragraph", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
    public ExtensionResponse askAQuestionFromADiscussionUsingSettings(
            @Field.Text(name = "User Prompt", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'A plain text query with context to be asked to LLM'")}, options = {}) String userPrompt,
            @Field.Text(name = "System Prompt", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'A plain text system instructions to be provided to the LLM'")}, options = {}) String systemPrompt,
            @Field(name = "Top K", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'It determines how many of the most likely tokens should be considered when generating a response'")}, options = {}) Double topK,
            @Field(name = "Top P", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Top P means, Only consider the possibilities that equal or exceed this value. This parameter is expressed as a number between 0.0 and 1.0, with 1.0 being 100% and 0 being 0%'")}, options = {}) Double topP,
            @Field(name = "Max Tokens", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'This refers to the maximum number of tokens that can be processed in a single input or output for a given model. This is a crucial parameter for controlling the length and cost of interactions with LLMs'")}, options = {}) Double maxTokens,
            @Field.Text(name = "Session Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sessionId,
            @Field(name = "Temperature", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'In large language models (LLMs), temperature is a parameter that controls the randomness and creativity of the model\\'s output. It essentially adjusts how confident the model is when choosing the next word in a sequence. A lower temperature (e.g., 0.2) leads to more predictable and deterministic outputs, while a higher temperature (e.g., 0.8) introduces more randomness and creativity.'")}, options = {}) Double temperature) {
        throw new UnsupportedOperationException("This method is not yet implemented! If you don't want to support this method, please remove the complete method");
    }

    @CatalogRequest(
            id = "localDomainRequest_2f936e7b-c926-457e-97f1-89c3ab5872aa",
            name = "Ask A Question Using Settings",
            description = "This request takes in parameters like user prompt, system prompt, both being simple plain text strings along with temperature, top k and top p as well as max tokens. To be used by more technical users who know what they are doing.",
            area = "Query",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field(name = "LLM Response", type = "Paragraph", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
    public ExtensionResponse askAQuestionUsingSettings(
            @Field.Text(name = "User Prompt", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String userPrompt,
            @Field.Text(name = "System Prompt", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String systemPrompt,
            @Field(name = "Temperature", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'In large language models (LLMs), temperature is a parameter that controls the randomness and creativity of the model\\'s output. It essentially adjusts how confident the model is when choosing the next word in a sequence. A lower temperature (e.g., 0.2) leads to more predictable and deterministic outputs, while a higher temperature (e.g., 0.8) introduces more randomness and creativity.'")}, options = {}) Double temperature,
            @Field(name = "Top K", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'It determines how many of the most likely tokens should be considered when generating a response'")}, options = {}) Double topK,
            @Field(name = "Top P", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Top P means, Only consider the possibilities that equal or exceed this value. This parameter is expressed as a number between 0.0 and 1.0, with 1.0 being 100% and 0 being 0%'")}, options = {}) Double topP,
            @Field(name = "Max Tokens", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'This refers to the maximum number of tokens that can be processed in a single input or output for a given model. This is a crucial parameter for controlling the length and cost of interactions with LLMs'")}, options = {}) Double maxTokens) {

        ExtensionResponse validationResponse = validateAttributes(attributes);
        if (validationResponse != null) {
            return validationResponse;
        }

        String answer = queryImpl.constructPromptAndQueryModelWithSettings(userPrompt, systemPrompt,
                                                                          temperature, topK, topP, maxTokens);
        return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("LLM Response", answer),
                null, null, null);
    }


    /**
     * Masks session ID for secure logging (shows only first and last 4 characters).
     *
     * @param sessionId The session ID to mask
     * @return Masked session ID for logging
     */
    private String maskSessionId(String sessionId) {
        if (sessionId == null || sessionId.length() < 8) {
            return "****";
        }
        return sessionId.substring(0, 4) + "****" + sessionId.substring(sessionId.length() - 4);
    }

    /**
     * Handle exceptions and create appropriate error responses with logging and remediation actions.
     *
     * <p>Provides consistent error handling across all catalog requests with:</p>
     * <ul>
     *   <li>Secure logging without sensitive data exposure</li>
     *   <li>Context-aware remediation messages</li>
     *   <li>Proper KSDK-compliant response structure</li>
     * </ul>
     *
     * @param operation Description of the operation that failed
     * @param cause The exception that occurred
     * @return ExtensionResponse with error details and remediation actions
     */
    private ExtensionResponse handleExceptionResponse(String operation, Exception cause) {
        String errorMessage = "An error occurred while " + operation;
        LOGGER.error("Error during {}: {}", operation, cause.getMessage(), cause);

        List<RemediationAction> actions = new ArrayList<>();

        // OpenAI-specific remediation messages based on operation type
        String remediationMessage;
        if (operation.contains("API") || operation.contains("model")) {
            remediationMessage = "Please check the API key configuration and model selection, then try again. If the issue persists, contact support.";
        } else if (operation.contains("discussion") || operation.contains("session")) {
            remediationMessage = "Please verify the session ID is valid and has documents uploaded, then try again. If the issue persists, contact support.";
        } else {
            remediationMessage = "Please check the input parameters and try again. If the issue persists, contact support.";
        }

        actions.add(InformAPersonAction.create(remediationMessage,
                RemediationAction.RecipientType.ALL_PARTICIPANTS, null));

        RemediationActions remediationActions = new RemediationActions(actions, null);

        return new ExtensionResponseBuilder()
                .success(Collections.emptyMap(), remediationActions, Map.of("ERROR", errorMessage))
                .build();
    }
}