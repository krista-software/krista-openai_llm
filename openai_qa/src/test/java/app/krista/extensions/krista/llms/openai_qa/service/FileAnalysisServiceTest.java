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
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.model.base.File;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("FileAnalysisService Tests")
class FileAnalysisServiceTest {

    private MediaServerFileAccess mediaServerFileAccess;
    private KristaMediaClientUtil kristaMediaClientUtil;
    private FileUploadService fileUploadService;
    private HttpRequestService httpRequestService;
    private FileAnalysisService service;

    @BeforeEach
    void setUp() {
        mediaServerFileAccess = mock(MediaServerFileAccess.class);
        kristaMediaClientUtil = mock(KristaMediaClientUtil.class);
        fileUploadService = mock(FileUploadService.class);
        httpRequestService = mock(HttpRequestService.class);
        service = new FileAnalysisService(mediaServerFileAccess, kristaMediaClientUtil,
                fileUploadService, httpRequestService, "gpt-4o");
    }

    private File mockKristaFile(String fileName) {
        File file = mock(File.class);
        when(file.getFileName()).thenReturn(fileName);
        return file;
    }

    private void setupFileDownload(File file, byte[] bytes) throws IOException {
        java.io.File javaFile = mock(java.io.File.class);
        when(mediaServerFileAccess.downloadFileFromMediaServer(file)).thenReturn(javaFile);
        when(kristaMediaClientUtil.readFileAsBytes(javaFile)).thenReturn(bytes);
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw for null files list")
        void shouldThrowForNullFiles() {
            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(null, "Describe this", null));
            assertEquals("At least one file is required", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw for empty files list")
        void shouldThrowForEmptyFiles() {
            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(Collections.emptyList(), "Describe this", null));
            assertEquals("At least one file is required", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw for null prompt")
        void shouldThrowForNullPrompt() {
            File file = mockKristaFile("test.png");
            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), null, null));
            assertEquals("Prompt is required", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw for blank prompt")
        void shouldThrowForBlankPrompt() {
            File file = mockKristaFile("test.png");
            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), "   ", null));
            assertEquals("Prompt is required", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw when too many files")
        void shouldThrowWhenTooManyFiles() {
            List<File> files = Collections.nCopies(21, mockKristaFile("test.png"));
            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(files, "Prompt", null));
            assertTrue(ex.getMessage().contains("Too many files"));
        }
    }

    @Nested
    @DisplayName("File Processing Tests")
    class ProcessingTests {

        @Test
        @DisplayName("Should process single image as base64 inline")
        void shouldProcessSingleImage() throws Exception {
            File file = mockKristaFile("photo.png");
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            setupFileDownload(file, pngBytes);
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Analysis result");

            String result = service.analyzeFiles(List.of(file), "Describe this image", null);

            assertEquals("Analysis result", result);

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("input_image"));
            assertTrue(payload.contains("data:image/png;base64,"));
            verify(fileUploadService, never()).uploadFileGenericNoDelay(any(), any());
        }

        @Test
        @DisplayName("Should process single PDF as input_file inline")
        void shouldProcessSinglePdf() throws Exception {
            File file = mockKristaFile("report.pdf");
            byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
            setupFileDownload(file, pdfBytes);
            when(fileUploadService.uploadFileGenericNoDelay(pdfBytes, "report.pdf")).thenReturn("file-abc123");
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("PDF analysis");

            String result = service.analyzeFiles(List.of(file), "Summarize this document", null);

            assertEquals("PDF analysis", result);

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();

            // Should have input_file block with file ID
            assertTrue(payload.contains("input_file"));
            assertTrue(payload.contains("file-abc123"));
            // Should NOT have file_search or vector store references
            assertFalse(payload.contains("file_search"));
            assertFalse(payload.contains("vector_store"));
        }

        @Test
        @DisplayName("Should process mixed image + PDF — both inline")
        void shouldProcessMixedFiles() throws Exception {
            File imageFile = mockKristaFile("screenshot.png");
            File pdfFile = mockKristaFile("contract.pdf");

            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};

            setupFileDownload(imageFile, pngBytes);
            setupFileDownload(pdfFile, pdfBytes);
            when(fileUploadService.uploadFileGenericNoDelay(pdfBytes, "contract.pdf")).thenReturn("file-xyz");
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Mixed analysis");

            String result = service.analyzeFiles(Arrays.asList(imageFile, pdfFile), "Compare these", null);

            assertEquals("Mixed analysis", result);

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();

            // Image inline
            assertTrue(payload.contains("input_image"));
            // Document inline as input_file
            assertTrue(payload.contains("input_file"));
            assertTrue(payload.contains("file-xyz"));
        }

        @Test
        @DisplayName("Should throw for unsupported file type")
        void shouldThrowForUnsupportedType() throws Exception {
            File file = mockKristaFile("music.mp3");
            setupFileDownload(file, new byte[]{0x01, 0x02, 0x03, 0x04});

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), "Analyze this", null));
            assertTrue(ex.getMessage().contains("Unsupported file type"));
            assertTrue(ex.getMessage().contains("Supported formats"));
        }

        @Test
        @DisplayName("Should include system instructions when provided")
        void shouldIncludeInstructions() throws Exception {
            File file = mockKristaFile("photo.jpg");
            byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
            setupFileDownload(file, jpegBytes);
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Result");

            service.analyzeFiles(List.of(file), "Describe", "Respond in bullet points");

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("system"));
            assertTrue(payload.contains("Respond in bullet points"));
        }

        @Test
        @DisplayName("Should omit system message when instructions are blank")
        void shouldOmitInstructionsWhenBlank() throws Exception {
            File file = mockKristaFile("photo.jpg");
            byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
            setupFileDownload(file, jpegBytes);
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Result");

            service.analyzeFiles(List.of(file), "Describe", "   ");

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertFalse(payload.contains("system"));
        }

        @Test
        @DisplayName("Should propagate upload failure")
        void shouldPropagateUploadFailure() throws Exception {
            File file = mockKristaFile("report.pdf");
            byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
            setupFileDownload(file, pdfBytes);
            when(fileUploadService.uploadFileGenericNoDelay(pdfBytes, "report.pdf"))
                    .thenThrow(new OpenAiException("Upload failed"));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), "Summarize", null));
            assertEquals("Upload failed", ex.getMessage());
        }

        @Test
        @DisplayName("Should propagate download failure")
        void shouldPropagateDownloadFailure() throws Exception {
            File file = mockKristaFile("photo.png");
            when(mediaServerFileAccess.downloadFileFromMediaServer(file))
                    .thenThrow(new IOException("Download error"));

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), "Describe", null));
            assertTrue(ex.getMessage().contains("Failed to download file"));
        }

        @Test
        @DisplayName("Should throw when total size exceeds limit")
        void shouldThrowWhenSizeExceeded() throws Exception {
            File file = mockKristaFile("huge.png");
            byte[] hugeBytes = new byte[51 * 1024 * 1024]; // 51 MB
            setupFileDownload(file, hugeBytes);

            OpenAiException ex = assertThrows(OpenAiException.class,
                    () -> service.analyzeFiles(List.of(file), "Describe", null));
            assertTrue(ex.getMessage().contains("50 MB"));
        }

        @Test
        @DisplayName("Should process DOCX as input_file inline")
        void shouldProcessDocx() throws Exception {
            File file = mockKristaFile("letter.docx");
            byte[] docxBytes = new byte[]{0x50, 0x4B, 0x03, 0x04};
            setupFileDownload(file, docxBytes);
            when(fileUploadService.uploadFileGenericNoDelay(docxBytes, "letter.docx")).thenReturn("file-docx");
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("DOCX result");

            String result = service.analyzeFiles(List.of(file), "Summarize", null);

            assertEquals("DOCX result", result);
            verify(fileUploadService).uploadFileGenericNoDelay(docxBytes, "letter.docx");

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("input_file"));
            assertTrue(payload.contains("file-docx"));
        }

        @Test
        @DisplayName("Should include truncation auto in payload")
        void shouldIncludeTruncationAuto() throws Exception {
            File file = mockKristaFile("photo.png");
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            setupFileDownload(file, pngBytes);
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Result");

            service.analyzeFiles(List.of(file), "Describe", null);

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("\"truncation\":\"auto\""));
        }

        @Test
        @DisplayName("Should include model name in payload")
        void shouldIncludeModelName() throws Exception {
            File file = mockKristaFile("photo.png");
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            setupFileDownload(file, pngBytes);
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Result");

            service.analyzeFiles(List.of(file), "Describe", null);

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("gpt-4o"));
        }

        @Test
        @DisplayName("Should handle multiple documents inline")
        void shouldHandleMultipleDocuments() throws Exception {
            File pdf1 = mockKristaFile("report.pdf");
            File pdf2 = mockKristaFile("invoice.pdf");
            byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};

            setupFileDownload(pdf1, pdfBytes);
            setupFileDownload(pdf2, pdfBytes);
            when(fileUploadService.uploadFileGenericNoDelay(pdfBytes, "report.pdf")).thenReturn("file-1");
            when(fileUploadService.uploadFileGenericNoDelay(pdfBytes, "invoice.pdf")).thenReturn("file-2");
            when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("Multi result");

            String result = service.analyzeFiles(Arrays.asList(pdf1, pdf2), "Compare both", null);

            assertEquals("Multi result", result);
            verify(fileUploadService).uploadFileGenericNoDelay(pdfBytes, "report.pdf");
            verify(fileUploadService).uploadFileGenericNoDelay(pdfBytes, "invoice.pdf");

            ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
            verify(httpRequestService).sendResponsesApiRequest(captor.capture());
            String payload = captor.getValue().toString();
            assertTrue(payload.contains("file-1"));
            assertTrue(payload.contains("file-2"));
        }
    }
}
