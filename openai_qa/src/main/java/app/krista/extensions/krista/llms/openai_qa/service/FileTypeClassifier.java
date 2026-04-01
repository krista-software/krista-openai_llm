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

/**
 * Utility for classifying files by type and resolving MIME types.
 * Supports images (JPG, PNG, GIF, WebP) and documents (PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX).
 */
public final class FileTypeClassifier {

    public enum FileCategory {
        IMAGE,
        DOCUMENT,
        UNSUPPORTED
    }

    private FileTypeClassifier() {
    }

    /**
     * Classifies a file as IMAGE, DOCUMENT, or UNSUPPORTED based on extension.
     */
    public static FileCategory classify(String fileName) {
        String ext = getExtension(fileName);
        switch (ext) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
                return FileCategory.IMAGE;
            case "pdf":
            case "doc":
            case "docx":
            case "ppt":
            case "pptx":
            case "xls":
            case "xlsx":
                return FileCategory.DOCUMENT;
            default:
                return FileCategory.UNSUPPORTED;
        }
    }

    /**
     * Returns the MIME type for a given file name based on extension.
     */
    public static String getMimeType(String fileName) {
        String ext = getExtension(fileName);
        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Detects image MIME type from raw bytes using magic-byte signatures.
     *
     * @return MIME type string or null if not a recognized image
     */
    public static String detectImageMimeFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return null;
        }
        // PNG: 89 50 4E 47
        if ((bytes[0] & 0xFF) == 0x89 && (bytes[1] & 0xFF) == 0x50
                && (bytes[2] & 0xFF) == 0x4E && (bytes[3] & 0xFF) == 0x47) {
            return "image/png";
        }
        // JPEG: FF D8
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) {
            return "image/jpeg";
        }
        // GIF: 47 49 46
        if ((bytes[0] & 0xFF) == 0x47 && (bytes[1] & 0xFF) == 0x49
                && (bytes[2] & 0xFF) == 0x46) {
            return "image/gif";
        }
        // WebP: bytes 8-11 = "WEBP"
        if (bytes.length >= 12
                && (bytes[8] & 0xFF) == 0x57 && (bytes[9] & 0xFF) == 0x45
                && (bytes[10] & 0xFF) == 0x42 && (bytes[11] & 0xFF) == 0x50) {
            return "image/webp";
        }
        return null;
    }

    /**
     * Returns the supported file types description for error messages.
     */
    public static String getSupportedFormatsDescription() {
        return "Supported formats: Images (JPG, PNG, GIF, WebP), Documents (PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX)";
    }

    private static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
