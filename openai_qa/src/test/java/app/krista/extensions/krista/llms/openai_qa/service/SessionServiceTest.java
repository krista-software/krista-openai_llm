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

import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.model.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionService Tests")
class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
    }

    @Nested
    @DisplayName("createSession Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Should create new session with given ID")
        void shouldCreateNewSessionWithGivenId() {
            SessionData session = sessionService.createSession("test-session-1");
            
            assertNotNull(session);
            assertEquals("test-session-1", session.getSessionId());
        }

        @Test
        @DisplayName("Should allow creating multiple sessions")
        void shouldAllowCreatingMultipleSessions() {
            sessionService.createSession("session-1");
            sessionService.createSession("session-2");
            
            assertEquals(2, sessionService.getSessionCount());
        }
    }

    @Nested
    @DisplayName("getSession Tests")
    class GetSessionTests {

        @Test
        @DisplayName("Should return existing session")
        void shouldReturnExistingSession() throws OpenAiException {
            sessionService.createSession("existing-session");
            
            SessionData session = sessionService.getSession("existing-session");
            
            assertNotNull(session);
            assertEquals("existing-session", session.getSessionId());
        }

        @Test
        @DisplayName("Should throw exception for non-existent session")
        void shouldThrowExceptionForNonExistentSession() {
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> sessionService.getSession("non-existent"));
            
            assertTrue(exception.getMessage().contains("Session not found"));
        }
    }

    @Nested
    @DisplayName("sessionExists Tests")
    class SessionExistsTests {

        @Test
        @DisplayName("Should return true for existing session")
        void shouldReturnTrueForExistingSession() {
            sessionService.createSession("my-session");
            
            assertTrue(sessionService.sessionExists("my-session"));
        }

        @Test
        @DisplayName("Should return false for non-existent session")
        void shouldReturnFalseForNonExistentSession() {
            assertFalse(sessionService.sessionExists("unknown-session"));
        }
    }

    @Nested
    @DisplayName("addDocumentToSession Tests")
    class AddDocumentToSessionTests {

        @Test
        @DisplayName("Should add document to existing session")
        void shouldAddDocumentToExistingSession() throws OpenAiException {
            sessionService.createSession("doc-session");
            
            sessionService.addDocumentToSession("doc-session", "report.pdf", "file-123");
            
            List<String> fileIds = sessionService.getFileIds("doc-session");
            assertEquals(1, fileIds.size());
            assertEquals("file-123", fileIds.get(0));
        }

        @Test
        @DisplayName("Should throw exception when adding to non-existent session")
        void shouldThrowExceptionWhenAddingToNonExistentSession() {
            assertThrows(OpenAiException.class, 
                () -> sessionService.addDocumentToSession("unknown", "doc.pdf", "file-456"));
        }
    }

    @Nested
    @DisplayName("addInstructionsToSession Tests")
    class AddInstructionsToSessionTests {

        @Test
        @DisplayName("Should add instructions to existing session")
        void shouldAddInstructionsToExistingSession() throws OpenAiException {
            sessionService.createSession("instr-session");
            
            sessionService.addInstructionsToSession("instr-session", "Be helpful and concise");
            
            String instructions = sessionService.getInstructions("instr-session");
            assertEquals("Be helpful and concise", instructions);
        }
    }

    @Nested
    @DisplayName("addConversationMessage Tests")
    class AddConversationMessageTests {

        @Test
        @DisplayName("Should add conversation message to session")
        void shouldAddConversationMessageToSession() throws OpenAiException {
            sessionService.createSession("conv-session");
            
            sessionService.addConversationMessage("conv-session", "user", "Hello");
            sessionService.addConversationMessage("conv-session", "assistant", "Hi there!");
            
            List<SessionData.ConversationMessage> history = 
                sessionService.getRecentConversationHistory("conv-session");
            assertEquals(2, history.size());
        }
    }

    @Nested
    @DisplayName("deleteSession Tests")
    class DeleteSessionTests {

        @Test
        @DisplayName("Should delete existing session")
        void shouldDeleteExistingSession() {
            sessionService.createSession("to-delete");
            assertTrue(sessionService.sessionExists("to-delete"));
            
            sessionService.deleteSession("to-delete");
            
            assertFalse(sessionService.sessionExists("to-delete"));
        }

        @Test
        @DisplayName("Should handle deleting non-existent session gracefully")
        void shouldHandleDeletingNonExistentSessionGracefully() {
            assertDoesNotThrow(() -> sessionService.deleteSession("never-existed"));
        }
    }
}

