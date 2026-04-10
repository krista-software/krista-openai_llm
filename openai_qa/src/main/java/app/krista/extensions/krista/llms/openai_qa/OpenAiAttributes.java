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

import app.krista.extension.executor.Invoker;
import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
/**
 * OpenAI attributes management service for handling API key and model validation
 */
@Service
public class OpenAiAttributes {
    private static final Logger log = LoggerFactory.getLogger(OpenAiAttributes.class);
    private String model;
    private String apiKey;

    @Inject
    public OpenAiAttributes(Invoker invoker) {
        this(invoker.getAttributes());
    }
    public OpenAiAttributes(Map<String, Object> attributes) {
        update(attributes);
    }
    public static OpenAiAttributes create(Invoker invoker, Map<String, Object> attributes) {
        OpenAiAttributes openAiAttributes = new OpenAiAttributes(invoker);
        openAiAttributes.update(attributes);
        return openAiAttributes;
    }

    public void update(Map<String, Object> newAttributes) {
        String displayName = getStringValue(newAttributes, OpenAIConstants.MODEL, "");
        this.model = resolveApiModelId(displayName);
        this.apiKey = getStringValue(newAttributes, OpenAIConstants.API_KEY, "");
    }

    /**
     * Maps user-friendly display names to OpenAI API model identifiers.
     */
    private static String resolveApiModelId(String displayName) {
        return switch (displayName) {
            case OpenAIConstants.GPT_35 -> "gpt-3.5-turbo";
            case OpenAIConstants.GPT_4 -> "gpt-4o";
            case OpenAIConstants.GPT_4_1 -> "gpt-4.1";
            case OpenAIConstants.GPT_4_1_MINI -> "gpt-4.1-mini";
            case OpenAIConstants.GPT_4_1_NANO -> "gpt-4.1-nano";
            case OpenAIConstants.GPT_5_4 -> "gpt-5.4";
            case OpenAIConstants.GPT_5_4_MINI -> "gpt-5.4-mini";
            case OpenAIConstants.GPT_5_4_NANO -> "gpt-5.4-nano";
            default -> OpenAiConfiguration.DEFAULT_MODEL;
        };
    }
    public String getApiKey()
    {
        return apiKey;
    }
     public String getModel()
     {
         return model;
     }

    private String getStringValue(Map<String, Object> attributes, String key, String defaultValue) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
