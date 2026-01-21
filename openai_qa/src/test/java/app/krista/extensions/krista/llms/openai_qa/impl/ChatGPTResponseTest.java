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

package app.krista.extensions.krista.llms.openai_qa.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChatGPTResponse class and its nested classes.
 * Tests response parsing, data access, and edge cases.
 */
class ChatGPTResponseTest {

    @Test
    @DisplayName("Should create ChatGPTResponse with all fields")
    void testChatGPTResponse_AllFields() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        response.id = "chatcmpl-123";
        response.object = "chat.completion";
        response.created = 1677652288L;
        response.model = "gpt-4";
        
        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        message.content = "Hello! How can I help you today?";
        choice.message = message;
        choice.finish_reason = "stop";
        choice.index = 0;
        
        response.choices = List.of(choice);

        // Then
        assertEquals("chatcmpl-123", response.getId());
        assertEquals("chat.completion", response.getObject());
        assertEquals(1677652288L, response.getCreated());
        assertEquals("gpt-4", response.getModel());
        assertNotNull(response.getChoices());
        assertEquals(1, response.getChoices().size());
        assertEquals("Hello! How can I help you today?", response.getAnswer());
    }

    @Test
    @DisplayName("Should handle empty choices list")
    void testChatGPTResponse_EmptyChoices() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        response.choices = new ArrayList<>();

        // Then
        assertEquals("N/A", response.getAnswer());
        assertNotNull(response.getChoices());
        assertTrue(response.getChoices().isEmpty());
    }

    @Test
    @DisplayName("Should handle null choices")
    void testChatGPTResponse_NullChoices() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        response.choices = null;

        // Then
        assertEquals("N/A", response.getAnswer());
        assertNull(response.getChoices());
    }

    @Test
    @DisplayName("Should get answer from first choice")
    void testChatGPTResponse_GetAnswerFromFirstChoice() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        
        ChatGPTResponse.Choice choice1 = new ChatGPTResponse.Choice();
        ChatGPTResponse.Choice.Message message1 = new ChatGPTResponse.Choice.Message();
        message1.content = "First answer";
        choice1.message = message1;
        
        ChatGPTResponse.Choice choice2 = new ChatGPTResponse.Choice();
        ChatGPTResponse.Choice.Message message2 = new ChatGPTResponse.Choice.Message();
        message2.content = "Second answer";
        choice2.message = message2;
        
        response.choices = List.of(choice1, choice2);

        // Then
        assertEquals("First answer", response.getAnswer());
    }

    @Test
    @DisplayName("Should handle choice with null message")
    void testChatGPTResponse_NullMessage() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        
        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        choice.message = null;
        
        response.choices = List.of(choice);

        // When/Then
        assertThrows(NullPointerException.class, response::getAnswer);
    }

    @Test
    @DisplayName("Should test Choice class methods")
    void testChoice_Methods() {
        // Given
        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        message.content = "Test content";
        choice.message = message;
        choice.finish_reason = "stop";
        choice.index = 1;

        // Then
        assertEquals(message, choice.getMessage());
        assertEquals("stop", choice.getFinishReason());
        assertEquals(1, choice.getIndex());
        assertNotNull(choice.toString());
    }

    @Test
    @DisplayName("Should test Message class methods")
    void testMessage_Methods() {
        // Given
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "user";
        message.content = "Hello, AI!";

        // Then
        assertEquals("user", message.getRole());
        assertEquals("Hello, AI!", message.getContent());
        assertNotNull(message.toString());
    }

    @Test
    @DisplayName("Should handle various role types")
    void testMessage_VariousRoles() {
        // Test different role types
        String[] roles = {"user", "assistant", "system", "function"};
        
        for (String role : roles) {
            ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
            message.role = role;
            message.content = "Content for " + role;
            
            assertEquals(role, message.getRole());
            assertEquals("Content for " + role, message.getContent());
        }
    }

    @Test
    @DisplayName("Should handle various finish reasons")
    void testChoice_VariousFinishReasons() {
        // Test different finish reasons
        String[] finishReasons = {"stop", "length", "function_call", "content_filter"};
        
        for (String reason : finishReasons) {
            ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
            choice.finish_reason = reason;
            choice.index = 0;
            
            assertEquals(reason, choice.getFinishReason());
        }
    }

    @Test
    @DisplayName("Should handle null and empty content")
    void testMessage_NullAndEmptyContent() {
        // Test null content
        ChatGPTResponse.Choice.Message message1 = new ChatGPTResponse.Choice.Message();
        message1.role = "assistant";
        message1.content = null;
        
        assertNull(message1.getContent());
        
        // Test empty content
        ChatGPTResponse.Choice.Message message2 = new ChatGPTResponse.Choice.Message();
        message2.role = "assistant";
        message2.content = "";
        
        assertEquals("", message2.getContent());
    }

    @Test
    @DisplayName("Should test toString methods return non-null values")
    void testToString_NonNull() {
        // Test ChatGPTResponse toString
        ChatGPTResponse response = new ChatGPTResponse();
        response.id = "test-id";
        assertNotNull(response.toString());
        
        // Test Choice toString
        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        choice.index = 0;
        assertNotNull(choice.toString());
        
        // Test Message toString
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        assertNotNull(message.toString());
    }

    @Test
    @DisplayName("Should handle multiple choices correctly")
    void testChatGPTResponse_MultipleChoices() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        
        List<ChatGPTResponse.Choice> choices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
            ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
            message.role = "assistant";
            message.content = "Answer " + (i + 1);
            choice.message = message;
            choice.index = i;
            choice.finish_reason = "stop";
            choices.add(choice);
        }
        
        response.choices = choices;

        // Then
        assertEquals(3, response.getChoices().size());
        assertEquals("Answer 1", response.getAnswer()); // Should return first choice
        
        for (int i = 0; i < 3; i++) {
            assertEquals("Answer " + (i + 1), response.getChoices().get(i).getMessage().getContent());
            assertEquals(i, response.getChoices().get(i).getIndex());
        }
    }

    @Test
    @DisplayName("Should handle long content correctly")
    void testMessage_LongContent() {
        // Given
        String longContent = "A".repeat(10000); // 10K characters
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        message.content = longContent;

        // Then
        assertEquals(longContent, message.getContent());
        assertEquals(10000, message.getContent().length());
    }

    @Test
    @DisplayName("Should handle special characters in content")
    void testMessage_SpecialCharacters() {
        // Given
        String specialContent = "Hello! 🌟 This contains émojis, ñ special chars, and \"quotes\".";
        ChatGPTResponse.Choice.Message message = new ChatGPTResponse.Choice.Message();
        message.role = "assistant";
        message.content = specialContent;

        // Then
        assertEquals(specialContent, message.getContent());
    }

    @Test
    @DisplayName("Should handle zero and negative indices")
    void testChoice_EdgeCaseIndices() {
        // Test zero index
        ChatGPTResponse.Choice choice1 = new ChatGPTResponse.Choice();
        choice1.index = 0;
        assertEquals(0, choice1.getIndex());
        
        // Test negative index (edge case)
        ChatGPTResponse.Choice choice2 = new ChatGPTResponse.Choice();
        choice2.index = -1;
        assertEquals(-1, choice2.getIndex());
    }

    @Test
    @DisplayName("Should handle very old and future timestamps")
    void testChatGPTResponse_EdgeCaseTimestamps() {
        // Given
        ChatGPTResponse response = new ChatGPTResponse();
        
        // Test very old timestamp
        response.created = 0L;
        assertEquals(0L, response.getCreated());
        
        // Test future timestamp
        response.created = Long.MAX_VALUE;
        assertEquals(Long.MAX_VALUE, response.getCreated());
    }
}
