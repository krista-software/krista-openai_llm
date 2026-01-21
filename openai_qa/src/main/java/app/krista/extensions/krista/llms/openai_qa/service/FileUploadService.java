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

import app.krista.extension.executor.Invoker;
import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.OpenAiAttributes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service for uploading files to OpenAI
 * Uses injected OpenAiAttributes for API key access
 */
@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final OkHttpClient httpClient;
    private final OpenAiAttributes openAiAttributes;
    private final FileValidationService fileValidationService;

    @Inject
    public FileUploadService(Invoker invoker) {
        this.openAiAttributes = new OpenAiAttributes(invoker);
        this.fileValidationService = new FileValidationService();
        this.httpClient = createHttpClient();
        logger.info("FileUploadService initialized with OpenAI attributes");

    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    /**
     * Uploads file to OpenAI Files API
     */
    public String uploadFile(byte[] fileBytes, String fileName) throws OpenAiException {
        // Validate file before upload
        fileValidationService.validateFile(fileBytes, fileName);
        
        logger.debug("Uploading file to OpenAI: {}", fileName);
        
        try {
            String fileId = performUpload(fileBytes, fileName);
            
            // Wait for file processing
            waitForFileProcessing();
            
            logger.info("Successfully uploaded file: {} (ID: {})", fileName, fileId);
            return fileId;
            
        } catch (IOException cause) {
            logger.error("Failed to upload file: {}", fileName, cause);
            throw new OpenAiException("File upload failed: " + cause.getMessage(), cause);
        }
    }
    
    private String performUpload(byte[] fileBytes, String fileName) throws IOException, OpenAiException {
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(OpenAiConfiguration.PARAM_PURPOSE, OpenAiConfiguration.VALUE_ASSISTANTS)
                .addFormDataPart("file", fileName, 
                    RequestBody.create(MediaType.parse(OpenAiConfiguration.PDF_MIME_TYPE), fileBytes))
                .build();

        // Get API key and validate
        String apiKey = openAiAttributes.getApiKey().trim();
        logger.debug("Using API key for file upload: {}", apiKey != null ? apiKey : "null");

        Request request = new Request.Builder()
                .url(OpenAiConfiguration.OPENAI_FILES_API_URL)
                .addHeader(OpenAiConfiguration.HEADER_AUTHORIZATION,
                    OpenAiConfiguration.BEARER_PREFIX + apiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new OpenAiException(String.format("File upload failed (HTTP %d): %s", 
                    response.code(), errorBody));
            }
            
            if (response.body() == null) {
                throw new OpenAiException("Empty response from OpenAI Files API");
            }
            
            return extractFileId(response.body().string());
        }
    }
    
    private String extractFileId(String responseBody) throws OpenAiException {
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            if (!jsonResponse.has("id")) {
                throw new OpenAiException("No file ID returned in response: " + responseBody);
            }

            return jsonResponse.get("id").getAsString();

        } catch (JsonSyntaxException cause) {
            logger.error("Failed to parse file upload response: {}", cause.getMessage());
            throw new OpenAiException("Failed to parse response: " + cause.getMessage(), cause);
        } catch (Exception cause) {
            logger.error("Unexpected error parsing response: {}", cause.getMessage());
            throw new OpenAiException("Failed to extract file ID: " + cause.getMessage(), cause);
        }
    }
    
    private void waitForFileProcessing() {
        try {
            Thread.sleep(OpenAiConfiguration.FILE_PROCESSING_DELAY_MS);
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            logger.warn("File processing delay interrupted");
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
