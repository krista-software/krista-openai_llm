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

package app.krista.extensions.krista.llms.openai_qa.config;

import com.google.gson.Gson;

/**
 * Unified configuration class for OpenAI extension settings
 * Consolidates all constants and configuration values
 */
public class OpenAiConfiguration {
    
    // API Configuration
    public static final String OPENAI_FILES_API_URL = "https://api.openai.com/v1/files";
    public static final String OPENAI_RESPONSES_API_URL = "https://api.openai.com/v1/responses";
    public static final String OPENAI_CHAT_COMPLETIONS_API_URL = "https://api.openai.com/v1/chat/completions";
    
    // Request Parameters
    public static final String PARAM_PURPOSE = "purpose";
    public static final String PARAM_MODEL = "model";
    public static final String PARAM_MESSAGES = "messages";
    public static final String PARAM_INPUT = "input";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_FILE_ID = "file_id";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_MAX_TOKENS = "max_tokens";
    public static final String PARAM_TEMPERATURE = "temperature";
    public static final String PARAM_TRUNCATION = "truncation";
    public static final String TRUNCATION_AUTO = "auto";

    // Request Values
    public static final String VALUE_ASSISTANTS = "assistants";
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String TYPE_INPUT_FILE = "input_file";
    public static final String TYPE_INPUT_TEXT = "input_text";
    public static final String TYPE_INPUT_IMAGE = "input_image";

    // File Analysis Limits
    public static final int MAX_FILES_PER_REQUEST = 20;
    public static final long MAX_TOTAL_FILE_SIZE_BYTES = 50L * 1024 * 1024; // 50MB OpenAI limit

    // HTTP Headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String BEARER_PREFIX = "Bearer ";
    
    // File Processing
    public static final String PDF_MIME_TYPE = "application/pdf";
    public static final String PDF_FILE_TYPE = "pdf";
    public static final String PDF_EXTENSION = ".pdf";
    public static final long MAX_FILE_SIZE_BYTES = 512 * 1024 * 1024; // 512MB (OpenAI limit)
    public static final int MAX_FILES_PER_SESSION = 100; // Reasonable limit per session
    public static final long FILE_PROCESSING_DELAY_MS = 1000; // 1 second

    // Business Rules - Updated to OpenAI API limits
    public static final int MAX_TOKENS_GPT_4O = 16384; // gpt-4o max output
    public static final int MAX_TOKENS_GPT_4_1 = 32768; // gpt-4.1 max output
    public static final int MAX_TOKENS_GPT_35 = 4096; // gpt-3.5-turbo max output
    public static final int DEFAULT_MAX_TOKENS = 16384; // Default for most models
    public static final int MAX_TOKENS = DEFAULT_MAX_TOKENS; // Backward compatibility
    public static final double TEMPERATURE = 0.7;
    public static final int MAX_CONVERSATION_HISTORY = 6; // Last 3 Q&A pairs

    // Message Limits - Increased to OpenAI capabilities
    public static final int MAX_MESSAGE_LENGTH = 10000; // characters (increased from 1000)
    public static final int MAX_INSTRUCTION_LENGTH = 10000; // characters (increased from 2000)

    // Session Management
    public static final String INSTRUCTIONS_SUFFIX = "_instructions";

    // HTTP Client Settings (Optimized for 30s UI limit)
    public static final int CONNECT_TIMEOUT_SECONDS = 5;  // Reduced from 10
    public static final int READ_TIMEOUT_SECONDS = 20;    // Reduced from 30



    // JSON Processing
    public static final Gson GSON = new Gson();

    // Valid OpenAI Models
    public static final String DEFAULT_MODEL = "gpt-4o";
    public static final String[] VALID_MODELS = {
        "gpt-4.1",
        "gpt-4.1-mini",
        "gpt-4.1-nano",
        "gpt-4o",
        "gpt-4o-mini",
        "chatgpt-4o-latest",
        "gpt-4o-2024-11-20",
        "gpt-4o-2024-08-06",
        "gpt-4o-2024-05-13",
        "gpt-4o-mini-2024-07-18",
        "gpt-4-turbo",
        "gpt-4-turbo-2024-04-09",
        "gpt-4-turbo-preview",
        "gpt-4-0125-preview",
        "gpt-4-1106-preview",
        "gpt-4",
        "gpt-4-0613",
        "gpt-3.5-turbo",
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-1106"
    };

    /**
     * Validates if the provided model is a known valid OpenAI model
     */
    public static boolean isValidModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            return false;
        }

        String trimmedModel = model.trim();
        for (String validModel : VALID_MODELS) {
            if (validModel.equals(trimmedModel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the default model if the provided model is invalid
     */
    public static String getValidModel(String model) {
        if (isValidModel(model)) {
            return model.trim();
        }
        return DEFAULT_MODEL;
    }

    /**
     * Gets the appropriate max tokens based on the model
     */
    public static int getMaxTokensForModel(String model) {
        if (model == null) {
            return DEFAULT_MAX_TOKENS;
        }

        String lowerModel = model.toLowerCase();
        if (lowerModel.contains("gpt-4.1") || lowerModel.contains("gpt-4-1")) {
            return MAX_TOKENS_GPT_4_1;
        } else if (lowerModel.contains("gpt-4")) {
            return MAX_TOKENS_GPT_4O;
        } else if (lowerModel.contains("gpt-3.5")) {
            return MAX_TOKENS_GPT_35;
        }

        return DEFAULT_MAX_TOKENS;
    }

    private OpenAiConfiguration() {
        // Utility class - prevent instantiation
    }
}
