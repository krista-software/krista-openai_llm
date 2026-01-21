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

package app.krista.extensions.krista.llms.openai_qa.impl;

import app.krista.extension.util.InvokerAttributeProvider;
import app.krista.extensions.krista.llms.openai_qa.util.LoggingUtils;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import app.krista.extensions.krista.llms.openai_qa.util.StringI18N;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.API_KEY_MISSING;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.MODEL_NOT_SELECTED;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.REQUEST_PROCESSING_ERROR;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.API_SERVICE_UNAVAILABLE;

/**
 * Implementation service for executing queries against OpenAI's ChatGPT API.
 *
 * <p>This service provides comprehensive functionality for interacting with OpenAI's language models,
 * including text-only queries, image analysis, and customizable model parameters. It handles
 * authentication, request formatting, response parsing, error handling, and retry logic.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Multiple Model Support:</strong> GPT-3.5, GPT-4, GPT-4 Latest, GPT-4.1 variants</li>
 *   <li><strong>Image Analysis:</strong> Support for vision-enabled models with image inputs</li>
 *   <li><strong>Customizable Parameters:</strong> Temperature, top_p, max_tokens configuration</li>
 *   <li><strong>Robust Error Handling:</strong> Automatic retries, rate limiting, and user-friendly error messages</li>
 *   <li><strong>Multiple Input Formats:</strong> Text, images (file/base64), and combined queries</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Simple text query
 * String response = queryImpl.queryModel("What is machine learning?", false);
 *
 * // Query with context
 * String response = queryImpl.constructPromptAndQueryModel("Explain this concept", contextText);
 *
 * // Image analysis
 * String response = queryImpl.constructPromptAndQueryModelForImageFile("Describe this image", null, imageFile);
 *
 * // Custom parameters
 * String response = queryImpl.constructPromptAndQueryModelWithSettings(
 *     "Creative writing prompt", "You are a creative writer", 0.8, null, 0.9, 2000.0);
 * }</pre>
 *
 * <h3>Configuration:</h3>
 * <p>This service requires proper configuration of:</p>
 * <ul>
 *   <li>OpenAI API key via {@link OpenAIConstants#API_KEY}</li>
 *   <li>Model selection via {@link OpenAIConstants#MODEL}</li>
 * </ul>
 *
 * <h3>Error Handling:</h3>
 * <p>The service implements comprehensive error handling including:</p>
 * <ul>
 *   <li>Automatic retry logic (up to 3 attempts)</li>
 *   <li>Rate limiting detection and backoff</li>
 *   <li>User-friendly error messages from {@link app.krista.extensions.krista.llms.openai_qa.catalog.Constants}</li>
 *   <li>Graceful degradation for service unavailability</li>
 * </ul>
 *
 * @author Krista Extensions Team
 * @version 2.1.5
 * @since 1.0.0
 * @see OpenAIConstants
 * @see app.krista.extensions.krista.llms.openai_qa.catalog.Constants
 */
@Service
public class QueryImpl {

    /** Logger for this class. */
    private static final Logger log = LoggerFactory.getLogger(QueryImpl.class);

    /**
     * Injected OpenAI API key provider.
     * @see OpenAIConstants#API_KEY
     */
    @Inject
    @Named(OpenAIConstants.API_KEY)
    private InvokerAttributeProvider<String> apiKey;

    /**
     * Injected model name provider.
     * @see OpenAIConstants#MODEL
     */
    @Inject
    @Named(OpenAIConstants.MODEL)
    private InvokerAttributeProvider<String> modelName;

    /** OpenAI Chat Completions API endpoint URL. */
    private static final String V1_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    /** Model identifier for GPT-4 Vision (using gpt-4o-mini for image processing). */
    private static final String GPT_4_VISSION_PREVIEW = "gpt-4o-mini";

    /** Model identifier for GPT-4. */
    private static final String GPT_4 = "gpt-4";

    /** Model identifier for GPT-4.1 Mini. */
    private static final String GPT_4_1_MINI = "gpt-4.1-mini";

    /** Model identifier for GPT-3.5 Turbo 16K. */
    private static final String GPT_35_TURBO_16K = "gpt-3.5-turbo-16k";

    /** Model identifier for GPT-4 Latest. */
    private static final String GPT_4_LATEST = "chatgpt-4o-latest";

    /** Shared HTTP client instance for API requests. */
    private static OkHttpClient client = null;

    /** Temperature setting for precise, focused responses (0.3). */
    static private final double PRECISE_TEMP = .3;

    /** Temperature setting for creative, varied responses (0.8). */
    static private final double CREATIVE_TEMP = .8;

    /** Top-p setting for precise, focused responses (0.2). */
    static private final double PRECISE_TOPP = .2;

    /** Top-p setting for creative, varied responses (0.8). */
    static private final double CREATIVE_TOPP = .8;

    /** Balanced token allocation for input and output (4KB). */
    static private final int BALANCED_INPUT_OUT_TOKENS = 4 * 1024;

    /** Maximum response tokens for most models (2064). */
    static private final int MAX_RESPONSE_TOKENS = 2064;

    /**
     * Maps user-friendly model display names to OpenAI API model identifiers.
     *
     * <p>This method translates the model names shown in the UI (from {@link OpenAIConstants})
     * to the actual model identifiers required by the OpenAI API. When image processing is
     * required, it automatically selects a vision-capable model.</p>
     *
     * <h3>Model Mapping:</h3>
     * <ul>
     *   <li>{@link OpenAIConstants#GPT_4} → "gpt-4"</li>
     *   <li>{@link OpenAIConstants#GPT_35} → "gpt-3.5-turbo-16k"</li>
     *   <li>{@link OpenAIConstants#GPT_LATEST} → "chatgpt-4o-latest"</li>
     *   <li>Default → "gpt-4.1-mini"</li>
     *   <li>Any model with image → "gpt-4o-mini" (vision-capable)</li>
     * </ul>
     *
     * @param modelDisplayName The user-friendly model name from the UI selection
     * @param withImage {@code true} if the request includes image processing, {@code false} otherwise
     * @return The OpenAI API model identifier string
     * @see OpenAIConstants for display name constants
     */
    private String getModelName(String modelDisplayName, boolean withImage) {
        if (withImage) {
            return GPT_4_VISSION_PREVIEW;
        }
        return switch (modelDisplayName) {
            case OpenAIConstants.GPT_4 -> GPT_4;
            case OpenAIConstants.GPT_35 -> GPT_35_TURBO_16K;
            case OpenAIConstants.GPT_LATEST -> GPT_4_LATEST;
            default -> GPT_4_1_MINI;
        };
    }

    /**
     * Executes a query against the configured OpenAI model with basic parameters.
     *
     * <p>This is the primary method for sending queries to OpenAI's API. It handles
     * authentication validation, model selection, request execution, and response processing.
     * The method uses predefined precise settings (temperature=0.3, top_p=0.2) for consistent,
     * focused responses.</p>
     *
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Validates API key and model configuration</li>
     *   <li>Executes the query with retry logic</li>
     *   <li>Logs execution time and response details</li>
     *   <li>Returns the AI response or user-friendly error message</li>
     * </ol>
     *
     * <h3>Error Handling:</h3>
     * <p>Returns user-friendly error messages instead of throwing exceptions:</p>
     * <ul>
     *   <li>Missing API key → {@link app.krista.extensions.krista.llms.openai_qa.catalog.Constants#API_KEY_MISSING}</li>
     *   <li>Missing model → {@link app.krista.extensions.krista.llms.openai_qa.catalog.Constants#MODEL_NOT_SELECTED}</li>
     *   <li>Service issues → Friendly "service overwhelmed" message</li>
     *   <li>Processing errors → "We goofed up" message</li>
     * </ul>
     *
     * @param prompt The text prompt to send to the AI model
     * @param withImage {@code true} if the request includes image processing (selects vision model),
     *                  {@code false} for text-only processing
     * @return The AI's response text, or a user-friendly error message if the request fails
     * @throws IllegalArgumentException if API key or model is missing (caught and converted to friendly message)
     * @see #execute(String, String, String, boolean) for the underlying execution logic
     * @see #getModelName(String, boolean) for model selection logic
     */
    public String queryModel(String prompt, boolean withImage) {
        try {
            if (apiKey.get() == null || apiKey.get().isBlank()) {
                throw new IllegalArgumentException(API_KEY_MISSING);
            }
            if (modelName.get() == null || modelName.get().isBlank()) {
                throw new IllegalArgumentException(MODEL_NOT_SELECTED);
            }
            long startTime = System.currentTimeMillis();
            ChatGPTResponse response = execute(prompt, apiKey.get(), modelName.get(), withImage);
            log.debug("Time taken execute : {} ms", (System.currentTimeMillis() - startTime));
            if (response == null) {
                log.error("Null response to prompt: {}", LoggingUtils.truncateForLogging(prompt));
                return "Sorry. We goofed up while processing that request. Please try again. If this continues, please contact support.";
            } else {
                log.debug("Answer from Open AI: {}", LoggingUtils.truncateForLogging(response.getAnswer()));
                return response.getAnswer();
            }
        } catch (IOException ioe) {
            log.error("Received the following error: {}", ioe.getMessage());
            log.error("Stacktrace: {}", Arrays.toString(ioe.getStackTrace()));
            return "Sorry. Our AI Service is overwhelmed with requests right now. Please try again soon.";
        }
    }

    /**
     * Initializes the shared HTTP client if not already created.
     *
     * <p>This method ensures thread-safe lazy initialization of the OkHttpClient instance
     * used for all OpenAI API requests. The client is configured with appropriate timeouts
     * for reliable communication with the OpenAI service.</p>
     *
     * <h3>Client Configuration:</h3>
     * <ul>
     *   <li><strong>Connect Timeout:</strong> 10 seconds</li>
     *   <li><strong>Read Timeout:</strong> 60 seconds</li>
     *   <li><strong>Thread Safety:</strong> Synchronized for safe concurrent access</li>
     * </ul>
     *
     * <p>The extended read timeout accommodates OpenAI's processing time for complex
     * queries, especially those involving image analysis or large context windows.</p>
     *
     * @see OkHttpClient for HTTP client documentation
     */
    synchronized static private void confirmHttpClient() {

        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
            client = builder.build();
        }
    }

    /**
     * Executes a raw JSON request against the OpenAI Chat Completions API.
     *
     * <p>This is the core method that handles direct communication with OpenAI's API.
     * It processes the raw JSON request, adds model parameters, handles authentication,
     * implements retry logic, and processes the response. This method is used by all
     * higher-level query methods.</p>
     *
     * <h3>Request Processing:</h3>
     * <ol>
     *   <li>Adds model-specific parameters to the JSON request</li>
     *   <li>Initializes HTTP client if needed</li>
     *   <li>Attempts request up to 3 times with exponential backoff</li>
     *   <li>Handles rate limiting (HTTP 429) with automatic retry</li>
     *   <li>Parses and validates the response</li>
     * </ol>
     *
     * <h3>Error Handling:</h3>
     * <ul>
     *   <li><strong>Rate Limiting (429):</strong> Automatic pause and retry</li>
     *   <li><strong>Non-200 Status:</strong> Extracts error message from response</li>
     *   <li><strong>Empty Response:</strong> Returns null for graceful handling</li>
     *   <li><strong>Network Issues:</strong> Retries up to 3 times with brief pauses</li>
     * </ul>
     *
     * <h3>Response Validation:</h3>
     * <p>The method validates that the response contains valid choices and logs
     * warnings for unexpected response structures (multiple choices, empty choices).</p>
     *
     * @param rawDocumentJson The JSON request payload (messages, etc.)
     * @param apiKey The OpenAI API key for authentication
     * @param modelName The display name of the model to use
     * @param withImage {@code true} if request includes image processing
     * @return The parsed ChatGPT response object, or {@code null} if no valid response
     * @throws IOException if all retry attempts fail or for unrecoverable errors
     * @throws IllegalArgumentException if the API returns an error response
     * @see #addModelParameters(String, String, boolean) for parameter injection
     * @see #briefPause() for retry delay logic
     */
    public ChatGPTResponse execute(String rawDocumentJson, String apiKey, String modelName, boolean withImage) throws IOException {

        rawDocumentJson = addModelParameters(rawDocumentJson, modelName, withImage);
        confirmHttpClient();

        IOException caughtError = null;
        for (int i = 1; i < 4; i++) {
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, rawDocumentJson);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(V1_CHAT_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("api-key", apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .post(body)
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                String responseJson = responseBody == null ? null : responseBody.string();
                response.close();

                if (response.code() == 429) {

                    briefPause();
                    throw new IOException("Rate limit exceeded, we'll slow down right here and try again.");
                }

                if (response.code() != 200) {
                    log.error("RESPONSE code from OpenAI: {}", response.code());
                    if (StringI18N.isEmpty(responseJson)) {
                        log.error("No response body from OpenAI");
                    } else {
                        log.error("OpenAI body: {}", LoggingUtils.truncateForLogging(responseJson));
                    }
                    JsonObject chat = new Gson().fromJson(responseJson, JsonObject.class);
                    String errorMessage = chat.get("error").getAsJsonObject().get("message").toString();
                    throw new IllegalArgumentException(errorMessage);
                }

                if (StringI18N.isEmpty(responseJson)) {
                    log.error("Empty response body from ChatGPT");
                    return null;
                }
                ChatGPTResponse chat = new Gson().fromJson(responseJson, ChatGPTResponse.class);
                if (chat.getChoices() == null || chat.getChoices().isEmpty()) {
                    log.info("Got no response choice from ChatGPT!");
                    return null;
                }
                if (chat.getChoices().size() > 1) {
                    log.info("We received > 1 choice but only replying with the first");
                }
                return chat;

            } catch (IOException e) {

                caughtError = e;
                log.error("ChatGPT error, attempt {} of 3: {}", i, e.getMessage());
                briefPause();
            }
        }

        throw caughtError;

    }

    /**
     * Adds model-specific parameters to a JSON request payload.
     *
     * <p>This method enhances the base JSON request with OpenAI-specific parameters
     * including model selection, token limits, and generation settings. It uses
     * predefined precise settings optimized for consistent, focused responses.</p>
     *
     * <h3>Added Parameters:</h3>
     * <ul>
     *   <li><strong>model:</strong> Resolved from display name via {@link #getModelName(String, boolean)}</li>
     *   <li><strong>temperature:</strong> {@value #PRECISE_TEMP} for focused responses</li>
     *   <li><strong>top_p:</strong> {@value #PRECISE_TOPP} for nucleus sampling</li>
     *   <li><strong>max_tokens:</strong> Dynamic based on input length and model capabilities</li>
     * </ul>
     *
     * <h3>Token Calculation Logic:</h3>
     * <ul>
     *   <li>Estimates input tokens as {@code inputLength / 3}</li>
     *   <li>If input < {@value #BALANCED_INPUT_OUT_TOKENS}, allocates {@value #BALANCED_INPUT_OUT_TOKENS} for response</li>
     *   <li>For larger inputs: GPT-4 Latest gets 16,000 tokens, others get {@value #MAX_RESPONSE_TOKENS}</li>
     * </ul>
     *
     * @param rawDocumentJson The base JSON request containing messages
     * @param modelname The user-selected model display name
     * @param withImage {@code true} if request includes image processing
     * @return Enhanced JSON string with model parameters added
     * @throws IllegalArgumentException if the input JSON is malformed
     * @see #getModelName(String, boolean) for model resolution
     */
    private String addModelParameters(String rawDocumentJson, String modelname, boolean withImage) {
        try {
            JsonObject json = new Gson().fromJson(rawDocumentJson, JsonObject.class);
            final String resolvedModelName = getModelName(modelname, withImage);
            json.addProperty("model", resolvedModelName);
            int tokensUsed = rawDocumentJson.length() / 3;
            int responseTokens;
            if (tokensUsed < BALANCED_INPUT_OUT_TOKENS)
                responseTokens = BALANCED_INPUT_OUT_TOKENS;
            else
                responseTokens = resolvedModelName.equalsIgnoreCase(GPT_4_LATEST) ? 16000 : MAX_RESPONSE_TOKENS;

            json.addProperty("temperature", PRECISE_TEMP);
            json.addProperty("top_p", PRECISE_TOPP);
            json.addProperty("max_tokens", responseTokens);
            return json.toString();
        } catch (JsonSyntaxException cause) {
            throw new IllegalArgumentException("Input prompt was not in the correct format!");
        }
    }

    /**
     * Introduces a brief pause to handle rate limiting and retry scenarios.
     *
     * <p>This utility method implements a 250ms delay used in retry logic and
     * rate limiting scenarios. It helps prevent overwhelming the OpenAI API
     * and provides a reasonable backoff period between failed requests.</p>
     *
     * <h3>Usage Scenarios:</h3>
     * <ul>
     *   <li>After receiving HTTP 429 (rate limit exceeded)</li>
     *   <li>Between retry attempts for failed requests</li>
     *   <li>General backoff strategy for API communication</li>
     * </ul>
     *
     * <p>The method gracefully handles thread interruption by ignoring
     * {@link InterruptedException}, allowing the calling thread to continue
     * execution without disruption.</p>
     *
     * @see #execute(String, String, String, boolean) for usage in retry logic
     */
    public static void briefPause() {
        try {
            Thread.sleep(250L);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Retrieves the display name of the currently configured AI model.
     * <p>
     * This method returns exactly what the user selected from the @Field.PickOne annotation.
     * </p>
     *
     * <p>Supported models:</p>
     * <ul>
     *   <li>"ChatGPT 4"</li>
     *   <li>"ChatGPT 3.5"</li>
     *   <li>"ChatGPT 4 Latest"</li>
     *   <li>"ChatGPT 4.1 Mini"</li>
     *   <li>"ChatGPT 4.1 Nano"</li>
     *   <li>"ChatGPT 4.1"</li>
     * </ul>
     *
     * @return The exact model name selected by the user. Returns
     *         "ChatGPT 3.5" as default if model is null, blank, or not recognized.
     * @see OpenAIConstants for model identifier constants
     */
    public String getAIModel() {
        return getAIModelDisplayName(modelName.get());
    }

    /**
     * Returns the exact model name that matches the user's selection.
     * <p>
     * This method returns the model name exactly as defined in OpenAIConstants,
     * matching what the user selected from the @Field.PickOne dropdown.
     * </p>
     *
     * @param model The model identifier (e.g., "ChatGPT 4", "ChatGPT 3.5")
     * @return The exact model name as selected by the user, or "ChatGPT 3.5" as default
     */
    public String getAIModelDisplayName(String model) {
        // Handle null or blank model name
        if (model == null || model.isBlank()) {
            log.warn("Model name is null or blank, returning default model");
            return OpenAIConstants.GPT_35;
        }

        // Return exactly what the user picked from @Field.PickOne
        switch (model) {
            case OpenAIConstants.GPT_4 -> {
                return OpenAIConstants.GPT_4;
            }
            case OpenAIConstants.GPT_35 -> {
                return OpenAIConstants.GPT_35;
            }
            case OpenAIConstants.GPT_LATEST -> {
                return OpenAIConstants.GPT_LATEST;
            }
            case OpenAIConstants.GPT_4_1_MINI -> {
                return OpenAIConstants.GPT_4_1_MINI;
            }
            case OpenAIConstants.GPT_4_1_NANO -> {
                return OpenAIConstants.GPT_4_1_NANO;
            }
            case OpenAIConstants.GPT_4_1 -> {
                return OpenAIConstants.GPT_4_1;
            }
        }

        // Default to GPT 3.5 for unrecognized models
        log.warn("Unknown model type: {}, returning default model", model);
        return OpenAIConstants.GPT_35;
    }

    /**
     * Constructs a text-only prompt with optional context and queries the AI model.
     *
     * <p>This convenience method combines query construction and model execution for
     * text-based interactions. It formats the user query with optional context
     * information and sends it to the configured AI model using standard parameters.</p>
     *
     * <h3>Prompt Structure:</h3>
     * <pre>
     * Please answer the following query asked by the user: [query]
     *
     * Use the following context to answer the above query: [context]
     * </pre>
     *
     * <h3>Features:</h3>
     * <ul>
     *   <li>Automatic prompt formatting with clear instructions</li>
     *   <li>Optional context integration for enhanced responses</li>
     *   <li>Uses precise model parameters for consistent results</li>
     *   <li>Text-only processing (no image support)</li>
     * </ul>
     *
     * @param query The user's question or request
     * @param context Optional context information to help answer the query.
     *                Can be {@code null} or blank to omit context section.
     * @return The AI model's response text, or error message if request fails
     * @see #getFinalQuery(String, String) for prompt construction logic
     * @see #queryModel(String, boolean) for execution details
     */
    public String constructPromptAndQueryModel(String query, String context) {
        String finalQuery = getFinalQuery(query, context);
        JsonObject finalPrompt = new JsonObject();
        JsonArray messages = new JsonArray();
        JsonObject baseMessage = new JsonObject();
        baseMessage.addProperty("role", "user");
        baseMessage.addProperty("content", finalQuery);
        messages.add(baseMessage);
        finalPrompt.add("messages", messages);
        return queryModel(finalPrompt.toString(), false);
    }

    /**
     * Constructs a multimodal prompt with text and base64-encoded image for AI analysis.
     *
     * <p>This method enables vision-capable AI models to analyze images alongside text queries.
     * It constructs a properly formatted multimodal request that includes both textual
     * instructions and image data, automatically selecting a vision-enabled model.</p>
     *
     * <h3>Request Structure:</h3>
     * <ul>
     *   <li><strong>Text Component:</strong> Combined query and context via {@link #getFinalQuery(String, String)}</li>
     *   <li><strong>Image Component:</strong> Base64-encoded image with JPEG MIME type</li>
     *   <li><strong>Model Selection:</strong> Automatically uses vision-capable model</li>
     * </ul>
     *
     * <h3>Image Format:</h3>
     * <p>The image is embedded as a data URL: {@code data:image/jpeg;base64,[encodedData]}</p>
     *
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Image description and analysis</li>
     *   <li>Visual question answering</li>
     *   <li>Document/chart interpretation</li>
     *   <li>Object detection and recognition</li>
     * </ul>
     *
     * @param query The text query about the image
     * @param context Optional context to guide the analysis
     * @param base64EncodedImage Base64-encoded image data (without data URL prefix)
     * @return The AI model's analysis response, or error message if request fails
     * @see #getFinalQuery(String, String) for text prompt construction
     * @see #queryModel(String, boolean) for execution with vision model
     */
    public String constructPromptAndQueryModelForImageString(String query, String context, String base64EncodedImage) {
        JsonObject finalPrompt = new JsonObject();
        JsonArray messages = new JsonArray();
        JsonObject baseMessage = new JsonObject();
        baseMessage.addProperty("role", "user");
        JsonArray finalQuery = new JsonArray();
        JsonObject textQuery = new JsonObject();
        textQuery.addProperty("type", "text");
        textQuery.addProperty("text", getFinalQuery(query, context));
        finalQuery.add(textQuery);
        JsonObject imageobj = new JsonObject();
        imageobj.addProperty("type", "image_url");
        JsonObject imageUrl = new JsonObject();
        imageUrl.addProperty("url", "data:image/jpeg;base64," + base64EncodedImage);
        imageobj.add("image_url", imageUrl);
        finalQuery.add(imageobj);
        baseMessage.add("content", finalQuery);
        messages.add(baseMessage);
        finalPrompt.add("messages", messages);
        return queryModel(finalPrompt.toString(), true);
    }

    /**
     * Constructs a multimodal prompt with text and image file for AI analysis.
     *
     * <p>This convenience method handles file I/O operations to read an image file,
     * encode it to base64, and send it for AI analysis. It provides a simple interface
     * for image analysis without requiring manual base64 encoding.</p>
     *
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Reads the entire image file into memory</li>
     *   <li>Encodes the image data as base64</li>
     *   <li>Delegates to {@link #constructPromptAndQueryModelForImageString(String, String, String)}</li>
     * </ol>
     *
     * <h3>Supported Formats:</h3>
     * <p>Supports any image format that the underlying vision model can process,
     * typically including JPEG, PNG, GIF, and WebP.</p>
     *
     * <h3>Memory Considerations:</h3>
     * <p>The entire image file is loaded into memory for encoding. For large images,
     * consider using the base64 string method with streaming encoding if memory
     * usage is a concern.</p>
     *
     * @param query The text query about the image
     * @param context Optional context to guide the analysis
     * @param image The image file to analyze
     * @return The AI model's analysis response, or error message if request fails
     * @throws RuntimeException if the image file cannot be read
     * @see #constructPromptAndQueryModelForImageString(String, String, String) for the underlying implementation
     */
    public String constructPromptAndQueryModelForImageFile(String query, String context, File image) {
        try (InputStream imageStream = new FileInputStream(image)) {
            byte[] imageBytes = new byte[(int) image.length()];
            imageStream.read(imageBytes);
            String encodedString = Base64.getEncoder().encodeToString(imageBytes);
            return constructPromptAndQueryModelForImageString(query, context, encodedString);
        } catch (IOException e) {
            log.error("Failed to read file: " + e.getMessage());
            throw new RuntimeException("Failed to read file");
        }
    }

    /**
     * Constructs a well-formatted prompt by combining user query with optional context.
     *
     * <p>This method creates a structured prompt that clearly separates the user's
     * question from any supporting context information. The format helps the AI
     * model understand the task and utilize the provided context effectively.</p>
     *
     * <h3>Output Format:</h3>
     * <pre>
     * Please answer the following query asked by the user: [query]
     *
     * Use the following context to answer the above query: [context]
     * </pre>
     *
     * <h3>Context Handling:</h3>
     * <ul>
     *   <li>If context is {@code null} or blank, only the query instruction is included</li>
     *   <li>If context is provided, it's appended with clear separation</li>
     *   <li>The format ensures the AI understands the relationship between query and context</li>
     * </ul>
     *
     * @param query The user's question or request (required)
     * @param context Optional supporting information. If {@code null} or blank,
     *                the context section is omitted entirely.
     * @return A well-formatted prompt string ready for AI processing
     */
    private String getFinalQuery(String query, String context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please answer the following query asked by the user: ");
        sb.append(query);
        if (context != null && !context.isBlank()) {
            sb.append("\n \n");
            sb.append("Use the following context to answer the above query:");
            sb.append(context);
        }
        return sb.toString();

    }

    /**
     * Constructs a multimodal prompt with text and multiple images for AI analysis with custom parameters.
     *
     * <p>This advanced method enables analysis of multiple images simultaneously with
     * customizable model parameters. Each image is labeled with its filename and
     * processed with automatic MIME type detection for optimal compatibility.</p>
     *
     * <h3>Input Format:</h3>
     * <p>Each map in the list should contain:</p>
     * <ul>
     *   <li><strong>"File Name":</strong> String identifier for the image</li>
     *   <li><strong>"Base 64 Encoded Image String":</strong> Base64 image data (with or without data URL prefix)</li>
     * </ul>
     *
     * <h3>Content Structure:</h3>
     * <ol>
     *   <li>Combined query and context text</li>
     *   <li>For each image: filename label followed by the image data</li>
     *   <li>Automatic MIME type detection (JPEG, PNG, GIF, WebP)</li>
     * </ol>
     *
     * <h3>MIME Type Detection:</h3>
     * <p>Automatically detects image format from binary headers:</p>
     * <ul>
     *   <li>JPEG: FF D8 signature</li>
     *   <li>PNG: 89 50 4E 47 signature</li>
     *   <li>GIF: 47 49 46 signature</li>
     *   <li>WebP: WEBP in header</li>
     * </ul>
     *
     * @param query The text query about the images
     * @param context Optional context to guide the analysis
     * @param base64ImagesByFilename List of maps containing filename and base64 image data
     * @param temperature Model creativity (0.0-1.0), or {@code null} for default
     * @param topK Top-K sampling parameter (ignored by OpenAI API)
     * @param topP Nucleus sampling (0.0-1.0), or {@code null} for default
     * @param maxTokens Maximum response tokens, or {@code null} for automatic calculation
     * @return The AI model's analysis response, or error message if request fails
     * @throws IllegalArgumentException if any image has unsupported format
     * @see #stripBase64Prefix(String) for base64 cleaning
     * @see #detectMimeType(String) for format detection
     * @see #queryModelWithSettings(String, Double, Double, Double, Double, boolean) for execution
     */
    public String constructPromptAndQueryModelForImageMap(String query, String context, List<Map<String, Object>> base64ImagesByFilename, Double temperature, Double topK, Double topP, Double maxTokens) {
        JsonObject finalPrompt = new JsonObject();
        JsonArray messages = new JsonArray();
        JsonObject baseMessage = new JsonObject();
        baseMessage.addProperty("role", "user");
        JsonArray contentArray = new JsonArray();
        // Add combined query and context first
        JsonObject queryText = new JsonObject();
        queryText.addProperty("type", "text");
        queryText.addProperty("text", getFinalQuery(query, context));
        contentArray.add(queryText);

        // Add image name and image itself
        for (Map<String, Object> imageEntry : base64ImagesByFilename) {
            String fileName = (String) imageEntry.get("File Name");
            String base64 = (String) imageEntry.get("Base 64 Encoded Image String");
            base64 = stripBase64Prefix(base64);
            String mimeType = detectMimeType(base64);
            if (mimeType == null) {
                throw new IllegalArgumentException("Unsupported or invalid image: " + fileName);
            }

            // Add filename
            JsonObject fileLabel = new JsonObject();
            fileLabel.addProperty("type", "text");
            fileLabel.addProperty("text", "Image: " + fileName);
            contentArray.add(fileLabel);

            // Add image
            JsonObject imageObj = new JsonObject();
            imageObj.addProperty("type", "image_url");

            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", "data:" + mimeType + ";base64," + base64);

            imageObj.add("image_url", imageUrl);
            contentArray.add(imageObj);
        }

        baseMessage.add("content", contentArray);
        messages.add(baseMessage);
        finalPrompt.add("messages", messages);
        return queryModelWithSettings(finalPrompt.toString(), temperature, topK, topP, maxTokens, false);
    }

    /**
     * Removes data URL prefix from base64-encoded image strings.
     *
     * <p>This utility method cleans base64 image data that may include data URL
     * prefixes (e.g., "data:image/jpeg;base64,"). It extracts only the pure
     * base64-encoded content needed for API requests.</p>
     *
     * <h3>Supported Prefixes:</h3>
     * <ul>
     *   <li>{@code data:image/jpeg;base64,}</li>
     *   <li>{@code data:image/png;base64,}</li>
     *   <li>Any string containing "base64," marker</li>
     * </ul>
     *
     * <h3>Examples:</h3>
     * <pre>
     * stripBase64Prefix("data:image/jpeg;base64,/9j/4AAQ...") → "/9j/4AAQ..."
     * stripBase64Prefix("/9j/4AAQ...")                        → "/9j/4AAQ..."
     * stripBase64Prefix(null)                                 → null
     * </pre>
     *
     * @param base64WithPrefix Base64 string that may contain data URL prefix
     * @return Pure base64 content without prefix, or {@code null} if input is {@code null}
     */
    public String stripBase64Prefix(String base64WithPrefix) {
        if (base64WithPrefix == null) {
            return null;
        }
        int base64StartIndex = base64WithPrefix.indexOf("base64,");
        if (base64StartIndex != -1) {
            return base64WithPrefix.substring(base64StartIndex + 7);
        }
        return base64WithPrefix;
    }

    /**
     * Detects the MIME type of an image from its base64-encoded binary data.
     *
     * <p>This method analyzes the binary signature (magic bytes) at the beginning
     * of image files to determine their format. It supports the most common
     * image formats used in web applications and AI processing.</p>
     *
     * <h3>Supported Formats:</h3>
     * <table border="1">
     *   <tr><th>Format</th><th>MIME Type</th><th>Signature</th></tr>
     *   <tr><td>JPEG</td><td>image/jpeg</td><td>FF D8</td></tr>
     *   <tr><td>PNG</td><td>image/png</td><td>89 50 4E 47</td></tr>
     *   <tr><td>GIF</td><td>image/gif</td><td>47 49 46</td></tr>
     *   <tr><td>WebP</td><td>image/webp</td><td>WEBP at offset 8</td></tr>
     * </table>
     *
     * <h3>Detection Process:</h3>
     * <ol>
     *   <li>Decodes base64 string to binary data</li>
     *   <li>Examines first 4-12 bytes for format signatures</li>
     *   <li>Returns appropriate MIME type or {@code null} if unrecognized</li>
     * </ol>
     *
     * <h3>Error Handling:</h3>
     * <p>Returns {@code null} for:</p>
     * <ul>
     *   <li>Invalid base64 encoding</li>
     *   <li>Insufficient data for signature detection</li>
     *   <li>Unrecognized image formats</li>
     * </ul>
     *
     * @param base64 Base64-encoded image data (without data URL prefix)
     * @return MIME type string (e.g., "image/jpeg") or {@code null} if format cannot be determined
     */
    private String detectMimeType(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes.length >= 4) {
                if ((bytes[0] & 0xFF) == 0x89 && (bytes[1] & 0xFF) == 0x50 &&
                        (bytes[2] & 0xFF) == 0x4E && (bytes[3] & 0xFF) == 0x47) {
                    return "image/png";
                }
                if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) {
                    return "image/jpeg";
                }
                if ((bytes[0] & 0xFF) == 0x47 && (bytes[1] & 0xFF) == 0x49 &&
                        (bytes[2] & 0xFF) == 0x46) {
                    return "image/gif";
                }
                if (bytes.length >= 12) {
                    if ((bytes[8] & 0xFF) == 0x57 && (bytes[9] & 0xFF) == 0x45 &&
                            (bytes[10] & 0xFF) == 0x42 && (bytes[11] & 0xFF) == 0x50) {
                        return "image/webp";
                    }
                }
            }
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        return null;
    }

    /**
     * Constructs and executes a prompt with custom system instructions and model parameters.
     *
     * <p>This method provides fine-grained control over AI behavior by allowing custom
     * system prompts and model parameters. System prompts define the AI's role and
     * behavior, while parameters control response characteristics.</p>
     *
     * <h3>Message Structure:</h3>
     * <ol>
     *   <li><strong>System Message:</strong> Sets AI behavior and context (if provided)</li>
     *   <li><strong>User Message:</strong> The actual query or request</li>
     * </ol>
     *
     * <h3>Parameter Effects:</h3>
     * <ul>
     *   <li><strong>temperature:</strong> Controls randomness (0.0=deterministic, 1.0=creative)</li>
     *   <li><strong>topP:</strong> Nucleus sampling threshold (0.0-1.0)</li>
     *   <li><strong>maxTokens:</strong> Maximum response length</li>
     *   <li><strong>topK:</strong> Ignored by OpenAI API (kept for compatibility)</li>
     * </ul>
     *
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Role-playing scenarios (e.g., "You are a helpful teacher")</li>
     *   <li>Specialized tasks (e.g., "You are a code reviewer")</li>
     *   <li>Creative writing with high temperature</li>
     *   <li>Precise analysis with low temperature</li>
     * </ul>
     *
     * @param userPrompt The user's query or request
     * @param systemPrompt Optional system message defining AI behavior.
     *                     If {@code null} or blank, no system message is included.
     * @param temperature Response creativity (0.0-1.0), or {@code null} for default
     * @param topK Top-K sampling (ignored by OpenAI), or {@code null}
     * @param topP Nucleus sampling (0.0-1.0), or {@code null} for default
     * @param maxTokens Maximum response tokens, or {@code null} for automatic calculation
     * @return The AI model's response, or error message if request fails
     * @see #queryModelWithSettings(String, Double, Double, Double, Double, boolean) for execution
     */
    public String constructPromptAndQueryModelWithSettings(String userPrompt, String systemPrompt,
                                                          Double temperature, Double topK, Double topP,
                                                          Double maxTokens) {
        JsonObject finalPrompt = new JsonObject();
        JsonArray messages = new JsonArray();

        // Add system prompt if provided
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", systemPrompt);
            messages.add(systemMessage);
        }

        // Add user prompt
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);

        finalPrompt.add("messages", messages);

        return queryModelWithSettings(finalPrompt.toString(), temperature, topK, topP, maxTokens, false);
    }

    /**
     * Executes a query with custom model parameters and optional image support.
     *
     * <p>This method provides the most flexible interface for AI queries, allowing
     * fine-tuned control over model behavior through custom parameters. It supports
     * both text-only and multimodal (text + image) requests.</p>
     *
     * <h3>Parameter Behavior:</h3>
     * <ul>
     *   <li><strong>temperature:</strong> Controls response randomness
     *     <ul>
     *       <li>0.0-0.3: Precise, consistent responses</li>
     *       <li>0.4-0.7: Balanced creativity and consistency</li>
     *       <li>0.8-1.0: Creative, varied responses</li>
     *     </ul>
     *   </li>
     *   <li><strong>topP:</strong> Nucleus sampling for response diversity</li>
     *   <li><strong>maxTokens:</strong> Response length limit</li>
     *   <li><strong>topK:</strong> Ignored by OpenAI API</li>
     * </ul>
     *
     * <h3>Model Selection:</h3>
     * <p>Automatically selects appropriate model based on {@code withImage} parameter:</p>
     * <ul>
     *   <li>{@code withImage=true}: Uses vision-capable model</li>
     *   <li>{@code withImage=false}: Uses standard text model</li>
     * </ul>
     *
     * @param prompt The JSON-formatted prompt (messages array)
     * @param temperature Response creativity (0.0-1.0), or {@code null} for default {@value #PRECISE_TEMP}
     * @param topK Top-K sampling parameter (ignored by OpenAI API)
     * @param topP Nucleus sampling (0.0-1.0), or {@code null} for default {@value #PRECISE_TOPP}
     * @param maxTokens Maximum response tokens, or {@code null} for automatic calculation
     * @param withImage {@code true} for vision model, {@code false} for text-only model
     * @return The AI model's response text, or user-friendly error message if request fails
     * @see #executeWithSettings(String, String, String, Double, Double, Double, Double, boolean) for execution logic
     */
    public String queryModelWithSettings(String prompt, Double temperature, Double topK, Double topP,
                                       Double maxTokens, boolean withImage) {
        try {
            if (apiKey.get() == null || apiKey.get().isBlank()) {
                throw new IllegalArgumentException(API_KEY_MISSING);
            }
            if (modelName.get() == null || modelName.get().isBlank()) {
                throw new IllegalArgumentException(MODEL_NOT_SELECTED);
            }
            long startTime = System.currentTimeMillis();
            ChatGPTResponse response = executeWithSettings(prompt, apiKey.get(), modelName.get(),
                                                         temperature, topK, topP, maxTokens, withImage);
            log.debug("Time taken execute : {} ms", (System.currentTimeMillis() - startTime));
            if (response == null) {
                log.error("Null response to prompt: {}", LoggingUtils.truncateForLogging(prompt));
                return REQUEST_PROCESSING_ERROR;
            } else {
                log.debug("Answer from Open AI: {}", LoggingUtils.truncateForLogging(response.getAnswer()));
                return response.getAnswer();
            }
        } catch (IOException ioe) {
            log.error("Received the following error: {}", ioe.getMessage());
            log.debug("Stacktrace: {}", Arrays.toString(ioe.getStackTrace()));
            return API_SERVICE_UNAVAILABLE;
        }
    }

    /**
     * Executes a request with custom parameters against the OpenAI API.
     *
     * <p>This is the core execution method for requests with custom model parameters.
     * It enhances the base JSON request with the specified parameters and handles
     * the complete request lifecycle including retries and error handling.</p>
     *
     * <h3>Parameter Processing:</h3>
     * <p>Custom parameters are applied via {@link #addModelParametersWithSettings(String, String, Double, Double, Double, Double, boolean)}:</p>
     * <ul>
     *   <li>Non-null parameters override defaults</li>
     *   <li>Null parameters fall back to predefined values</li>
     *   <li>Token calculation considers input length and model capabilities</li>
     * </ul>
     *
     * <h3>Execution Logic:</h3>
     * <p>Identical to {@link #execute(String, String, String, boolean)} but with
     * custom parameter injection. Includes the same retry logic, error handling,
     * and response validation.</p>
     *
     * @param rawDocumentJson The JSON request payload (messages, etc.)
     * @param apiKey The OpenAI API key for authentication
     * @param modelName The display name of the model to use
     * @param temperature Response creativity (0.0-1.0), or {@code null} for default
     * @param topK Top-K sampling (ignored by OpenAI API)
     * @param topP Nucleus sampling (0.0-1.0), or {@code null} for default
     * @param maxTokens Maximum response tokens, or {@code null} for automatic calculation
     * @param withImage {@code true} if request includes image processing
     * @return The parsed ChatGPT response object, or {@code null} if no valid response
     * @throws IOException if all retry attempts fail or for unrecoverable errors
     * @throws IllegalArgumentException if the API returns an error response
     * @see #addModelParametersWithSettings(String, String, Double, Double, Double, Double, boolean) for parameter injection
     */
    public ChatGPTResponse executeWithSettings(String rawDocumentJson, String apiKey, String modelName,
                                             Double temperature, Double topK, Double topP, Double maxTokens,
                                             boolean withImage) throws IOException {

        rawDocumentJson = addModelParametersWithSettings(rawDocumentJson, modelName, temperature,
                                                        topK, topP, maxTokens, withImage);
        confirmHttpClient();

        IOException caughtError = null;
        for (int i = 1; i < 4; i++) {
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, rawDocumentJson);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(V1_CHAT_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("api-key", apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .post(body)
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                String responseJson = responseBody == null ? null : responseBody.string();
                response.close();

                if (response.code() == 429) {
                    briefPause();
                    throw new IOException("Rate limit exceeded, we'll slow down right here and try again.");
                }

                if (response.code() != 200) {
                    log.error("RESPONSE code from OpenAI: {}", response.code());
                    if (StringI18N.isEmpty(responseJson)) {
                        log.error("No response body from OpenAI");
                    } else {
                        log.error("OpenAI body: {}", LoggingUtils.truncateForLogging(responseJson));
                    }
                    JsonObject chat = new Gson().fromJson(responseJson, JsonObject.class);
                    String errorMessage = chat.get("error").getAsJsonObject().get("message").toString();
                    throw new IllegalArgumentException(errorMessage);
                }

                if (StringI18N.isEmpty(responseJson)) {
                    log.error("Empty response body from ChatGPT");
                    return null;
                }
                ChatGPTResponse chat = new Gson().fromJson(responseJson, ChatGPTResponse.class);
                if (chat.getChoices() == null || chat.getChoices().isEmpty()) {
                    log.info("Got no response choice from ChatGPT!");
                    return null;
                }
                if (chat.getChoices().size() > 1) {
                    log.info("We received > 1 choice but only replying with the first");
                }
                return chat;

            } catch (IOException e) {
                caughtError = e;
                log.error("ChatGPT error, attempt {} of 3: {}", i, e.getMessage());
                briefPause();
            }
        }

        throw caughtError;
    }

    /**
     * Adds model-specific parameters with custom settings to a JSON request payload.
     *
     * <p>This method enhances the base JSON request with both model identification
     * and custom generation parameters. It provides flexible parameter handling
     * with intelligent defaults and automatic token calculation.</p>
     *
     * <h3>Parameter Handling:</h3>
     * <ul>
     *   <li><strong>temperature:</strong> Uses provided value or falls back to {@value #PRECISE_TEMP}</li>
     *   <li><strong>top_p:</strong> Uses provided value or falls back to {@value #PRECISE_TOPP}</li>
     *   <li><strong>max_tokens:</strong> Uses provided value or calculates based on input length</li>
     *   <li><strong>top_k:</strong> Ignored (OpenAI API doesn't support this parameter)</li>
     * </ul>
     *
     * <h3>Token Calculation:</h3>
     * <p>When maxTokens is null, automatic calculation:</p>
     * <ul>
     *   <li>Estimates input tokens as {@code inputLength / 3}</li>
     *   <li>If input < {@value #BALANCED_INPUT_OUT_TOKENS}: allocates {@value #BALANCED_INPUT_OUT_TOKENS}</li>
     *   <li>For larger inputs: GPT-4 Latest gets 16,000, others get {@value #MAX_RESPONSE_TOKENS}</li>
     * </ul>
     *
     * <h3>API Compatibility:</h3>
     * <p>The method maintains compatibility with different LLM providers by accepting
     * top_k parameter but ignoring it for OpenAI API calls, which don't support this parameter.</p>
     *
     * @param rawDocumentJson The base JSON request containing messages
     * @param modelname The user-selected model display name
     * @param temperature Response creativity (0.0-1.0), or {@code null} for default {@value #PRECISE_TEMP}
     * @param topK Top-K sampling parameter (ignored for OpenAI compatibility)
     * @param topP Nucleus sampling (0.0-1.0), or {@code null} for default {@value #PRECISE_TOPP}
     * @param maxTokens Maximum response tokens, or {@code null} for automatic calculation
     * @param withImage {@code true} if request includes image processing
     * @return Enhanced JSON string with model parameters added
     * @throws IllegalArgumentException if the input JSON is malformed
     * @see #getModelName(String, boolean) for model resolution
     */
    private String addModelParametersWithSettings(String rawDocumentJson, String modelname,
                                                 Double temperature, Double topK, Double topP,
                                                 Double maxTokens, boolean withImage) {
        try {
            JsonObject json = new Gson().fromJson(rawDocumentJson, JsonObject.class);
            final String resolvedModelName = getModelName(modelname, withImage);
            json.addProperty("model", resolvedModelName);

            // Use provided parameters or fall back to defaults
            json.addProperty("temperature", Objects.requireNonNullElse(temperature, PRECISE_TEMP));

            json.addProperty("top_p", Objects.requireNonNullElse(topP, PRECISE_TOPP));

            if (maxTokens != null) {
                json.addProperty("max_tokens", maxTokens.intValue());
            } else {
                int tokensUsed = rawDocumentJson.length() / 3;
                int responseTokens;
                if (tokensUsed < BALANCED_INPUT_OUT_TOKENS)
                    responseTokens = BALANCED_INPUT_OUT_TOKENS;
                else
                    responseTokens = resolvedModelName.equalsIgnoreCase(GPT_4_LATEST) ? 16000 : MAX_RESPONSE_TOKENS;
                json.addProperty("max_tokens", responseTokens);
            }

            // Note: OpenAI API doesn't support top_k parameter, so we'll ignore it
            // but keep the parameter for compatibility with other LLM providers

            return json.toString();
        } catch (JsonSyntaxException cause) {
            throw new IllegalArgumentException("Input prompt was not in the correct format!");
        }
    }

}
