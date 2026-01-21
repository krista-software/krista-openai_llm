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

package app.krista.extensions.krista.llms.openai_qa.util;

import app.krista.extension.executor.ExtensionResponse;

import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.API_KEY_MISSING;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.MODEL_NOT_SELECTED;

/**
 * Utility class providing common validation and error handling functions for the OpenAI extension.
 *
 * <p>This utility class centralizes validation logic and error response creation to ensure
 * consistent behavior across all catalog requests. It provides standardized validation
 * for required extension attributes and creates properly formatted error responses.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Attribute Validation:</strong> Validates API key and model selection</li>
 *   <li><strong>Error Response Creation:</strong> Creates standardized error responses</li>
 *   <li><strong>Consistent Messaging:</strong> Uses centralized error messages from Constants</li>
 *   <li><strong>Null Safety:</strong> Handles null and missing attributes gracefully</li>
 * </ul>
 *
 * <h3>Usage Pattern:</h3>
 * <pre>{@code
 * ExtensionResponse validationResponse = ExtensionUtil.validateAttributes(attributes);
 * if (validationResponse != null) {
 *     return validationResponse; // Return validation error
 * }
 * // Proceed with normal processing
 * }</pre>
 *
 * @author Krista Extensions Team
 * @version 2.1.5
 * @since 1.0.0
 * @see app.krista.extensions.krista.llms.openai_qa.catalog.Constants for error messages
 */
public class ExtensionUtil {

    /** Private constructor to prevent instantiation of utility class. */
    private ExtensionUtil() {
    }

    /**
     * Validates the required attributes for catalog requests.
     *
     * <p>This method performs comprehensive validation of extension configuration
     * attributes required for OpenAI API operations. It checks for the presence
     * and validity of both API key and model selection.</p>
     *
     * <h3>Validation Steps:</h3>
     * <ol>
     *   <li>Validates API key presence and non-null value</li>
     *   <li>Validates model selection presence and non-null value</li>
     *   <li>Returns first validation error encountered, or null if all valid</li>
     * </ol>
     *
     * @param attributes The map of extension configuration attributes to validate
     * @return ExtensionResponse with error details if validation fails, otherwise {@code null}
     * @see #validateApiKey(Map) for API key validation logic
     * @see #validateModel(Map) for model validation logic
     */
    public static ExtensionResponse validateAttributes(Map<String, Object> attributes) {
        ExtensionResponse response = validateApiKey(attributes);
        if (response != null) {
            return response;
        }

        response = validateModel(attributes);
        if (response != null) {
            return response;
        }

        return null;
    }

    /**
     * Validates the presence and validity of the OpenAI API key.
     *
     * <p>Checks that the attributes map is not null and contains a non-null
     * API key value. The API key is required for all OpenAI API operations.</p>
     *
     * @param attributes The extension configuration attributes
     * @return ExtensionResponse with API key error if invalid, otherwise {@code null}
     */
    private static ExtensionResponse validateApiKey(Map<String, Object> attributes) {
        if (attributes == null || attributes.get(OpenAIConstants.API_KEY) == null) {
            return createErrorResponse(API_KEY_MISSING);
        }
        return null;
    }

    /**
     * Validates the presence and validity of the selected AI model.
     *
     * <p>Checks that a model has been selected in the extension configuration.
     * The model determines which OpenAI model will be used for processing.</p>
     *
     * @param attributes The extension configuration attributes
     * @return ExtensionResponse with model selection error if invalid, otherwise {@code null}
     */
    private static ExtensionResponse validateModel(Map<String, Object> attributes) {
        if (attributes.get(OpenAIConstants.MODEL) == null) {
            return createErrorResponse(MODEL_NOT_SELECTED);
        }
        return null;
    }

    /**
     * Creates a standardized error response for validation failures.
     *
     * <p>This method creates a consistent error response format used throughout
     * the extension for validation and configuration errors. All error responses
     * use the INPUT_ERROR exception type and include a timestamp.</p>
     *
     * <h3>Error Response Structure:</h3>
     * <ul>
     *   <li><strong>Result:</strong> FAILURE</li>
     *   <li><strong>Data:</strong> Empty map</li>
     *   <li><strong>Error:</strong> Contains message, timestamp, and exception type</li>
     *   <li><strong>Exception Type:</strong> INPUT_ERROR (indicates user configuration issue)</li>
     * </ul>
     *
     * @param message The error message to include in the response
     * @return ExtensionResponse configured as a failure with the provided error message
     */
    public static ExtensionResponse createErrorResponse(String message) {
        ExtensionResponse.Error error = new ExtensionResponse.Error(
                message,
                System.currentTimeMillis(),
                ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                null
        );
        return new ExtensionResponse(ExtensionResponse.Result.FAILURE, Map.of(), error, null, null);
    }
}
