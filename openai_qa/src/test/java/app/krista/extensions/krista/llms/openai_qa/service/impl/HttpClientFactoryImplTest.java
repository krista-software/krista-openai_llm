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
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpClientFactoryImpl.
 * Tests HTTP client creation and configuration.
 */
class HttpClientFactoryImplTest {

    private HttpClientFactoryImpl httpClientFactory;

    @BeforeEach
    void setUp() {
        httpClientFactory = new HttpClientFactoryImpl();
    }

    @Test
    void testCreateApiClient_ReturnsConfiguredClient() {
        // When
        OkHttpClient client = httpClientFactory.createApiClient();

        // Then
        assertNotNull(client);
        assertEquals(OpenAiConfiguration.CONNECT_TIMEOUT_SECONDS * 1000, client.connectTimeoutMillis());
        assertEquals(OpenAiConfiguration.READ_TIMEOUT_SECONDS * 1000, client.readTimeoutMillis());
        assertEquals(OpenAiConfiguration.READ_TIMEOUT_SECONDS * 1000, client.writeTimeoutMillis());
    }

    @Test
    void testCreateFileUploadClient_ReturnsClientWithExtendedTimeouts() {
        // When
        OkHttpClient client = httpClientFactory.createFileUploadClient();

        // Then
        assertNotNull(client);
        assertEquals(OpenAiConfiguration.CONNECT_TIMEOUT_SECONDS * 1000, client.connectTimeoutMillis());
        
        // File upload client should have extended read timeout (at least 60 seconds)
        int expectedReadTimeout = Math.max(OpenAiConfiguration.READ_TIMEOUT_SECONDS * 2, 60) * 1000;
        assertEquals(expectedReadTimeout, client.readTimeoutMillis());
        assertEquals(expectedReadTimeout, client.writeTimeoutMillis());
    }

    @Test
    void testCreateCustomClient_ReturnsClientWithSpecifiedTimeouts() {
        // Given
        int connectTimeout = 10;
        int readTimeout = 30;

        // When
        OkHttpClient client = httpClientFactory.createCustomClient(connectTimeout, readTimeout);

        // Then
        assertNotNull(client);
        assertEquals(connectTimeout * 1000, client.connectTimeoutMillis());
        assertEquals(readTimeout * 1000, client.readTimeoutMillis());
        assertEquals(readTimeout * 1000, client.writeTimeoutMillis());
    }

    @Test
    void testCreateCustomClient_WithZeroTimeouts_ReturnsClientWithZeroTimeouts() {
        // Given
        int connectTimeout = 0;
        int readTimeout = 0;

        // When
        OkHttpClient client = httpClientFactory.createCustomClient(connectTimeout, readTimeout);

        // Then
        assertNotNull(client);
        assertEquals(0, client.connectTimeoutMillis());
        assertEquals(0, client.readTimeoutMillis());
        assertEquals(0, client.writeTimeoutMillis());
    }

    @Test
    void testCleanup_WithValidClient_DoesNotThrowException() {
        // Given
        OkHttpClient client = httpClientFactory.createApiClient();

        // When & Then
        assertDoesNotThrow(() -> httpClientFactory.cleanup(client));
    }

    @Test
    void testCleanup_WithNullClient_DoesNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> httpClientFactory.cleanup(null));
    }

    @Test
    void testMultipleClientCreation_ReturnsIndependentInstances() {
        // When
        OkHttpClient client1 = httpClientFactory.createApiClient();
        OkHttpClient client2 = httpClientFactory.createApiClient();

        // Then
        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2); // Should be different instances
    }

    @Test
    void testFileUploadClientVsApiClient_HasDifferentTimeouts() {
        // When
        OkHttpClient apiClient = httpClientFactory.createApiClient();
        OkHttpClient uploadClient = httpClientFactory.createFileUploadClient();

        // Then
        assertNotNull(apiClient);
        assertNotNull(uploadClient);
        
        // Upload client should have longer timeouts than API client
        assertTrue(uploadClient.readTimeoutMillis() >= apiClient.readTimeoutMillis());
        assertTrue(uploadClient.writeTimeoutMillis() >= apiClient.writeTimeoutMillis());
    }
}
