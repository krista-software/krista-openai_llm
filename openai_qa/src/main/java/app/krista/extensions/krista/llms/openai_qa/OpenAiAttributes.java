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
       this.model= OpenAiConfiguration.DEFAULT_MODEL;
        this.apiKey = getStringValue(newAttributes, OpenAIConstants.API_KEY, "");
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
