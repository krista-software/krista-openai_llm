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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ValidationResult model class.
 */
@DisplayName("ValidationResult Tests")
class ValidationResultTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create valid result with constructor")
        void shouldCreateValidResultWithConstructor() {
            ValidationResult result = new ValidationResult(true, null);

            assertTrue(result.isValid());
            assertNull(result.getErrorMessage());
        }

        @Test
        @DisplayName("Should create invalid result with constructor")
        void shouldCreateInvalidResultWithConstructor() {
            ValidationResult result = new ValidationResult(false, "Error occurred");

            assertFalse(result.isValid());
            assertEquals("Error occurred", result.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Static Factory Methods")
    class StaticFactoryTests {

        @Test
        @DisplayName("success() should create valid result")
        void successShouldCreateValidResult() {
            ValidationResult result = ValidationResult.success();

            assertTrue(result.isValid());
            assertNull(result.getErrorMessage());
            assertFalse(result.hasError());
        }

        @Test
        @DisplayName("failure() should create invalid result with message")
        void failureShouldCreateInvalidResultWithMessage() {
            ValidationResult result = ValidationResult.failure("Validation failed");

            assertFalse(result.isValid());
            assertEquals("Validation failed", result.getErrorMessage());
            assertTrue(result.hasError());
        }
    }

    @Nested
    @DisplayName("hasError() Method")
    class HasErrorTests {

        @Test
        @DisplayName("hasError should return false for valid result")
        void hasErrorShouldReturnFalseForValidResult() {
            ValidationResult result = ValidationResult.success();
            assertFalse(result.hasError());
        }

        @Test
        @DisplayName("hasError should return true for invalid result")
        void hasErrorShouldReturnTrueForInvalidResult() {
            ValidationResult result = ValidationResult.failure("Error");
            assertTrue(result.hasError());
        }
    }

    @Nested
    @DisplayName("toString() Method")
    class ToStringTests {

        @Test
        @DisplayName("toString should contain valid status for success")
        void toStringShouldContainValidStatusForSuccess() {
            ValidationResult result = ValidationResult.success();
            String str = result.toString();

            assertTrue(str.contains("valid=true"));
            assertTrue(str.contains("ValidationResult"));
        }

        @Test
        @DisplayName("toString should contain error message for failure")
        void toStringShouldContainErrorMessageForFailure() {
            ValidationResult result = ValidationResult.failure("Test error");
            String str = result.toString();

            assertTrue(str.contains("valid=false"));
            assertTrue(str.contains("Test error"));
        }
    }
}

