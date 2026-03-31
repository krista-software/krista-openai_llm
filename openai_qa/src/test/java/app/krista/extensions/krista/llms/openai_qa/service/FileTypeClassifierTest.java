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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileTypeClassifier Tests")
class FileTypeClassifierTest {

    @Nested
    @DisplayName("classify Tests")
    class ClassifyTests {

        @Test
        @DisplayName("Should classify JPG as IMAGE")
        void shouldClassifyJpgAsImage() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("photo.jpg"));
        }

        @Test
        @DisplayName("Should classify JPEG as IMAGE")
        void shouldClassifyJpegAsImage() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("photo.jpeg"));
        }

        @Test
        @DisplayName("Should classify PNG as IMAGE")
        void shouldClassifyPngAsImage() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("screenshot.png"));
        }

        @Test
        @DisplayName("Should classify GIF as IMAGE")
        void shouldClassifyGifAsImage() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("animation.gif"));
        }

        @Test
        @DisplayName("Should classify WebP as IMAGE")
        void shouldClassifyWebpAsImage() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("image.webp"));
        }

        @Test
        @DisplayName("Should classify PDF as DOCUMENT")
        void shouldClassifyPdfAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("report.pdf"));
        }

        @Test
        @DisplayName("Should classify DOCX as DOCUMENT")
        void shouldClassifyDocxAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("letter.docx"));
        }

        @Test
        @DisplayName("Should classify PPTX as DOCUMENT")
        void shouldClassifyPptxAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("slides.pptx"));
        }

        @Test
        @DisplayName("Should classify XLSX as DOCUMENT")
        void shouldClassifyXlsxAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("data.xlsx"));
        }

        @Test
        @DisplayName("Should classify legacy DOC as DOCUMENT")
        void shouldClassifyDocAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("letter.doc"));
        }

        @Test
        @DisplayName("Should classify legacy PPT as DOCUMENT")
        void shouldClassifyPptAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("slides.ppt"));
        }

        @Test
        @DisplayName("Should classify legacy XLS as DOCUMENT")
        void shouldClassifyXlsAsDocument() {
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("data.xls"));
        }

        @Test
        @DisplayName("Should classify unknown extension as UNSUPPORTED")
        void shouldClassifyUnknownAsUnsupported() {
            assertEquals(FileTypeClassifier.FileCategory.UNSUPPORTED, FileTypeClassifier.classify("file.mp3"));
        }

        @Test
        @DisplayName("Should classify case-insensitively")
        void shouldClassifyCaseInsensitively() {
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("photo.JPG"));
            assertEquals(FileTypeClassifier.FileCategory.IMAGE, FileTypeClassifier.classify("photo.Png"));
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("report.PDF"));
            assertEquals(FileTypeClassifier.FileCategory.DOCUMENT, FileTypeClassifier.classify("file.Docx"));
        }

        @Test
        @DisplayName("Should return UNSUPPORTED for null file name")
        void shouldReturnUnsupportedForNull() {
            assertEquals(FileTypeClassifier.FileCategory.UNSUPPORTED, FileTypeClassifier.classify(null));
        }

        @Test
        @DisplayName("Should return UNSUPPORTED for file without extension")
        void shouldReturnUnsupportedForNoExtension() {
            assertEquals(FileTypeClassifier.FileCategory.UNSUPPORTED, FileTypeClassifier.classify("noext"));
        }
    }

    @Nested
    @DisplayName("getMimeType Tests")
    class GetMimeTypeTests {

        @Test
        @DisplayName("Should return correct MIME for images")
        void shouldReturnCorrectMimeForImages() {
            assertEquals("image/jpeg", FileTypeClassifier.getMimeType("photo.jpg"));
            assertEquals("image/jpeg", FileTypeClassifier.getMimeType("photo.jpeg"));
            assertEquals("image/png", FileTypeClassifier.getMimeType("file.png"));
            assertEquals("image/gif", FileTypeClassifier.getMimeType("file.gif"));
            assertEquals("image/webp", FileTypeClassifier.getMimeType("file.webp"));
        }

        @Test
        @DisplayName("Should return correct MIME for documents")
        void shouldReturnCorrectMimeForDocuments() {
            assertEquals("application/pdf", FileTypeClassifier.getMimeType("file.pdf"));
            assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    FileTypeClassifier.getMimeType("file.docx"));
            assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    FileTypeClassifier.getMimeType("file.pptx"));
            assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    FileTypeClassifier.getMimeType("file.xlsx"));
        }

        @Test
        @DisplayName("Should return octet-stream for unknown extension")
        void shouldReturnOctetStreamForUnknown() {
            assertEquals("application/octet-stream", FileTypeClassifier.getMimeType("file.xyz"));
        }
    }

    @Nested
    @DisplayName("detectImageMimeFromBytes Tests")
    class DetectImageMimeTests {

        @Test
        @DisplayName("Should detect PNG from bytes")
        void shouldDetectPng() {
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            assertEquals("image/png", FileTypeClassifier.detectImageMimeFromBytes(pngBytes));
        }

        @Test
        @DisplayName("Should detect JPEG from bytes")
        void shouldDetectJpeg() {
            byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
            assertEquals("image/jpeg", FileTypeClassifier.detectImageMimeFromBytes(jpegBytes));
        }

        @Test
        @DisplayName("Should detect GIF from bytes")
        void shouldDetectGif() {
            byte[] gifBytes = new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61};
            assertEquals("image/gif", FileTypeClassifier.detectImageMimeFromBytes(gifBytes));
        }

        @Test
        @DisplayName("Should detect WebP from bytes")
        void shouldDetectWebp() {
            byte[] webpBytes = new byte[]{
                    0x52, 0x49, 0x46, 0x46, // RIFF
                    0x00, 0x00, 0x00, 0x00, // size
                    0x57, 0x45, 0x42, 0x50  // WEBP
            };
            assertEquals("image/webp", FileTypeClassifier.detectImageMimeFromBytes(webpBytes));
        }

        @Test
        @DisplayName("Should return null for null bytes")
        void shouldReturnNullForNullBytes() {
            assertNull(FileTypeClassifier.detectImageMimeFromBytes(null));
        }

        @Test
        @DisplayName("Should return null for too-short bytes")
        void shouldReturnNullForShortBytes() {
            assertNull(FileTypeClassifier.detectImageMimeFromBytes(new byte[]{0x00, 0x01}));
        }

        @Test
        @DisplayName("Should return null for unrecognized bytes")
        void shouldReturnNullForUnrecognized() {
            assertNull(FileTypeClassifier.detectImageMimeFromBytes(new byte[]{0x00, 0x00, 0x00, 0x00}));
        }
    }
}
