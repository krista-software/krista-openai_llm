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

package app.krista.extensions.krista.llms.openai_qa;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extensions.krista.llms.openai_qa.service.impl.DiscussionValidatorImpl;
import app.krista.extensions.krista.llms.openai_qa.model.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify basic functionality without complex dependencies.
 * Tests core validation and service logic.
 */
public class SimpleDiscussionTest {

    @Test
    void testValidationResult_Success() {
        // Test ValidationResult success case
        ValidationResult result = ValidationResult.success();
        
        assertTrue(result.isValid());
        assertFalse(result.hasError());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidationResult_Failure() {
        // Test ValidationResult failure case
        String errorMessage = "Test error message";
        ValidationResult result = ValidationResult.failure(errorMessage);
        
        assertFalse(result.isValid());
        assertTrue(result.hasError());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    @Test
    void testDiscussionValidator_ValidSessionId() {
        // Test session ID validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String validSessionId = "123e4567-e89b-12d3-a456-426614174000";
        
        ValidationResult result = validator.validateSessionId(validSessionId);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testDiscussionValidator_InvalidSessionId() {
        // Test invalid session ID validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String invalidSessionId = "invalid-session-id";
        
        ValidationResult result = validator.validateSessionId(invalidSessionId);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Invalid session ID format"));
    }

    @Test
    void testDiscussionValidator_EmptySessionId() {
        // Test empty session ID validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        ValidationResult result = validator.validateSessionId("");
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Session ID is required"));
    }

    @Test
    void testDiscussionValidator_NullSessionId() {
        // Test null session ID validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        ValidationResult result = validator.validateSessionId(null);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Session ID is required"));
    }

    @Test
    void testDiscussionValidator_ValidQuestion() {
        // Test question validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String validQuestion = "What are the main topics in this document?";
        
        ValidationResult result = validator.validateQuestion(validQuestion);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testDiscussionValidator_EmptyQuestion() {
        // Test empty question validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        ValidationResult result = validator.validateQuestion("");
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Question is required"));
    }

    @Test
    void testDiscussionValidator_TooLongQuestion() {
        // Test question that's too long
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String longQuestion = "a".repeat(10001); // Exceeds 10K limit
        
        ValidationResult result = validator.validateQuestion(longQuestion);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("too long"));
    }

    @Test
    void testDiscussionValidator_ValidInstructions() {
        // Test instructions validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String validInstructions = "Please summarize the key points from the document.";
        
        ValidationResult result = validator.validateInstructions(validInstructions);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testDiscussionValidator_EmptyInstructions() {
        // Test empty instructions validation
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        ValidationResult result = validator.validateInstructions("");
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Instructions cannot be empty"));
    }

    @Test
    void testDiscussionValidator_TooLongInstructions() {
        // Test instructions that are too long
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String longInstructions = "a".repeat(10001); // Exceeds 10K limit
        
        ValidationResult result = validator.validateInstructions(longInstructions);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("too long"));
    }

    @Test
    void testExtensionResponse_SuccessCreation() {
        // Test ExtensionResponse creation
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        String message = "Session created successfully";
        
        ExtensionResponse response = new ExtensionResponse(
            ExtensionResponse.Result.SUCCESS,
            java.util.Map.of("Session Id", sessionId, "Message", message),
            null, null, null
        );
        
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());
        assertNotNull(response);
    }

    @Test
    void testSessionIdFormat_ValidUUIDs() {
        // Test various valid UUID formats
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        String[] validUUIDs = {
            "123e4567-e89b-12d3-a456-426614174000",
            "550e8400-e29b-41d4-a716-446655440000",
            "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
            "6ba7b811-9dad-11d1-80b4-00c04fd430c8"
        };
        
        for (String uuid : validUUIDs) {
            ValidationResult result = validator.validateSessionId(uuid);
            assertTrue(result.isValid(), "UUID should be valid: " + uuid);
        }
    }

    @Test
    void testSessionIdFormat_InvalidFormats() {
        // Test various invalid formats
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        String[] invalidFormats = {
            "not-a-uuid",
            "123e4567-e89b-12d3-a456",  // Too short
            "123e4567-e89b-12d3-a456-426614174000-extra", // Too long
            "123e4567_e89b_12d3_a456_426614174000", // Wrong separators
            "ggge4567-e89b-12d3-a456-426614174000"  // Invalid hex characters
        };
        
        for (String invalidFormat : invalidFormats) {
            ValidationResult result = validator.validateSessionId(invalidFormat);
            assertFalse(result.isValid(), "Format should be invalid: " + invalidFormat);
        }
    }

    @Test
    void testTextLengthValidation() {
        // Test the generic text length validation method
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        
        // Valid length
        ValidationResult result1 = validator.validateTextLength("Valid text", 100, "Test Field");
        assertTrue(result1.isValid());
        
        // Exceeds length
        ValidationResult result2 = validator.validateTextLength("a".repeat(101), 100, "Test Field");
        assertFalse(result2.isValid());
        assertTrue(result2.getErrorMessage().contains("Test Field is too long"));
        assertTrue(result2.getErrorMessage().contains("100 characters"));
    }

    @Test
    void testValidationChaining() {
        // Test multiple validations in sequence
        DiscussionValidatorImpl validator = new DiscussionValidatorImpl();
        String sessionId = "123e4567-e89b-12d3-a456-426614174000";
        String question = "What are the main findings?";
        String instructions = "Provide a detailed summary.";
        
        // All should be valid
        assertTrue(validator.validateSessionId(sessionId).isValid());
        assertTrue(validator.validateQuestion(question).isValid());
        assertTrue(validator.validateInstructions(instructions).isValid());
        
        // Test workflow simulation
        ValidationResult sessionResult = validator.validateSessionId(sessionId);
        if (sessionResult.isValid()) {
            ValidationResult questionResult = validator.validateQuestion(question);
            if (questionResult.isValid()) {
                ValidationResult instructionResult = validator.validateInstructions(instructions);
                assertTrue(instructionResult.isValid());
            }
        }
    }
}
