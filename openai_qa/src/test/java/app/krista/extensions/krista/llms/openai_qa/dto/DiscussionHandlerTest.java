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

package app.krista.extensions.krista.llms.openai_qa.dto;

import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.service.OpenAiClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscussionHandler class.
 * Uses mocked OpenAiClientService to test without Krista SDK dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DiscussionHandler Tests")
class DiscussionHandlerTest {

    @Mock
    private OpenAiClientService mockClientService;

    private DiscussionHandler discussionHandler;

    @BeforeEach
    void setUp() {
        discussionHandler = new DiscussionHandler(mockClientService);
    }

    @Nested
    @DisplayName("createDocumentSet Tests")
    class CreateDocumentSetTests {

        @Test
        @DisplayName("Should create session and return session ID")
        void shouldCreateSessionAndReturnSessionId() {
            when(mockClientService.createSession()).thenReturn("session-123");

            String sessionId = discussionHandler.createDocumentSet();

            assertEquals("session-123", sessionId);
            verify(mockClientService).createSession();
        }
    }

    @Nested
    @DisplayName("askQuestion Tests")
    class AskQuestionTests {

        @Test
        @DisplayName("Should ask question and return response")
        void shouldAskQuestionAndReturnResponse() throws IOException, OpenAiException {
            String sessionId = "session-123";
            String question = "What is AI?";
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("exists", true);

            when(mockClientService.getSessionInfo(sessionId)).thenReturn(sessionInfo);
            when(mockClientService.askQuestion(eq(sessionId), eq(question), isNull()))
                    .thenReturn("AI is artificial intelligence.");

            String response = discussionHandler.askQuestion(sessionId, question);

            assertEquals("AI is artificial intelligence.", response);
            verify(mockClientService).getSessionInfo(sessionId);
            verify(mockClientService).askQuestion(sessionId, question, null);
        }

        @Test
        @DisplayName("Should throw exception when session not found")
        void shouldThrowExceptionWhenSessionNotFound() {
            String sessionId = "non-existent-session";
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("exists", false);

            when(mockClientService.getSessionInfo(sessionId)).thenReturn(sessionInfo);

            assertThrows(IllegalStateException.class, 
                () -> discussionHandler.askQuestion(sessionId, "question"));
        }

        @Test
        @DisplayName("Should wrap OpenAiException in IOException")
        void shouldWrapOpenAiExceptionInIOException() throws OpenAiException {
            String sessionId = "session-123";
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("exists", true);

            when(mockClientService.getSessionInfo(sessionId)).thenReturn(sessionInfo);
            when(mockClientService.askQuestion(anyString(), anyString(), any()))
                    .thenThrow(new OpenAiException("API error"));

            assertThrows(IOException.class, 
                () -> discussionHandler.askQuestion(sessionId, "question"));
        }
    }

    @Nested
    @DisplayName("addDocuments Tests")
    class AddDocumentsTests {

        @Test
        @DisplayName("Should add documents to session")
        void shouldAddDocumentsToSession() throws IOException, OpenAiException {
            String sessionId = "session-123";
            File mockFile1 = new File("doc1.pdf");
            File mockFile2 = new File("doc2.pdf");
            DocumentSet doc1 = new DocumentSet(mockFile1, new byte[]{1, 2, 3});
            DocumentSet doc2 = new DocumentSet(mockFile2, new byte[]{4, 5, 6});
            List<DocumentSet> documents = Arrays.asList(doc1, doc2);

            when(mockClientService.addDocumentToSession(eq(sessionId), any(DocumentSet.class)))
                    .thenReturn("file-id");

            boolean result = discussionHandler.addDocuments(sessionId, documents);

            assertTrue(result);
            verify(mockClientService, times(2)).addDocumentToSession(eq(sessionId), any(DocumentSet.class));
        }

        @Test
        @DisplayName("Should throw IOException when adding documents fails")
        void shouldThrowIOExceptionWhenAddingDocumentsFails() throws OpenAiException {
            String sessionId = "session-123";
            File mockFile = new File("doc.pdf");
            DocumentSet doc = new DocumentSet(mockFile, new byte[]{1, 2, 3});

            when(mockClientService.addDocumentToSession(anyString(), any(DocumentSet.class)))
                    .thenThrow(new OpenAiException("Upload failed"));

            assertThrows(IOException.class,
                () -> discussionHandler.addDocuments(sessionId, Arrays.asList(doc)));
        }
    }

    @Nested
    @DisplayName("addInstructions Tests")
    class AddInstructionsTests {

        @Test
        @DisplayName("Should add instructions to session")
        void shouldAddInstructionsToSession() throws IOException, OpenAiException {
            String sessionId = "session-123";
            List<String> instructions = Arrays.asList("Be concise", "Use examples");

            doNothing().when(mockClientService).addInstructionsToSession(anyString(), anyString());

            boolean result = discussionHandler.addInstructions(sessionId, instructions);

            assertTrue(result);
            verify(mockClientService).addInstructionsToSession(eq(sessionId), contains("Be concise"));
        }

        @Test
        @DisplayName("Should handle empty instructions list")
        void shouldHandleEmptyInstructionsList() throws IOException, OpenAiException {
            String sessionId = "session-123";
            List<String> emptyInstructions = Arrays.asList();

            boolean result = discussionHandler.addInstructions(sessionId, emptyInstructions);

            assertTrue(result);
            verify(mockClientService, never()).addInstructionsToSession(anyString(), anyString());
        }

        @Test
        @DisplayName("Should handle null instructions list")
        void shouldHandleNullInstructionsList() throws IOException, OpenAiException {
            String sessionId = "session-123";

            boolean result = discussionHandler.addInstructions(sessionId, null);

            assertTrue(result);
            verify(mockClientService, never()).addInstructionsToSession(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("closeSession Tests")
    class CloseSessionTests {

        @Test
        @DisplayName("Should close session successfully")
        void shouldCloseSessionSuccessfully() {
            String sessionId = "session-123";

            doNothing().when(mockClientService).closeSession(anyString(), anyBoolean());

            assertDoesNotThrow(() -> discussionHandler.closeSession(sessionId));
            verify(mockClientService).closeSession(sessionId, false);
        }

        @Test
        @DisplayName("Should handle exception during close gracefully")
        void shouldHandleExceptionDuringCloseGracefully() {
            String sessionId = "session-123";

            doThrow(new RuntimeException("Close failed")).when(mockClientService)
                    .closeSession(anyString(), anyBoolean());

            assertDoesNotThrow(() -> discussionHandler.closeSession(sessionId));
        }
    }
}

