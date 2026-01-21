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

package app.krista.extensions.krista.llms.openai_qa.exception;

/**
 * Base exception for OpenAI extension operations
 */
public class OpenAiException extends Exception {
    
    public OpenAiException(String message) {
        super(message);
    }
    
    public OpenAiException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when document upload fails
 */
class DocumentUploadException extends OpenAiException {
    private final String fileName;
    private final int httpStatus;
    
    public DocumentUploadException(String fileName, int httpStatus, String message) {
        super(String.format("Failed to upload document '%s' (HTTP %d): %s", fileName, httpStatus, message));
        this.fileName = fileName;
        this.httpStatus = httpStatus;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}

/**
 * Exception thrown when session is not found
 */
class SessionNotFoundException extends OpenAiException {
    private final String sessionId;
    
    public SessionNotFoundException(String sessionId) {
        super("Session not found: " + sessionId);
        this.sessionId = sessionId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
}

/**
 * Exception thrown when file validation fails
 */
class FileValidationException extends OpenAiException {
    private final String fileName;
    
    public FileValidationException(String fileName, String message) {
        super(String.format("File validation failed for '%s': %s", fileName, message));
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
}

/**
 * Exception thrown when API call fails
 */
class ApiCallException extends OpenAiException {
    private final String apiEndpoint;
    private final int httpStatus;
    
    public ApiCallException(String apiEndpoint, int httpStatus, String message) {
        super(String.format("API call failed to %s (HTTP %d): %s", apiEndpoint, httpStatus, message));
        this.apiEndpoint = apiEndpoint;
        this.httpStatus = httpStatus;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}
