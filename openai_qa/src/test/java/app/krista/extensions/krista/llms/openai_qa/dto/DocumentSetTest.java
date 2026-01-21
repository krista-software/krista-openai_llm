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

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DocumentSet DTO class.
 */
@DisplayName("DocumentSet Tests")
class DocumentSetTest {

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with name and file")
        void shouldCreateWithNameAndFile() throws IOException {
            File file = createTempFile("test.pdf");
            DocumentSet doc = new DocumentSet("document.pdf", file);

            assertEquals("document.pdf", doc.getName());
            assertEquals(file, doc.getFile());
            assertNull(doc.getFileBytes());
            assertEquals(OpenAiConfiguration.PDF_MIME_TYPE, doc.getMimeType());
        }

        @Test
        @DisplayName("Should create with file and bytes")
        void shouldCreateWithFileAndBytes() throws IOException {
            File file = createTempFile("test.pdf");
            byte[] bytes = "PDF content".getBytes();
            DocumentSet doc = new DocumentSet(file, bytes);

            assertEquals("test.pdf", doc.getName());
            assertEquals(file, doc.getFile());
            assertArrayEquals(bytes, doc.getFileBytes());
        }

        @Test
        @DisplayName("Should create with all parameters")
        void shouldCreateWithAllParameters() throws IOException {
            File file = createTempFile("test.pdf");
            byte[] bytes = "content".getBytes();
            DocumentSet doc = new DocumentSet("custom.pdf", file, bytes, "application/pdf");

            assertEquals("custom.pdf", doc.getName());
            assertEquals(file, doc.getFile());
            assertArrayEquals(bytes, doc.getFileBytes());
            assertEquals("application/pdf", doc.getMimeType());
        }

        @Test
        @DisplayName("Should use default MIME type when null provided")
        void shouldUseDefaultMimeTypeWhenNullProvided() throws IOException {
            File file = createTempFile("test.pdf");
            DocumentSet doc = new DocumentSet("test.pdf", file, null, null);

            assertEquals(OpenAiConfiguration.PDF_MIME_TYPE, doc.getMimeType());
        }
    }

    @Nested
    @DisplayName("getFileSize Tests")
    class FileSizeTests {

        @Test
        @DisplayName("Should return bytes length when bytes available")
        void shouldReturnBytesLengthWhenBytesAvailable() throws IOException {
            File file = createTempFile("test.pdf");
            byte[] bytes = "Hello World".getBytes();
            DocumentSet doc = new DocumentSet(file, bytes);

            assertEquals(11, doc.getFileSize());
        }

        @Test
        @DisplayName("Should return file length when no bytes")
        void shouldReturnFileLengthWhenNoBytes() throws IOException {
            Path filePath = tempDir.resolve("test.pdf");
            Files.write(filePath, "Test content".getBytes());
            File file = filePath.toFile();
            DocumentSet doc = new DocumentSet("test.pdf", file);

            assertEquals(12, doc.getFileSize());
        }

        @Test
        @DisplayName("Should return 0 when file does not exist")
        void shouldReturnZeroWhenFileDoesNotExist() {
            File nonExistent = new File("/nonexistent/file.pdf");
            DocumentSet doc = new DocumentSet("file.pdf", nonExistent);

            assertEquals(0, doc.getFileSize());
        }
    }

    @Nested
    @DisplayName("getFileExtension Tests")
    class FileExtensionTests {

        @Test
        @DisplayName("Should return pdf extension")
        void shouldReturnPdfExtension() throws IOException {
            File file = createTempFile("document.pdf");
            DocumentSet doc = new DocumentSet("document.pdf", file);

            assertEquals("pdf", doc.getFileExtension());
        }

        @Test
        @DisplayName("Should return extension in lowercase")
        void shouldReturnExtensionInLowercase() throws IOException {
            File file = createTempFile("document.PDF");
            DocumentSet doc = new DocumentSet("document.PDF", file);

            assertEquals("pdf", doc.getFileExtension());
        }

        @Test
        @DisplayName("Should return empty string for no extension")
        void shouldReturnEmptyStringForNoExtension() throws IOException {
            File file = createTempFile("document");
            DocumentSet doc = new DocumentSet("document", file);

            assertEquals("", doc.getFileExtension());
        }

        @Test
        @DisplayName("Should return empty string for null name")
        void shouldReturnEmptyStringForNullName() throws IOException {
            File file = createTempFile("test.pdf");
            DocumentSet doc = new DocumentSet(null, file, null, null);

            assertEquals("", doc.getFileExtension());
        }
    }

    @Nested
    @DisplayName("isPdfDocument Tests")
    class IsPdfDocumentTests {

        @Test
        @DisplayName("Should return true for pdf extension")
        void shouldReturnTrueForPdfExtension() throws IOException {
            File file = createTempFile("document.pdf");
            DocumentSet doc = new DocumentSet("document.pdf", file);

            assertTrue(doc.isPdfDocument());
        }

        @Test
        @DisplayName("Should return true for PDF uppercase extension")
        void shouldReturnTrueForPdfUppercaseExtension() throws IOException {
            File file = createTempFile("document.PDF");
            DocumentSet doc = new DocumentSet("document.PDF", file);

            assertTrue(doc.isPdfDocument());
        }

        @Test
        @DisplayName("Should return false for non-pdf extension")
        void shouldReturnFalseForNonPdfExtension() throws IOException {
            File file = createTempFile("document.txt");
            DocumentSet doc = new DocumentSet("document.txt", file);

            assertFalse(doc.isPdfDocument());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should contain name and file info")
        void shouldContainNameAndFileInfo() throws IOException {
            File file = createTempFile("test.pdf");
            DocumentSet doc = new DocumentSet("test.pdf", file);
            String str = doc.toString();

            assertTrue(str.contains("test.pdf"));
            assertTrue(str.contains("DocumentSet"));
            assertTrue(str.contains("mimeType"));
        }

        @Test
        @DisplayName("Should handle null file")
        void shouldHandleNullFile() {
            DocumentSet doc = new DocumentSet("test.pdf", null, null, "application/pdf");
            String str = doc.toString();

            assertTrue(str.contains("null"));
        }
    }

    private File createTempFile(String name) throws IOException {
        Path filePath = tempDir.resolve(name);
        Files.createFile(filePath);
        return filePath.toFile();
    }
}
