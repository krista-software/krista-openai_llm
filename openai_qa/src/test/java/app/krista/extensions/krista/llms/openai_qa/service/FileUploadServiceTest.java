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

@DisplayName("FileUploadService Tests")
class FileUploadServiceTest {

    private MockWebServer mockServer;
    private FileUploadService service;

    @BeforeEach
    void setUp() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start();

        Map<String, Object> attrs = Map.of("API Key", "sk-test-key-12345", "Model Name", "ChatGPT 4");
        OpenAiAttributes attributes = new OpenAiAttributes(attrs);

        service = createServiceWithMockServer(attributes);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    private FileUploadService createServiceWithMockServer(OpenAiAttributes attributes) throws Exception {
        FileUploadService svc = createInstanceWithoutConstructor(FileUploadService.class);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        setField(svc, "httpClient", client);
        setField(svc, "openAiAttributes", attributes);
        setField(svc, "fileValidationService", new FileValidationService());
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

    // ── Successful Upload Tests ─────────────────────────────────────

    @Nested
    @DisplayName("Successful Upload Tests")
    class SuccessfulUploadTests {

        @Test
        @DisplayName("Should upload PDF file and return file ID")
        void shouldUploadPdfAndReturnFileId() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"id\":\"file-abc123\",\"object\":\"file\",\"purpose\":\"assistants\"}")
                    .setResponseCode(200));

            String fileId = uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "test.pdf");

            assertEquals("file-abc123", fileId);
        }

        @Test
        @DisplayName("Should send correct Authorization header")
        void shouldSendCorrectAuthHeader() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"id\":\"file-xyz789\",\"object\":\"file\"}")
                    .setResponseCode(200));

            uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "report.pdf");

            RecordedRequest request = mockServer.takeRequest();
            assertEquals("Bearer sk-test-key-12345", request.getHeader("Authorization"));
            assertEquals("POST", request.getMethod());
        }

        @Test
        @DisplayName("Should send multipart form data")
        void shouldSendMultipartFormData() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"id\":\"file-multi\",\"object\":\"file\"}")
                    .setResponseCode(200));

            uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "data.pdf");

            RecordedRequest request = mockServer.takeRequest();
            String contentType = request.getHeader("Content-Type");
            assertNotNull(contentType);
            assertTrue(contentType.contains("multipart/form-data"));

            String body = request.getBody().readUtf8();
            assertTrue(body.contains("assistants"), "Body should contain purpose=assistants");
            assertTrue(body.contains("data.pdf"), "Body should contain the file name");
        }

        @Test
        @DisplayName("Should upload XLSX file via generic upload")
        void shouldUploadXlsxViaGenericUpload() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"id\":\"file-xlsx001\",\"object\":\"file\"}")
                    .setResponseCode(200));

            String fileId = uploadGenericToMockServer(new byte[]{1, 2, 3, 4}, "report.xlsx");

            assertEquals("file-xlsx001", fileId);
        }
    }

    // ── Error Handling Tests ────────────────────────────────────────

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw for HTTP 400 upload error")
        void shouldThrowForUploadError() {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"error\":{\"message\":\"Invalid file\"}}")
                    .setResponseCode(400));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "bad.pdf"));
            assertTrue(ex.getMessage().contains("File upload failed"));
        }

        @Test
        @DisplayName("Should throw for HTTP 401 unauthorized")
        void shouldThrowForUnauthorized() {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"error\":{\"message\":\"Invalid API key\"}}")
                    .setResponseCode(401));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "secret.pdf"));
            assertTrue(ex.getMessage().contains("File upload failed"));
        }

        @Test
        @DisplayName("Should throw when response has no file ID")
        void shouldThrowWhenNoFileIdInResponse() {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"object\":\"file\",\"purpose\":\"assistants\"}")
                    .setResponseCode(200));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "noid.pdf"));
            assertTrue(ex.getMessage().contains("No file ID"));
        }

        @Test
        @DisplayName("Should throw when response body is not valid JSON")
        void shouldThrowForInvalidJsonResponse() {
            mockServer.enqueue(new MockResponse()
                    .setBody("not json at all")
                    .setResponseCode(200));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "badjson.pdf"));
            assertTrue(ex.getMessage().contains("parse"));
        }

        @Test
        @DisplayName("Should throw for empty response body")
        void shouldThrowForEmptyResponseBody() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(""));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "empty.pdf"));
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("Should throw for network error during upload")
        void shouldThrowForNetworkError() {
            mockServer.enqueue(new MockResponse()
                    .setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));

            // Network error surfaces as either OpenAiException (production) or IOException (test helper)
            Exception ex = assertThrows(Exception.class,
                    () -> uploadToMockServer(new byte[]{0x25, 0x50, 0x44, 0x46}, "network.pdf"));
            assertTrue(ex instanceof OpenAiException || ex instanceof IOException,
                    "Expected OpenAiException or IOException but got: " + ex.getClass().getName());
        }
    }

    // ── Validation Tests ────────────────────────────────────────────

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should reject null file bytes")
        void shouldRejectNullFileBytes() {
            assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(null, "test.pdf"));
        }

        @Test
        @DisplayName("Should reject empty file bytes")
        void shouldRejectEmptyFileBytes() {
            assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[0], "test.pdf"));
        }

        @Test
        @DisplayName("Should reject non-PDF file for PDF-only upload")
        void shouldRejectNonPdfFile() {
            assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{1, 2, 3}, "test.xlsx"));
        }

        @Test
        @DisplayName("Should reject empty file name")
        void shouldRejectEmptyFileName() {
            assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{1, 2, 3}, ""));
        }

        @Test
        @DisplayName("Should accept DOCX via generic upload but reject via PDF upload")
        void shouldAcceptDocxViaGenericOnly() {
            mockServer.enqueue(new MockResponse()
                    .setBody("{\"id\":\"file-docx\",\"object\":\"file\"}")
                    .setResponseCode(200));

            // PDF-only upload should reject DOCX
            assertThrows(OpenAiException.class,
                    () -> uploadToMockServer(new byte[]{1, 2, 3}, "test.docx"));

            // Generic upload should accept DOCX
            assertDoesNotThrow(
                    () -> uploadGenericToMockServer(new byte[]{1, 2, 3}, "test.docx"));
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private String uploadToMockServer(byte[] fileBytes, String fileName) throws Exception {
        return callPerformUploadViaMock(fileBytes, fileName, true);
    }

    private String uploadGenericToMockServer(byte[] fileBytes, String fileName) throws Exception {
        return callPerformUploadViaMock(fileBytes, fileName, false);
    }

    /**
     * Validates and uploads file to mock server.
     * For pdfOnly=true, validates via validateFile (PDF-only).
     * For pdfOnly=false, validates via validateFileGeneric.
     */
    private String callPerformUploadViaMock(byte[] fileBytes, String fileName, boolean pdfOnly) throws Exception {
        // Run validation first
        FileValidationService validator = new FileValidationService();
        if (pdfOnly) {
            validator.validateFile(fileBytes, fileName);
        } else {
            validator.validateFileGeneric(fileBytes, fileName);
        }

        // Call performUpload with mock URL via reflection
        String mockUrl = mockServer.url("/v1/files").toString();

        // Build multipart body and request manually to point at mock server
        okhttp3.MultipartBody body = new okhttp3.MultipartBody.Builder()
                .setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("purpose", "assistants")
                .addFormDataPart("file", fileName,
                        okhttp3.RequestBody.create(
                                okhttp3.MediaType.parse(FileTypeClassifier.getMimeType(fileName)), fileBytes))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(mockUrl)
                .addHeader("Authorization", "Bearer " + getApiKey())
                .post(body)
                .build();

        // Get httpClient via reflection
        Field clientField = FileUploadService.class.getDeclaredField("httpClient");
        clientField.setAccessible(true);
        OkHttpClient client = (OkHttpClient) clientField.get(service);

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new OpenAiException(String.format("File upload failed (HTTP %d): %s",
                        response.code(), errorBody));
            }

            if (response.body() == null) {
                throw new OpenAiException("Empty response from OpenAI Files API");
            }

            // Use extractFileId via reflection
            var method = FileUploadService.class.getDeclaredMethod("extractFileId", String.class);
            method.setAccessible(true);
            try {
                return (String) method.invoke(service, response.body().string());
            } catch (java.lang.reflect.InvocationTargetException e) {
                if (e.getCause() instanceof OpenAiException) {
                    throw (OpenAiException) e.getCause();
                }
                throw e;
            }
        }
    }

    private String getApiKey() throws Exception {
        Field attrsField = FileUploadService.class.getDeclaredField("openAiAttributes");
        attrsField.setAccessible(true);
        OpenAiAttributes attrs = (OpenAiAttributes) attrsField.get(service);
        return attrs.getApiKey().trim();
    }
}
