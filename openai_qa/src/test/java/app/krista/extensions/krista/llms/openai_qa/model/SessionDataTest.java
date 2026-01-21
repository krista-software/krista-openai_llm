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

package app.krista.extensions.krista.llms.openai_qa.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SessionData model class.
 */
@DisplayName("SessionData Tests")
class SessionDataTest {

    private SessionData sessionData;

    @BeforeEach
    void setUp() {
        sessionData = new SessionData("test-session-123");
    }

    @Nested
    @DisplayName("Constructor and Basic Getters")
    class ConstructorTests {

        @Test
        @DisplayName("Should create session with correct ID")
        void shouldCreateSessionWithCorrectId() {
            assertEquals("test-session-123", sessionData.getSessionId());
        }

        @Test
        @DisplayName("Should set createdAt timestamp")
        void shouldSetCreatedAtTimestamp() {
            long before = System.currentTimeMillis();
            SessionData newSession = new SessionData("new-session");
            long after = System.currentTimeMillis();

            assertTrue(newSession.getCreatedAt() >= before);
            assertTrue(newSession.getCreatedAt() <= after);
        }

        @Test
        @DisplayName("Should start with empty documents")
        void shouldStartWithEmptyDocuments() {
            assertEquals(0, sessionData.getDocumentCount());
            assertTrue(sessionData.getDocumentNames().isEmpty());
        }
    }

    @Nested
    @DisplayName("Document Management")
    class DocumentTests {

        @Test
        @DisplayName("Should add document successfully")
        void shouldAddDocumentSuccessfully() {
            sessionData.addDocument("test.pdf", "file-123");

            assertEquals(1, sessionData.getDocumentCount());
            assertEquals("file-123", sessionData.getFileId("test.pdf"));
        }

        @Test
        @DisplayName("Should add multiple documents")
        void shouldAddMultipleDocuments() {
            sessionData.addDocument("doc1.pdf", "file-1");
            sessionData.addDocument("doc2.pdf", "file-2");

            assertEquals(2, sessionData.getDocumentCount());
        }

        @Test
        @DisplayName("Should update existing document")
        void shouldUpdateExistingDocument() {
            sessionData.addDocument("test.pdf", "file-old");
            sessionData.addDocument("test.pdf", "file-new");

            assertEquals(1, sessionData.getDocumentCount());
            assertEquals("file-new", sessionData.getFileId("test.pdf"));
        }

        @Test
        @DisplayName("Should return null for non-existent document")
        void shouldReturnNullForNonExistentDocument() {
            assertNull(sessionData.getFileId("nonexistent.pdf"));
        }

        @Test
        @DisplayName("Should return file IDs in order")
        void shouldReturnFileIdsInOrder() {
            sessionData.addDocument("doc1.pdf", "file-1");
            sessionData.addDocument("doc2.pdf", "file-2");
            sessionData.addDocument("doc3.pdf", "file-3");

            List<String> fileIds = sessionData.getFileIds();
            assertEquals(3, fileIds.size());
            assertEquals("file-1", fileIds.get(0));
            assertEquals("file-2", fileIds.get(1));
            assertEquals("file-3", fileIds.get(2));
        }

        @Test
        @DisplayName("Should return document names")
        void shouldReturnDocumentNames() {
            sessionData.addDocument("doc1.pdf", "file-1");
            sessionData.addDocument("doc2.pdf", "file-2");

            Set<String> names = sessionData.getDocumentNames();
            assertEquals(2, names.size());
            assertTrue(names.contains("doc1.pdf"));
            assertTrue(names.contains("doc2.pdf"));
        }
    }

    @Nested
    @DisplayName("Instructions Management")
    class InstructionsTests {

        @Test
        @DisplayName("Should start with null instructions")
        void shouldStartWithNullInstructions() {
            assertNull(sessionData.getInstructions());
        }

        @Test
        @DisplayName("Should set and get instructions")
        void shouldSetAndGetInstructions() {
            sessionData.setInstructions("Be helpful and concise");
            assertEquals("Be helpful and concise", sessionData.getInstructions());
        }

        @Test
        @DisplayName("Should allow null instructions")
        void shouldAllowNullInstructions() {
            sessionData.setInstructions("Some instructions");
            sessionData.setInstructions(null);
            assertNull(sessionData.getInstructions());
        }
    }

    @Nested
    @DisplayName("Conversation History")
    class ConversationHistoryTests {

        @Test
        @DisplayName("Should start with empty conversation history")
        void shouldStartWithEmptyConversationHistory() {
            assertTrue(sessionData.getConversationHistory().isEmpty());
        }

        @Test
        @DisplayName("Should add conversation message")
        void shouldAddConversationMessage() {
            sessionData.addConversationMessage("user", "Hello");

            List<SessionData.ConversationMessage> history = sessionData.getConversationHistory();
            assertEquals(1, history.size());
            assertEquals("user", history.get(0).getRole());
            assertEquals("Hello", history.get(0).getContent());
        }

        @Test
        @DisplayName("Should add multiple messages")
        void shouldAddMultipleMessages() {
            sessionData.addConversationMessage("user", "Hello");
            sessionData.addConversationMessage("assistant", "Hi there!");

            List<SessionData.ConversationMessage> history = sessionData.getConversationHistory();
            assertEquals(2, history.size());
        }

        @Test
        @DisplayName("Should get recent conversation history")
        void shouldGetRecentConversationHistory() {
            sessionData.addConversationMessage("user", "Message 1");
            sessionData.addConversationMessage("assistant", "Response 1");
            sessionData.addConversationMessage("user", "Message 2");
            sessionData.addConversationMessage("assistant", "Response 2");

            List<SessionData.ConversationMessage> recent = sessionData.getRecentConversationHistory(2);
            assertEquals(2, recent.size());
            assertEquals("Message 2", recent.get(0).getContent());
            assertEquals("Response 2", recent.get(1).getContent());
        }

        @Test
        @DisplayName("Should return all when maxMessages exceeds history size")
        void shouldReturnAllWhenMaxMessagesExceedsHistorySize() {
            sessionData.addConversationMessage("user", "Hello");

            List<SessionData.ConversationMessage> recent = sessionData.getRecentConversationHistory(10);
            assertEquals(1, recent.size());
        }

        @Test
        @DisplayName("Should clear conversation history")
        void shouldClearConversationHistory() {
            sessionData.addConversationMessage("user", "Hello");
            sessionData.addConversationMessage("assistant", "Hi");

            sessionData.clearConversationHistory();

            assertTrue(sessionData.getConversationHistory().isEmpty());
        }

        @Test
        @DisplayName("ConversationMessage should have timestamp")
        void conversationMessageShouldHaveTimestamp() {
            long before = System.currentTimeMillis();
            sessionData.addConversationMessage("user", "Hello");
            long after = System.currentTimeMillis();

            SessionData.ConversationMessage msg = sessionData.getConversationHistory().get(0);
            assertTrue(msg.getTimestamp() >= before);
            assertTrue(msg.getTimestamp() <= after);
        }
    }
}

