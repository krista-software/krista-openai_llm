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

package app.krista.extensions.krista.llms.openai_qa;

import app.krista.extension.impl.anno.*;
import app.krista.extension.util.InvokerAttributeProvider;
import app.krista.extensions.krista.llms.openai_qa.impl.QueryImpl;

import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.API_KEY_MISSING;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.MODEL_NOT_SELECTED;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.CONNECTION_TEST_FAILED;

/**
 * Main extension class for OpenAI ChatGPT integration with Krista platform.
 *
 * <p>This extension provides comprehensive AI capabilities including text generation,
 * image analysis, document Q&A, and multimodal interactions through OpenAI's API.
 * It serves as the primary entry point for all OpenAI-related functionality within
 * the Krista automation platform.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Multiple Model Support:</strong> GPT-3.5, GPT-4, GPT-4 Latest, GPT-4.1 variants</li>
 *   <li><strong>Multimodal Capabilities:</strong> Text, image, and document processing</li>
 *   <li><strong>Enterprise Security:</strong> Secure API key management and validation</li>
 *   <li><strong>Connection Testing:</strong> Automatic API connectivity validation</li>
 *   <li><strong>Documentation:</strong> Embedded static documentation resources</li>
 * </ul>
 *
 * <h3>Configuration:</h3>
 * <p>The extension requires two mandatory configuration fields:</p>
 * <ul>
 *   <li><strong>API Key:</strong> OpenAI API key (secured field)</li>
 *   <li><strong>Model Name:</strong> Selected ChatGPT model variant</li>
 * </ul>
 *
 * <h3>Supported Models:</h3>
 * <ul>
 *   <li>{@link OpenAIConstants#GPT_35} - ChatGPT 3.5 Turbo</li>
 *   <li>{@link OpenAIConstants#GPT_4} - ChatGPT 4</li>
 *   <li>{@link OpenAIConstants#GPT_LATEST} - ChatGPT 4 Latest</li>
 *   <li>{@link OpenAIConstants#GPT_4_1_NANO} - ChatGPT 4.1 Nano</li>
 *   <li>{@link OpenAIConstants#GPT_4_1_MINI} - ChatGPT 4.1 Mini</li>
 *   <li>{@link OpenAIConstants#GPT_4_1} - ChatGPT 4.1</li>
 * </ul>
 *
 * <h3>Extension Lifecycle:</h3>
 * <p>The extension handles standard Krista lifecycle events:</p>
 * <ul>
 *   <li><strong>Attribute Validation:</strong> Validates API key and model selection</li>
 *   <li><strong>Connection Testing:</strong> Tests OpenAI API connectivity</li>
 *   <li><strong>Custom Tabs:</strong> Provides embedded documentation</li>
 * </ul>
 *
 * @author Krista Extensions Team
 * @version 2.1.5
 * @since 1.0.0
 * @see QueryImpl for core AI query functionality
 * @see OpenAIConstants for configuration constants
 */
@Java(version = Java.Version.JAVA_21)
@Extension(version = "2.1.7", jaxrsId = "openai", name = "Open AI")
@Field.Text(value = OpenAIConstants.API_KEY, isSecured = true)
@Field.PickOne(value = OpenAIConstants.MODEL, values = {OpenAIConstants.GPT_35, OpenAIConstants.GPT_4, OpenAIConstants.GPT_LATEST, OpenAIConstants.GPT_4_1_NANO, OpenAIConstants.GPT_4_1_MINI, OpenAIConstants.GPT_4_1})
@StaticResource(path = "docs", file = "docs")
public class OpenAIExtension {

    /** Logger for this extension class. */
    private static final Logger log = LoggerFactory.getLogger(OpenAIExtension.class);

    /**
     * Injected OpenAI API key provider from extension configuration.
     * @see OpenAIConstants#API_KEY
     */
    @Inject
    @Named(OpenAIConstants.API_KEY)
    private InvokerAttributeProvider<String> apiKey;

    /**
     * Injected model name provider from extension configuration.
     * @see OpenAIConstants#MODEL
     */
    @Inject
    @Named(OpenAIConstants.MODEL)
    private InvokerAttributeProvider<String> modelName;

    /** Core query implementation service for AI operations. */
    private final QueryImpl query;

    /**
     * Constructs the OpenAI extension with dependency injection.
     *
     * @param query The query implementation service for AI operations
     */
    @Inject
    public OpenAIExtension(QueryImpl query) {
        this.query = query;
    }

    /**
     * Validates extension attributes during configuration.
     *
     * <p>This method is called by the Krista platform when the extension is being
     * configured or updated. It validates that both required attributes (API key
     * and model name) are properly set and tests the connection to OpenAI's API.</p>
     *
     * <h3>Validation Steps:</h3>
     * <ol>
     *   <li>Validates API key is present and is a string</li>
     *   <li>Validates model name is present and is a string</li>
     *   <li>Tests actual connectivity to OpenAI API</li>
     * </ol>
     *
     * @param attributes The extension configuration attributes to validate
     * @throws IllegalArgumentException if API key is missing, model is not selected,
     *                                  or connection test fails
     * @see #testConnection(String, String) for connection testing logic
     */
    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> attributes) {

        if (!(attributes.get(OpenAIConstants.API_KEY) instanceof String)) {
            throw new IllegalArgumentException(API_KEY_MISSING);

        }

        if (!(attributes.get(OpenAIConstants.MODEL) instanceof String)) {
            throw new IllegalArgumentException(MODEL_NOT_SELECTED);

        }

        this.testConnection((String) attributes.get(OpenAIConstants.API_KEY), (String) attributes.get(OpenAIConstants.MODEL));

    }

    /**
     * Tests connection to OpenAI API using current configuration.
     *
     * <p>This method is called by the Krista platform to verify that the extension
     * can successfully communicate with OpenAI's API using the configured credentials.</p>
     *
     * @throws IllegalArgumentException if connection test fails
     * @see #testConnection(String, String) for the actual test implementation
     */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
        this.testConnection(this.apiKey.get(), this.modelName.get());
    }

    /**
     * Performs actual connection test to OpenAI API.
     *
     * <p>This method sends a simple "Hello there!" message to the OpenAI API
     * to verify that the provided credentials are valid and the service is
     * accessible. It constructs a minimal valid request and attempts to execute it.</p>
     *
     * <h3>Test Process:</h3>
     * <ol>
     *   <li>Validates API key and model name are not null/empty</li>
     *   <li>Constructs a simple test message in OpenAI format</li>
     *   <li>Attempts to execute the request via {@link QueryImpl#execute}</li>
     *   <li>Logs and re-throws any connection failures</li>
     * </ol>
     *
     * @param apiKey The OpenAI API key to test
     * @param modelName The model name to use for testing
     * @throws IllegalArgumentException if credentials are invalid or connection fails
     */
    private void testConnection(String apiKey, String modelName) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException(API_KEY_MISSING);
        }
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException(MODEL_NOT_SELECTED);
        }
        JsonObject json = new JsonObject();
        json.addProperty("role", "user");
        json.addProperty("content", "Hello there!");
        JsonArray messages = new JsonArray();
        messages.add(json);
        JsonObject finalJson = new JsonObject();
        finalJson.add("messages", messages);
        String jsonString = finalJson.toString();
        try {
            query.execute(jsonString, apiKey, modelName, false);
        } catch (IOException e) {
            log.info("Connection test failed: {}", e.getMessage());
            throw new IllegalArgumentException(CONNECTION_TEST_FAILED + " Error details: " + e.getMessage());
        }

    }

    /**
     * Provides custom tabs for the extension UI.
     *
     * <p>This method defines additional tabs that appear in the Krista platform
     * when viewing this extension. It provides access to embedded documentation
     * resources that help users understand and configure the extension.</p>
     *
     * @return A map containing tab names and their corresponding static resource paths
     */
    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }
/*
//    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
//    public void invokerUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) throws IOException {
//        log.error("Informing connections as Open AI invoker is updated.");
//        registryService.informConnections(newAttributes, invoker.getInvokerId());
//        log.error("Informing successful after invoker update.");
//    }

    @InvokerRequest(InvokerRequest.Type.INVOKER_LOADED)
    public void invokerLoaded() throws IOException {
       /* log.error("Informing connections as Open AI invoker is loaded.");
        registryService.informConnections();
        log.error("Informing successful after invoker loaded.");*/


}

