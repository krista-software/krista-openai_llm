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
import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.dto.DiscussionHandler;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.extensions.krista.llms.openai_qa.service.IFileProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//
/**
 * Unit tests for DiscussionServiceImpl.
 * Tests session management operations with proper mocking of dependencies.
 */
class DiscussionServiceImplTest {
    private DiscussionServiceImpl discussionService;

    @Mock
    private DiscussionHandler mockDiscussionHandler;

    @Mock
    private IDiscussionValidator mockValidator;

    @Mock
    private IFileProcessor mockFileProcessor;

    @Mock
    private DocumentSet mockDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        discussionService = new DiscussionServiceImpl(mockDiscussionHandler, mockValidator);
    }
    @Test
    void testCreateSession_Success_ReturnsSessionId() throws Exception {
        // Given
        String expectedSessionId = "123e4567-e89b-12d3-a456-426614174000";
        when(mockDiscussionHandler.createDocumentSet()).thenReturn(expectedSessionId);

        // When
        String result = discussionService.createSession();

        // Then
        assertEquals(expectedSessionId, result);
        verify(mockDiscussionHandler).createDocumentSet();
    }

    @Test
    void testCreateSession_HandlerThrowsException_ThrowsOpenAiException() throws Exception {
        // Given
        when(mockDiscussionHandler.createDocumentSet()).thenThrow(new RuntimeException("Connection failed"));

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> discussionService.createSession());

        assertTrue(exception.getMessage().contains("Failed to create discussion session"));
        assertTrue(exception.getCause() instanceof RuntimeException);
    }
    @Test
    void testAddDocuments_ValidInput_ReturnsTrue() throws Exception {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        List<DocumentSet> documents = Collections.singletonList(mockDocument);

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());
        when(mockDiscussionHandler.addDocuments(sessionId, documents)).thenReturn(true);

        // When
        boolean result = discussionService.addDocuments(sessionId, documents);

        // Then
        assertTrue(result);
        verify(mockValidator).validateSessionId(sessionId);
        verify(mockDiscussionHandler).addDocuments(sessionId, documents);
    }

    @Test
    void testAddDocuments_InvalidSessionId_ThrowsOpenAiException() {
        // Given
        String invalidSessionId = "invalid-session";
        List<DocumentSet> documents = Collections.singletonList(mockDocument);

        when(mockValidator.validateSessionId(invalidSessionId))
            .thenReturn(ValidationResult.failure("Invalid session ID"));

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> discussionService.addDocuments(invalidSessionId, documents));

        assertEquals("Invalid session ID", exception.getMessage());
        verify(mockValidator).validateSessionId(invalidSessionId);
        verifyNoInteractions(mockDiscussionHandler);
    }
    @Test
    void testAddDocuments_EmptyDocumentList_ThrowsOpenAiException() {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        List<DocumentSet> emptyDocuments = Collections.emptyList();

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> discussionService.addDocuments(sessionId, emptyDocuments));

        assertEquals("No documents provided for upload", exception.getMessage());
    }
    @Test
    void testAddInstructions_ValidInput_ReturnsTrue() throws Exception {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        List<String> instructions = Arrays.asList("Summarize key points", "Focus on technical details");

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());
        when(mockValidator.validateInstructions(anyString())).thenReturn(ValidationResult.success());
        when(mockDiscussionHandler.addInstructions(sessionId, instructions)).thenReturn(true);

        // When
        boolean result = discussionService.addInstructions(sessionId, instructions);

        // Then
        assertTrue(result);
        verify(mockValidator).validateSessionId(sessionId);
        verify(mockValidator, times(2)).validateInstructions(anyString());
        verify(mockDiscussionHandler).addInstructions(sessionId, instructions);
    }

    @Test
    void testAddInstructions_InvalidInstruction_ThrowsOpenAiException() {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        List<String> instructions = Collections.singletonList("Invalid instruction");

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());
        when(mockValidator.validateInstructions("Invalid instruction"))
            .thenReturn(ValidationResult.failure("Instruction too long"));

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> discussionService.addInstructions(sessionId, instructions));

        assertEquals("Instruction too long", exception.getMessage());
        verifyNoInteractions(mockDiscussionHandler);
    }
    @Test
    void testAskQuestion_ValidInput_ReturnsResponse() throws Exception {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        String question = "What are the main topics?";
        String expectedResponse = "The main topics are...";

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());
        when(mockValidator.validateQuestion(question)).thenReturn(ValidationResult.success());
        when(mockDiscussionHandler.askQuestion(sessionId, question)).thenReturn(expectedResponse);

        // When
        String result = discussionService.askQuestion(sessionId, question);

        // Then
        assertEquals(expectedResponse, result);
        verify(mockValidator).validateSessionId(sessionId);
        verify(mockValidator).validateQuestion(question);
        verify(mockDiscussionHandler).askQuestion(sessionId, question);
    }

    @Test
    void testAskQuestion_EmptyResponse_ThrowsOpenAiException() throws Exception {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        String question = "What are the main topics?";

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());
        when(mockValidator.validateQuestion(question)).thenReturn(ValidationResult.success());
        when(mockDiscussionHandler.askQuestion(sessionId, question)).thenReturn("");

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> discussionService.askQuestion(sessionId, question));

        assertTrue(exception.getMessage().contains("No response generated"));
    }
    @Test
    void testDeleteSession_ValidSessionId_ReturnsTrue() throws Exception {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";

        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());

        // When
        boolean result = discussionService.deleteSession(sessionId);

        // Then
        assertTrue(result);
        verify(mockValidator).validateSessionId(sessionId);
    }

    @Test
    void testIsValidSession_ValidSessionId_ReturnsTrue() {
        // Given
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        when(mockValidator.validateSessionId(sessionId)).thenReturn(ValidationResult.success());

        // When
        boolean result = discussionService.isValidSession(sessionId);

        // Then
        assertTrue(result);
        verify(mockValidator).validateSessionId(sessionId);
    }

    @Test
    void testIsValidSession_InvalidSessionId_ReturnsFalse() {
        // Given
        String sessionId = "invalid-session";
        when(mockValidator.validateSessionId(sessionId))
            .thenReturn(ValidationResult.failure("Invalid format"));

        // When
        boolean result = discussionService.isValidSession(sessionId);

        // Then
        assertFalse(result);
        verify(mockValidator).validateSessionId(sessionId);
    }
}
