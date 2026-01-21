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

package app.krista.extensions.krista.llms.openai_qa.service.impl;

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.dto.DocumentSet;
import app.krista.extensions.krista.llms.openai_qa.exception.OpenAiException;
import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.model.base.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FileProcessorImpl.
 * Tests file processing, validation, and size limit enforcement.
 */
@ExtendWith(MockitoExtension.class)
class FileProcessorImplTest {

    @Mock
    private KristaMediaClientUtil mockMediaClientUtil;

    @Mock
    private MediaServerFileAccess mockMediaServerFileAccess;

    @Mock
    private IDiscussionValidator mockValidator;

    @Mock
    private File mockKristaFile;

    @Mock
    private java.io.File mockJavaFile;

    private FileProcessorImpl fileProcessor;

    @BeforeEach
    void setUp() {
        fileProcessor = new FileProcessorImpl(mockMediaClientUtil, mockMediaServerFileAccess, mockValidator);
    }

    @Test
    @DisplayName("Should successfully process file within size limit")
    void testProcessFile_WithinSizeLimit_Success() throws Exception {
        // Given
        String fileName = "test.pdf";
        long fileSize = 100 * 1024 * 1024; // 100 MB
        byte[] fileBytes = new byte[1024];

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);
        when(mockMediaClientUtil.readFileAsBytes(mockJavaFile)).thenReturn(fileBytes);

        // When
        DocumentSet result = fileProcessor.processFile(mockKristaFile);

        // Then
        assertNotNull(result);
        verify(mockMediaServerFileAccess).downloadFileFromMediaServer(mockKristaFile);
        verify(mockJavaFile).length();
        verify(mockMediaClientUtil).readFileAsBytes(mockJavaFile);
    }

    @Test
    @DisplayName("Should successfully process file at exact size limit (512 MB)")
    void testProcessFile_AtExactSizeLimit_Success() throws Exception {
        // Given
        String fileName = "large.pdf";
        long fileSize = OpenAiConfiguration.MAX_FILE_SIZE_BYTES; // Exactly 512 MB
        byte[] fileBytes = new byte[1024];

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);
        when(mockMediaClientUtil.readFileAsBytes(mockJavaFile)).thenReturn(fileBytes);

        // When
        DocumentSet result = fileProcessor.processFile(mockKristaFile);

        // Then
        assertNotNull(result);
        verify(mockJavaFile).length();
    }

    @Test
    @DisplayName("Should throw exception when file exceeds size limit")
    void testProcessFile_ExceedsSizeLimit_ThrowsException() throws Exception {
        // Given
        String fileName = "too_large.pdf";
        long fileSize = OpenAiConfiguration.MAX_FILE_SIZE_BYTES + 1; // 512 MB + 1 byte

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> {
            fileProcessor.processFile(mockKristaFile);
        });

        assertTrue(exception.getMessage().contains("exceeds maximum size limit"));
        assertTrue(exception.getMessage().contains(fileName));
        assertTrue(exception.getMessage().contains("512 MB"));
        verify(mockMediaClientUtil, never()).readFileAsBytes(any());
    }

    @Test
    @DisplayName("Should throw exception when file is 1 GB (way over limit)")
    void testProcessFile_VeryLargeFile_ThrowsException() throws Exception {
        // Given
        String fileName = "huge_file.pdf";
        long fileSize = 1024L * 1024 * 1024; // 1 GB

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> {
            fileProcessor.processFile(mockKristaFile);
        });

        assertTrue(exception.getMessage().contains("exceeds maximum size limit"));
        assertTrue(exception.getMessage().contains("1024 MB"));
        assertTrue(exception.getMessage().contains("512 MB"));
    }

    @Test
    @DisplayName("Should process small file successfully")
    void testProcessFile_SmallFile_Success() throws Exception {
        // Given
        String fileName = "small.pdf";
        long fileSize = 1024 * 1024; // 1 MB
        byte[] fileBytes = new byte[100];

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);
        when(mockMediaClientUtil.readFileAsBytes(mockJavaFile)).thenReturn(fileBytes);

        // When
        DocumentSet result = fileProcessor.processFile(mockKristaFile);

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when file download fails")
    void testProcessFile_DownloadFails_ThrowsException() throws Exception {
        // Given
        String fileName = "test.pdf";
        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile))
            .thenThrow(new IOException("Download failed"));

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> {
            fileProcessor.processFile(mockKristaFile);
        });

        assertTrue(exception.getMessage().contains("Failed to process file"));
    }

    @Test
    @DisplayName("Should throw exception when reading file bytes fails")
    void testProcessFile_ReadBytesFails_ThrowsException() throws Exception {
        // Given
        String fileName = "test.pdf";
        long fileSize = 10 * 1024 * 1024; // 10 MB

        when(mockKristaFile.getFileName()).thenReturn(fileName);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(mockKristaFile)).thenReturn(mockJavaFile);
        when(mockJavaFile.length()).thenReturn(fileSize);
        when(mockMediaClientUtil.readFileAsBytes(mockJavaFile))
            .thenThrow(new IOException("Read failed"));

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> {
            fileProcessor.processFile(mockKristaFile);
        });

        assertTrue(exception.getMessage().contains("Failed to process file"));
    }

    @Test
    @DisplayName("Should successfully process multiple files within size limits")
    void testProcessFiles_AllWithinLimits_Success() throws Exception {
        // Given
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        java.io.File javaFile1 = mock(java.io.File.class);
        java.io.File javaFile2 = mock(java.io.File.class);

        when(file1.getFileName()).thenReturn("file1.pdf");
        when(file2.getFileName()).thenReturn("file2.pdf");
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(file1)).thenReturn(javaFile1);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(file2)).thenReturn(javaFile2);
        when(javaFile1.length()).thenReturn(100L * 1024 * 1024); // 100 MB
        when(javaFile2.length()).thenReturn(200L * 1024 * 1024); // 200 MB
        when(mockMediaClientUtil.readFileAsBytes(any())).thenReturn(new byte[1024]);

        List<File> files = Arrays.asList(file1, file2);

        // When
        List<DocumentSet> results = fileProcessor.processFiles(files);

        // Then
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Should throw exception when one file in batch exceeds size limit")
    void testProcessFiles_OneFileExceedsLimit_ThrowsException() throws Exception {
        // Given
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        java.io.File javaFile1 = mock(java.io.File.class);
        java.io.File javaFile2 = mock(java.io.File.class);

        when(file1.getFileName()).thenReturn("file1.pdf");
        when(file2.getFileName()).thenReturn("file2_too_large.pdf");
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(file1)).thenReturn(javaFile1);
        when(mockMediaServerFileAccess.downloadFileFromMediaServer(file2)).thenReturn(javaFile2);
        when(javaFile1.length()).thenReturn(100L * 1024 * 1024); // 100 MB
        when(javaFile2.length()).thenReturn(OpenAiConfiguration.MAX_FILE_SIZE_BYTES + 1); // Over limit
        when(mockMediaClientUtil.readFileAsBytes(javaFile1)).thenReturn(new byte[1024]);

        List<File> files = Arrays.asList(file1, file2);

        // When & Then
        OpenAiException exception = assertThrows(OpenAiException.class, () -> {
            fileProcessor.processFiles(files);
        });

        assertTrue(exception.getMessage().contains("File processing errors"));
        assertTrue(exception.getMessage().contains("file2_too_large.pdf"));
    }

    @Test
    @DisplayName("Should return true for PDF file format")
    void testIsSupportedFormat_PDFFile_ReturnsTrue() {
        // When & Then
        assertTrue(fileProcessor.isSupportedFormat("document.pdf"));
        assertTrue(fileProcessor.isSupportedFormat("DOCUMENT.PDF"));
        assertTrue(fileProcessor.isSupportedFormat("Document.Pdf"));
    }

    @Test
    @DisplayName("Should return false for non-PDF file formats")
    void testIsSupportedFormat_NonPDFFile_ReturnsFalse() {
        // When & Then
        assertFalse(fileProcessor.isSupportedFormat("document.docx"));
        assertFalse(fileProcessor.isSupportedFormat("image.png"));
        assertFalse(fileProcessor.isSupportedFormat("text.txt"));
        assertFalse(fileProcessor.isSupportedFormat(null));
    }
}

