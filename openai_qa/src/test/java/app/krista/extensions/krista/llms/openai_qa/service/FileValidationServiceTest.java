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

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileValidationService Tests")
class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
    }

    @Nested
    @DisplayName("validateFile Tests")
    class ValidateFileTests {

        @Test
        @DisplayName("Should validate valid PDF file successfully")
        void shouldValidateValidPdfFile() {
            byte[] validPdfBytes = new byte[1024];
            String validFileName = "document.pdf";
            
            assertDoesNotThrow(() -> fileValidationService.validateFile(validPdfBytes, validFileName));
        }

        @Test
        @DisplayName("Should validate PDF with uppercase extension")
        void shouldValidatePdfWithUppercaseExtension() {
            byte[] validPdfBytes = new byte[1024];
            String validFileName = "document.PDF";
            
            assertDoesNotThrow(() -> fileValidationService.validateFile(validPdfBytes, validFileName));
        }

        @Test
        @DisplayName("Should validate PDF with mixed case extension")
        void shouldValidatePdfWithMixedCaseExtension() {
            byte[] validPdfBytes = new byte[1024];
            String validFileName = "document.Pdf";
            
            assertDoesNotThrow(() -> fileValidationService.validateFile(validPdfBytes, validFileName));
        }
    }

    @Nested
    @DisplayName("validateFileBytes Tests")
    class ValidateFileBytesTests {

        @Test
        @DisplayName("Should throw exception for null file bytes")
        void shouldThrowExceptionForNullFileBytes() {
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(null, "document.pdf"));
            
            assertEquals("File cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty file bytes")
        void shouldThrowExceptionForEmptyFileBytes() {
            byte[] emptyBytes = new byte[0];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(emptyBytes, "document.pdf"));
            
            assertEquals("File cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validatePdfExtension Tests")
    class ValidatePdfExtensionTests {

        @Test
        @DisplayName("Should throw exception for null file name")
        void shouldThrowExceptionForNullFileName() {
            byte[] validBytes = new byte[1024];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(validBytes, null));
            
            assertEquals("File name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty file name")
        void shouldThrowExceptionForEmptyFileName() {
            byte[] validBytes = new byte[1024];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(validBytes, ""));
            
            assertEquals("File name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for whitespace-only file name")
        void shouldThrowExceptionForWhitespaceFileName() {
            byte[] validBytes = new byte[1024];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(validBytes, "   "));
            
            assertEquals("File name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for non-PDF file")
        void shouldThrowExceptionForNonPdfFile() {
            byte[] validBytes = new byte[1024];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(validBytes, "document.txt"));
            
            assertEquals("Only PDF files are supported", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for DOCX file")
        void shouldThrowExceptionForDocxFile() {
            byte[] validBytes = new byte[1024];
            
            OpenAiException exception = assertThrows(OpenAiException.class, 
                () -> fileValidationService.validateFile(validBytes, "document.docx"));
            
            assertEquals("Only PDF files are supported", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateFileSize Tests")
    class ValidateFileSizeTests {

        @Test
        @DisplayName("Should accept file within size limit")
        void shouldAcceptFileWithinSizeLimit() {
            // Use a reasonable size for testing (1MB)
            byte[] validBytes = new byte[1024 * 1024];

            assertDoesNotThrow(() -> fileValidationService.validateFile(validBytes, "document.pdf"));
        }

        @Test
        @DisplayName("Should accept small file")
        void shouldAcceptSmallFile() {
            byte[] smallBytes = new byte[100];

            assertDoesNotThrow(() -> fileValidationService.validateFile(smallBytes, "document.pdf"));
        }

        @Test
        @DisplayName("Should accept 10MB file")
        void shouldAccept10MBFile() {
            byte[] tenMBBytes = new byte[10 * 1024 * 1024];

            assertDoesNotThrow(() -> fileValidationService.validateFile(tenMBBytes, "document.pdf"));
        }
    }
}

