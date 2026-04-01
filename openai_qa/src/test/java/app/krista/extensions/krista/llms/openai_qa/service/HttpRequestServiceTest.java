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

import app.krista.extensions.krista.llms.openai_qa.OpenAiAttributes;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpRequestService Tests")
class HttpRequestServiceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private MockWebServer mockServer;
    private HttpRequestService service;

    @BeforeEach
    void setUp() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start();

        // Create service with mock attributes via reflection
        Map<String, Object> attrs = Map.of("API Key", "sk-test-key-12345", "Model Name", "ChatGPT 4");
        OpenAiAttributes attributes = new OpenAiAttributes(attrs);

        service = createServiceWithMockServer(attributes);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Creates HttpRequestService using reflection to inject mock dependencies,
     * bypassing the Invoker-based constructor.
     */
    private HttpRequestService createServiceWithMockServer(OpenAiAttributes attributes) throws Exception {
        // Use Unsafe or reflection to create instance without calling constructor
        HttpRequestService svc = createInstanceWithoutConstructor(HttpRequestService.class);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        setField(svc, "httpClient", client);
        setField(svc, "openAiAttributes", attributes);
        setField(svc, "responseParser", new ResponseParserService());
        return svc;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createInstanceWithoutConstructor(Class<T> clazz) throws Exception {
        var unsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafe.setAccessible(true);
        sun.misc.Unsafe u = (sun.misc.Unsafe) unsafe.get(null);
        return (T) u.allocateInstance(clazz);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private ObjectNode createTestPayload() {
        ObjectNode payload = OBJECT_MAPPER.createObjectNode();
        payload.put("model", "gpt-4o");
        payload.put("input", "test");
        return payload;
    }

    // ── Responses API Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("Responses API Tests")
    class ResponsesApiTests {

        @Test
        @DisplayName("Should parse successful Responses API response")
        void shouldParseSuccessfulResponse() throws Exception {
            String responseJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"Hello world\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(responseJson).setResponseCode(200));

            // Override URL to point to mock server
            ObjectNode payload = createTestPayload();
            String result = sendToMockServer(payload, false);

            assertEquals("Hello world", result);
        }

        @Test
        @DisplayName("Should send correct headers")
        void shouldSendCorrectHeaders() throws Exception {
            String responseJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"ok\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(responseJson).setResponseCode(200));

            sendToMockServer(createTestPayload(), false);

            RecordedRequest request = mockServer.takeRequest();
            assertTrue(request.getHeader("Content-Type").contains("application/json"));
            assertEquals("Bearer sk-test-key-12345", request.getHeader("Authorization"));
            assertEquals("POST", request.getMethod());
        }

        @Test
        @DisplayName("Should throw OpenAiException for 400 client error")
        void shouldThrowForClientError() {
            String errorJson = "{\"error\":{\"message\":\"Bad request\",\"type\":\"invalid_request_error\",\"code\":\"invalid_request\"}}";
            mockServer.enqueue(new MockResponse().setBody(errorJson).setResponseCode(400));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("API call failed"));
        }

        @Test
        @DisplayName("Should return user-friendly message for context_length_exceeded")
        void shouldHandleContextLengthExceeded() {
            String errorJson = "{\"error\":{\"message\":\"Too many tokens\",\"type\":\"invalid_request_error\",\"code\":\"context_length_exceeded\"}}";
            mockServer.enqueue(new MockResponse().setBody(errorJson).setResponseCode(400));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("context window"));
            assertTrue(ex.getMessage().contains("gpt-4.1"));
        }

        @Test
        @DisplayName("Should return user-friendly message for invalid_api_key")
        void shouldHandleInvalidApiKey() {
            String errorJson = "{\"error\":{\"message\":\"Invalid key\",\"type\":\"authentication_error\",\"code\":\"invalid_api_key\"}}";
            mockServer.enqueue(new MockResponse().setBody(errorJson).setResponseCode(401));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("API key is invalid"));
        }

        @Test
        @DisplayName("Should return user-friendly message for model_not_found")
        void shouldHandleModelNotFound() {
            String errorJson = "{\"error\":{\"message\":\"Not found\",\"type\":\"invalid_request_error\",\"code\":\"model_not_found\"}}";
            mockServer.enqueue(new MockResponse().setBody(errorJson).setResponseCode(404));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("model is not available"));
        }

        @Test
        @DisplayName("Should return user-friendly message for invalid_image")
        void shouldHandleInvalidImage() {
            String errorJson = "{\"error\":{\"message\":\"Bad image\",\"type\":\"invalid_request_error\",\"code\":\"invalid_image\"}}";
            mockServer.enqueue(new MockResponse().setBody(errorJson).setResponseCode(400));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("images could not be processed"));
        }
    }

    // ── Chat Completions API Tests ──────────────────────────────────

    @Nested
    @DisplayName("Chat Completions API Tests")
    class ChatCompletionsTests {

        @Test
        @DisplayName("Should parse successful Chat Completions response")
        void shouldParseSuccessfulResponse() throws Exception {
            String responseJson = "{\"choices\":[{\"message\":{\"content\":\"Chat reply\"}}]}";
            mockServer.enqueue(new MockResponse().setBody(responseJson).setResponseCode(200));

            String result = sendToMockServer(createTestPayload(), true);

            assertEquals("Chat reply", result);
        }
    }

    // ── Retry Logic Tests ───────────────────────────────────────────

    @Nested
    @DisplayName("Retry Logic Tests")
    class RetryTests {

        @Test
        @DisplayName("Should retry on HTTP 429 and succeed on second attempt")
        void shouldRetryOnRateLimit() throws Exception {
            // First attempt: 429
            mockServer.enqueue(new MockResponse().setResponseCode(429).setBody("{\"error\":{\"message\":\"Rate limited\",\"type\":\"rate_limit\",\"code\":\"rate_limit_exceeded\"}}"));
            // Second attempt: success
            String successJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"Retry success\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(successJson).setResponseCode(200));

            String result = sendToMockServer(createTestPayload(), false);

            assertEquals("Retry success", result);
            assertEquals(2, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should retry on HTTP 500 server error")
        void shouldRetryOnServerError() throws Exception {
            mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("{\"error\":{\"message\":\"Server error\",\"type\":\"server_error\",\"code\":\"server_error\"}}"));
            String successJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"Recovered\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(successJson).setResponseCode(200));

            String result = sendToMockServer(createTestPayload(), false);

            assertEquals("Recovered", result);
            assertEquals(2, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should retry on HTTP 503 service unavailable")
        void shouldRetryOn503() throws Exception {
            mockServer.enqueue(new MockResponse().setResponseCode(503).setBody("{\"error\":{\"message\":\"Unavailable\",\"type\":\"server_error\",\"code\":\"service_unavailable\"}}"));
            mockServer.enqueue(new MockResponse().setResponseCode(503).setBody("{\"error\":{\"message\":\"Unavailable\",\"type\":\"server_error\",\"code\":\"service_unavailable\"}}"));
            String successJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"Back online\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(successJson).setResponseCode(200));

            String result = sendToMockServer(createTestPayload(), false);

            assertEquals("Back online", result);
            assertEquals(3, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should fail after max retries on persistent 429")
        void shouldFailAfterMaxRetriesOn429() {
            for (int i = 0; i < 3; i++) {
                mockServer.enqueue(new MockResponse().setResponseCode(429)
                        .setBody("{\"error\":{\"message\":\"Rate limited\",\"type\":\"rate_limit\",\"code\":\"rate_limit_exceeded\"}}"));
            }

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            // After 3 retries exhausted, the last 429 response is handled as error
            assertTrue(ex.getMessage().contains("rate limit") || ex.getMessage().contains("Rate limit"));
            assertEquals(3, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should fail after max retries on persistent 500")
        void shouldFailAfterMaxRetriesOn500() {
            for (int i = 0; i < 3; i++) {
                mockServer.enqueue(new MockResponse().setResponseCode(500)
                        .setBody("{\"error\":{\"message\":\"Internal error\",\"type\":\"server_error\",\"code\":\"server_error\"}}"));
            }

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertNotNull(ex.getMessage());
            assertEquals(3, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should NOT retry on HTTP 400 client error")
        void shouldNotRetryOnClientError() {
            mockServer.enqueue(new MockResponse().setResponseCode(400)
                    .setBody("{\"error\":{\"message\":\"Bad request\",\"type\":\"invalid_request_error\",\"code\":\"invalid_request\"}}"));

            assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertEquals(1, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should NOT retry on HTTP 401 unauthorized")
        void shouldNotRetryOnUnauthorized() {
            mockServer.enqueue(new MockResponse().setResponseCode(401)
                    .setBody("{\"error\":{\"message\":\"Invalid key\",\"type\":\"auth_error\",\"code\":\"invalid_api_key\"}}"));

            assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertEquals(1, mockServer.getRequestCount());
        }

        @Test
        @DisplayName("Should retry on network error and succeed")
        void shouldRetryOnNetworkError() throws Exception {
            // First attempt: connection reset
            mockServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
            // Second attempt: success
            String successJson = "{\"output\":[{\"content\":[{\"type\":\"output_text\",\"text\":\"Network recovered\"}]}]}";
            mockServer.enqueue(new MockResponse().setBody(successJson).setResponseCode(200));

            String result = sendToMockServer(createTestPayload(), false);

            assertEquals("Network recovered", result);
        }

        @Test
        @DisplayName("Should throw after max retries on persistent network error")
        void shouldFailAfterMaxRetriesOnNetworkError() {
            for (int i = 0; i < 3; i++) {
                mockServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
            }

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> sendToMockServer(createTestPayload(), false));
            assertTrue(ex.getMessage().contains("Network error"));
            assertTrue(ex.getMessage().contains("3 attempts"));
        }
    }

    // ── Validate API Key Tests ──────────────────────────────────────

    @Nested
    @DisplayName("Validate API Key Tests")
    class ValidateApiKeyTests {

        @Test
        @DisplayName("Should pass for valid API key")
        void shouldPassForValidKey() {
            assertDoesNotThrow(() -> service.validateApiKey());
        }

        @Test
        @DisplayName("Should throw for empty API key")
        void shouldThrowForEmptyKey() throws Exception {
            Map<String, Object> attrs = Map.of("API Key", "", "Model Name", "ChatGPT 4");
            OpenAiAttributes emptyKeyAttrs = new OpenAiAttributes(attrs);
            HttpRequestService svc = createServiceWithMockServer(emptyKeyAttrs);

            OpenAiException ex = assertThrows(OpenAiException.class, svc::validateApiKey);
            assertTrue(ex.getMessage().contains("API key is required"));
        }
    }

    // ── Helper: send request to mock server ─────────────────────────

    /**
     * Sends a request to the MockWebServer by overriding the URL constant via
     * direct HTTP call through the service's internal sendRequest method.
     * Uses reflection to call the private sendRequest method.
     */
    private String sendToMockServer(ObjectNode payload, boolean chatCompletions) throws Exception {
        String url = mockServer.url("/").toString();

        // Use reflection to call private sendRequest
        var method = HttpRequestService.class.getDeclaredMethod("sendRequest", String.class, ObjectNode.class);
        method.setAccessible(true);

        try {
            String responseBody = (String) method.invoke(service, url, payload);
            ResponseParserService parser = new ResponseParserService();
            if (chatCompletions) {
                return parser.parseChatCompletionsResponse(responseBody);
            } else {
                return parser.parseResponsesApiResponse(responseBody);
            }
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof OpenAiException) {
                throw (OpenAiException) e.getCause();
            }
            throw e;
        }
    }
}
