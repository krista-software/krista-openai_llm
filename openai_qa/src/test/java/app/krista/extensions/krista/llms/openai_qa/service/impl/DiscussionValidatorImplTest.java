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
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;

import app.krista.model.base.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscussionValidatorImpl.
 * Tests validation logic for session IDs, files, instructions, and questions.
 */
class DiscussionValidatorImplTest {

    private DiscussionValidatorImpl validator;

    @Mock
    private app.krista.model.base.File mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DiscussionValidatorImpl();
    }

    @Test
    void testValidateSessionId_ValidUUID_ReturnsSuccess() {
        // Given
        String validSessionId = "123e4567-e89b-12d3-a456-426614174000";

        // When
        ValidationResult result = validator.validateSessionId(validSessionId);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateSessionId_NullSessionId_ReturnsFailure() {
        // When
        ValidationResult result = validator.validateSessionId(null);

        // Then
        assertFalse(result.isValid());
        assertEquals("Session ID is required. Please provide a valid session ID from 'Create Discussion' request.",
                    result.getErrorMessage());
    }

    @Test
    void testValidateSessionId_EmptySessionId_ReturnsFailure() {
        // When
        ValidationResult result = validator.validateSessionId("");

        // Then
        assertFalse(result.isValid());
        assertEquals("Session ID is required. Please provide a valid session ID from 'Create Discussion' request.",
                    result.getErrorMessage());
    }

    @Test
    void testValidateSessionId_InvalidFormat_ReturnsFailure() {
        // Given
        String invalidSessionId = "invalid-session-id";

        // When
        ValidationResult result = validator.validateSessionId(invalidSessionId);

        // Then
        assertFalse(result.isValid());
        assertEquals("Invalid session ID format. Please provide a valid session ID from 'Create Discussion' request.",
                    result.getErrorMessage());
    }

    @Test
    void testValidateFiles_ValidPDFFile_ReturnsSuccess() {
        // Given
        when(mockFile.getFileName()).thenReturn("document.pdf");

        List<File> files = Collections.singletonList(mockFile);

        // When
        ValidationResult result = validator.validateFiles(files);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateFiles_EmptyFileList_ReturnsFailure() {
        // When
        ValidationResult result = validator.validateFiles(Collections.emptyList());

        // Then
        assertFalse(result.isValid());
        assertEquals("At least one file is required for document upload.", result.getErrorMessage());
    }

    @Test
    void testValidateFiles_TooManyFiles_ReturnsFailure() {
        // Given
        List<File> tooManyFiles = Collections.nCopies(OpenAiConfiguration.MAX_FILES_PER_SESSION + 1, mockFile);
        when(mockFile.getFileName()).thenReturn("document.pdf");

        // When
        ValidationResult result = validator.validateFiles(tooManyFiles);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Too many files"));
    }

    @Test
    void testValidateFiles_FileTooLarge_ReturnsFailure() {

        when(mockFile.getFileName()).thenReturn("large-document.pdf");
        List<File> files = Collections.singletonList(mockFile);

        // When
        ValidationResult result = validator.validateFiles(files);

        // Then - Should pass validation since only format is checked
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateFiles_NonPDFFile_ReturnsFailure() {
        // Given
        when(mockFile.getFileName()).thenReturn("document.txt");
        List<File> files = Collections.singletonList(mockFile);

        // When
        ValidationResult result = validator.validateFiles(files);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("unsupported format"));
    }

    @Test
    void testValidateInstructions_ValidInstructions_ReturnsSuccess() {
        // Given
        String validInstructions = "Please summarize the key points from the document.";

        // When
        ValidationResult result = validator.validateInstructions(validInstructions);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateInstructions_EmptyInstructions_ReturnsFailure() {
        // When
        ValidationResult result = validator.validateInstructions("");

        // Then
        assertFalse(result.isValid());
        assertEquals("Instructions cannot be empty. Please provide guidance for the AI assistant.",
                    result.getErrorMessage());
    }

    @Test
    void testValidateInstructions_TooLong_ReturnsFailure() {
        // Given
        String tooLongInstructions = "a".repeat(OpenAiConfiguration.MAX_INSTRUCTION_LENGTH + 1);

        // When
        ValidationResult result = validator.validateInstructions(tooLongInstructions);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("too long"));
    }

    @Test
    void testValidateQuestion_ValidQuestion_ReturnsSuccess() {
        // Given
        String validQuestion = "What are the main topics covered in this document?";

        // When
        ValidationResult result = validator.validateQuestion(validQuestion);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateQuestion_EmptyQuestion_ReturnsFailure() {
        // When
        ValidationResult result = validator.validateQuestion("");

        // Then
        assertFalse(result.isValid());
        assertEquals("Question is required. Please enter a question about your uploaded documents.",
                    result.getErrorMessage());
    }

    @Test
    void testValidateQuestion_TooLong_ReturnsFailure() {
        // Given
        String tooLongQuestion = "a".repeat(OpenAiConfiguration.MAX_MESSAGE_LENGTH + 1);

        // When
        ValidationResult result = validator.validateQuestion(tooLongQuestion);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("too long"));
    }

    @Test
    void testIsValidSessionIdFormat_ValidUUID_ReturnsTrue() {
        // Given
        String validUUID = "123e4567-e89b-12d3-a456-426614174000";

        // When
        boolean result = validator.isValidSessionIdFormat(validUUID);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsValidSessionIdFormat_InvalidFormat_ReturnsFalse() {
        // Given
        String invalidFormat = "not-a-uuid";

        // When
        boolean result = validator.isValidSessionIdFormat(invalidFormat);

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateTextLength_WithinLimit_ReturnsSuccess() {
        // Given
        String text = "Valid text";
        int maxLength = 100;
        String fieldName = "Test Field";

        // When
        ValidationResult result = validator.validateTextLength(text, maxLength, fieldName);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateTextLength_ExceedsLimit_ReturnsFailure() {
        // Given
        String text = "a".repeat(101);
        int maxLength = 100;
        String fieldName = "Test Field";

        // When
        ValidationResult result = validator.validateTextLength(text, maxLength, fieldName);

        // Then
        assertFalse(result.isValid());
        assertEquals("Test Field is too long. Please limit to 100 characters or less.",
                    result.getErrorMessage());
    }
}
