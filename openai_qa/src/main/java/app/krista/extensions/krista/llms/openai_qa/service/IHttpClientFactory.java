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

package app.krista.extensions.krista.llms.openai_qa.service;

import okhttp3.OkHttpClient;

/**
 * Interface for HTTP client creation and management.
 * Provides abstraction for HTTP client configuration and lifecycle.
 */
public interface IHttpClientFactory {
    
    /**
     * Creates a configured HTTP client for OpenAI API calls.
     * 
     * @return Configured OkHttpClient instance
     */
    OkHttpClient createApiClient();
    
    /**
     * Creates a configured HTTP client for file upload operations.
     * 
     * @return Configured OkHttpClient instance optimized for file uploads
     */
    OkHttpClient createFileUploadClient();
    
    /**
     * Creates a configured HTTP client with custom timeouts.
     * 
     * @param connectTimeoutSeconds Connection timeout in seconds
     * @param readTimeoutSeconds Read timeout in seconds
     * @return Configured OkHttpClient instance
     */
    OkHttpClient createCustomClient(int connectTimeoutSeconds, int readTimeoutSeconds);
    
    /**
     * Cleans up resources for a given HTTP client.
     * 
     * @param client The HTTP client to clean up
     */
    void cleanup(OkHttpClient client);
}
