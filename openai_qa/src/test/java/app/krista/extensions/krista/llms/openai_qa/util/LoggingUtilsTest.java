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

package app.krista.extensions.krista.llms.openai_qa.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for LoggingUtils utility class.
 * Tests secure logging truncation functionality per KE-2772 security requirement.
 */
@DisplayName("LoggingUtils Tests")
class LoggingUtilsTest {

    @Nested
    @DisplayName("truncateForLogging(String) - Default Length")
    class TruncateForLoggingDefaultTests {

        @Test
        @DisplayName("Should return 'null' for null input")
        void shouldReturnNullStringForNullInput() {
            assertEquals("null", LoggingUtils.truncateForLogging(null));
        }

        @Test
        @DisplayName("Should return empty string unchanged")
        void shouldReturnEmptyStringUnchanged() {
            assertEquals("", LoggingUtils.truncateForLogging(""));
        }

        @Test
        @DisplayName("Should return short content unchanged")
        void shouldReturnShortContentUnchanged() {
            String shortContent = "This is a short message";
            assertEquals(shortContent, LoggingUtils.truncateForLogging(shortContent));
        }

        @Test
        @DisplayName("Should return content at exactly 200 chars unchanged")
        void shouldReturnContentAtExactLimitUnchanged() {
            String exactContent = "a".repeat(200);
            assertEquals(exactContent, LoggingUtils.truncateForLogging(exactContent));
        }

        @Test
        @DisplayName("Should truncate content over 200 chars")
        void shouldTruncateContentOverLimit() {
            String longContent = "a".repeat(500);
            String result = LoggingUtils.truncateForLogging(longContent);
            
            assertTrue(result.startsWith("a".repeat(200)));
            assertTrue(result.contains("... [TRUNCATED - 500 chars total]"));
        }

        @Test
        @DisplayName("Should handle content at 201 chars (just over limit)")
        void shouldTruncateContentJustOverLimit() {
            String content = "a".repeat(201);
            String result = LoggingUtils.truncateForLogging(content);
            
            assertTrue(result.startsWith("a".repeat(200)));
            assertTrue(result.contains("... [TRUNCATED - 201 chars total]"));
        }

        @Test
        @DisplayName("Should handle whitespace-only content")
        void shouldHandleWhitespaceOnlyContent() {
            String whitespace = "   ";
            assertEquals(whitespace, LoggingUtils.truncateForLogging(whitespace));
        }

        @Test
        @DisplayName("Should handle content with special characters")
        void shouldHandleSpecialCharacters() {
            String special = "Test with special chars: \n\t\r\"'<>&";
            assertEquals(special, LoggingUtils.truncateForLogging(special));
        }

        @Test
        @DisplayName("Should handle unicode content")
        void shouldHandleUnicodeContent() {
            String unicode = "Unicode: 日本語 中文 한국어 🎉";
            assertEquals(unicode, LoggingUtils.truncateForLogging(unicode));
        }
    }

    @Nested
    @DisplayName("truncateForLogging(String, int) - Custom Length")
    class TruncateForLoggingCustomLengthTests {

        @Test
        @DisplayName("Should return 'null' for null input with custom length")
        void shouldReturnNullStringForNullInput() {
            assertEquals("null", LoggingUtils.truncateForLogging(null, 100));
        }

        @Test
        @DisplayName("Should truncate to custom length")
        void shouldTruncateToCustomLength() {
            String content = "a".repeat(150);
            String result = LoggingUtils.truncateForLogging(content, 50);
            
            assertTrue(result.startsWith("a".repeat(50)));
            assertTrue(result.contains("... [TRUNCATED - 150 chars total]"));
        }

        @Test
        @DisplayName("Should return content unchanged when under custom limit")
        void shouldReturnContentUnchangedWhenUnderLimit() {
            String content = "Short content";
            assertEquals(content, LoggingUtils.truncateForLogging(content, 100));
        }

        @Test
        @DisplayName("Should return content unchanged at exact custom limit")
        void shouldReturnContentUnchangedAtExactLimit() {
            String content = "a".repeat(50);
            assertEquals(content, LoggingUtils.truncateForLogging(content, 50));
        }

        @Test
        @DisplayName("Should throw exception for maxLength less than 1")
        void shouldThrowExceptionForInvalidMaxLength() {
            assertThrows(IllegalArgumentException.class, 
                () -> LoggingUtils.truncateForLogging("test", 0));
            assertThrows(IllegalArgumentException.class, 
                () -> LoggingUtils.truncateForLogging("test", -1));
        }

        @Test
        @DisplayName("Should work with maxLength of 1")
        void shouldWorkWithMinimalMaxLength() {
            String result = LoggingUtils.truncateForLogging("hello", 1);
            assertTrue(result.startsWith("h"));
            assertTrue(result.contains("... [TRUNCATED - 5 chars total]"));
        }
    }

    @Nested
    @DisplayName("LOG_TRUNCATE_LENGTH Constant")
    class ConstantTests {

        @Test
        @DisplayName("LOG_TRUNCATE_LENGTH should be 200")
        void shouldHaveCorrectTruncateLength() {
            assertEquals(200, LoggingUtils.LOG_TRUNCATE_LENGTH);
        }
    }
}

