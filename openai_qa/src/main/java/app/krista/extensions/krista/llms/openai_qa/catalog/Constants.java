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

/**
 * Constants for OpenAI extension error messages and validation.
 * These constants provide user-friendly error messages for various validation scenarios.
 */
public final class Constants {

    // Image Generation Validation Constants
    public static final String IMAGE_COUNT_INVALID = 
        "Image count must be between 1 and 10. Please adjust the number of images requested to be within this range.";
    
    public static final String IMAGE_MODEL_INVALID = 
        "Invalid image generation model specified. Please use either 'dall-e-2' or 'dall-e-3' as the model parameter.";
    
    public static final String IMAGE_QUALITY_INVALID = 
        "Image quality must be either 'standard' or 'hd'. Please set the quality parameter to one of these supported values.";
    
    public static final String IMAGE_SIZE_INVALID = 
        "Image size must be one of: 256x256, 512x512, or 1024x1024. Please use one of the supported image dimensions.";

    // Connection and API Constants
    public static final String CONNECTION_TEST_FAILED = 
        "Connection test failed. Please verify your API key is correct, check your network connectivity, " +
        "and ensure the OpenAI service is available. Visit https://status.openai.com/ to check service status.";
    
    public static final String API_SERVICE_UNAVAILABLE = 
        "OpenAI API service is currently unavailable. Please check the service status at https://status.openai.com/ " +
        "and try again later. If the issue persists, verify your API key and account status.";
    
    public static final String REQUEST_PROCESSING_ERROR =
        "An error occurred while processing your request. Please verify your input parameters and try again. " +
        "If the issue continues, check your API key permissions and account billing status.";

    // Configuration and Validation Constants
    public static final String API_KEY_MISSING =
        "API Key is missing! Please configure your OpenAI API key in the extension settings. " +
        "You can obtain an API key from https://platform.openai.com/api-keys";

    public static final String MODEL_NOT_SELECTED =
        "Model is not selected! Please select a valid OpenAI model in the extension configuration. " +
        "Available models include GPT-3.5, GPT-4, and other supported variants.";

    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
