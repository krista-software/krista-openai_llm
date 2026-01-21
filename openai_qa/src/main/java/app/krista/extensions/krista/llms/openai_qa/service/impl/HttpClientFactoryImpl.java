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

package app.krista.extensions.krista.llms.openai_qa.service.impl;

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.service.IHttpClientFactory;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Implementation of HTTP client factory for OpenAI API operations.
 * Provides configured HTTP clients with appropriate timeouts and settings.
 */
public class HttpClientFactoryImpl implements IHttpClientFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactoryImpl.class);
    
    @Override
    public OkHttpClient createApiClient() {
        logger.debug("Creating API HTTP client with standard timeouts");
        return new OkHttpClient.Builder()
                .connectTimeout(OpenAiConfiguration.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(OpenAiConfiguration.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(OpenAiConfiguration.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }
    
    @Override
    public OkHttpClient createFileUploadClient() {
        logger.debug("Creating file upload HTTP client with extended timeouts");
        // Extended timeouts for large file uploads
        int uploadConnectTimeout = OpenAiConfiguration.CONNECT_TIMEOUT_SECONDS;
        int uploadReadTimeout = Math.max(OpenAiConfiguration.READ_TIMEOUT_SECONDS * 2, 60); // At least 60 seconds
        
        return new OkHttpClient.Builder()
                .connectTimeout(uploadConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(uploadReadTimeout, TimeUnit.SECONDS)
                .writeTimeout(uploadReadTimeout, TimeUnit.SECONDS)
                .build();
    }
    
    @Override
    public OkHttpClient createCustomClient(int connectTimeoutSeconds, int readTimeoutSeconds) {
        logger.debug("Creating custom HTTP client with timeouts: connect={}s, read={}s", 
                    connectTimeoutSeconds, readTimeoutSeconds);
        
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .build();
    }
    
    @Override
    public void cleanup(OkHttpClient client) {
        if (client != null) {
            logger.debug("Cleaning up HTTP client resources");
            try {
                // Shutdown dispatcher executor service
                client.dispatcher().executorService().shutdown();
                
                // Evict all connections from connection pool
                client.connectionPool().evictAll();
                
                // Close cache if present
                if (client.cache() != null) {
                    client.cache().close();
                }
            } catch (Exception e) {
                logger.warn("Error during HTTP client cleanup: {}", e.getMessage());
            }
        }
    }
}
