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
import app.krista.extensions.krista.llms.openai_qa.model.ContentBlock;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.model.base.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Core orchestrator for the "Process Files" catalog request.
 * Downloads files from Krista, classifies them, prepares content blocks,
 * builds an OpenAI Responses API payload, and returns the parsed answer.
 *
 * Documents are sent inline via input_file blocks with truncation: auto.
 * Images are sent inline as base64.
 * If the total content exceeds the model's context window, OpenAI returns
 * a context_length_exceeded error which is translated into an actionable
 * user message by HttpRequestService.
 */
public class FileAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(FileAnalysisService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MediaServerFileAccess mediaServerFileAccess;
    private final KristaMediaClientUtil kristaMediaClientUtil;
    private final FileUploadService fileUploadService;
    private final HttpRequestService httpRequestService;
    private final String modelName;

    public FileAnalysisService(MediaServerFileAccess mediaServerFileAccess,
                               KristaMediaClientUtil kristaMediaClientUtil,
                               FileUploadService fileUploadService,
                               HttpRequestService httpRequestService,
                               String modelName) {
        this.mediaServerFileAccess = mediaServerFileAccess;
        this.kristaMediaClientUtil = kristaMediaClientUtil;
        this.fileUploadService = fileUploadService;
        this.httpRequestService = httpRequestService;
        this.modelName = modelName;
    }

    /**
     * Analyzes files with the given prompt using OpenAI Responses API.
     * Documents are uploaded and referenced via input_file blocks.
     * Images are sent inline as base64.
     *
     * @param files        list of Krista file objects
     * @param prompt       user question / instruction
     * @param instructions optional system instructions (may be null or blank)
     * @return the AI's text response
     */
    public String analyzeFiles(List<File> files, String prompt, String instructions) throws OpenAiException {
        validateInputs(files, prompt);

        List<ContentBlock> contentBlocks = processFiles(files);

        ObjectNode payload = buildPayload(contentBlocks, prompt, instructions);

        logger.info("Sending Process Files request with {} content block(s)", contentBlocks.size());
        return httpRequestService.sendResponsesApiRequest(payload);
    }

    private void validateInputs(List<File> files, String prompt) throws OpenAiException {
        if (files == null || files.isEmpty()) {
            throw new OpenAiException("At least one file is required");
        }
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new OpenAiException("Prompt is required");
        }
        if (files.size() > OpenAiConfiguration.MAX_FILES_PER_REQUEST) {
            throw new OpenAiException(String.format("Too many files: %d. Maximum allowed is %d",
                    files.size(), OpenAiConfiguration.MAX_FILES_PER_REQUEST));
        }
    }

    private List<ContentBlock> processFiles(List<File> files) throws OpenAiException {
        List<ContentBlock> blocks = new ArrayList<>();
        long totalSize = 0;

        for (File file : files) {
            String fileName = file.getFileName();
            logger.debug("Processing file: {}", fileName);

            byte[] fileBytes = downloadAndReadFile(file);
            totalSize += fileBytes.length;

            if (totalSize > OpenAiConfiguration.MAX_TOTAL_FILE_SIZE_BYTES) {
                throw new OpenAiException("Total file size exceeds 50 MB limit");
            }

            FileTypeClassifier.FileCategory category = FileTypeClassifier.classify(fileName);

            switch (category) {
                case IMAGE:
                    blocks.add(createImageBlock(fileBytes, fileName));
                    break;
                case DOCUMENT:
                    blocks.add(createDocumentBlock(fileBytes, fileName));
                    break;
                default:
                    throw new OpenAiException(String.format(
                            "Unsupported file type: '%s'. %s",
                            fileName, FileTypeClassifier.getSupportedFormatsDescription()));
            }
        }
        return blocks;
    }

    private byte[] downloadAndReadFile(File file) throws OpenAiException {
        try {
            java.io.File javaFile = mediaServerFileAccess.downloadFileFromMediaServer(file);
            return kristaMediaClientUtil.readFileAsBytes(javaFile);
        } catch (IOException cause) {
            throw new OpenAiException("Failed to download file: " + file.getFileName() + " - " + cause.getMessage(), cause);
        }
    }

    private ContentBlock createImageBlock(byte[] fileBytes, String fileName) {
        String mime = FileTypeClassifier.detectImageMimeFromBytes(fileBytes);
        if (mime == null) {
            mime = FileTypeClassifier.getMimeType(fileName);
        }
        String base64 = Base64.getEncoder().encodeToString(fileBytes);
        String dataUrl = "data:" + mime + ";base64," + base64;
        logger.debug("Created image block for: {} ({})", fileName, mime);
        return ContentBlock.imageBlock(dataUrl, fileName);
    }

    private ContentBlock createDocumentBlock(byte[] fileBytes, String fileName) throws OpenAiException {
        String fileId = fileUploadService.uploadFileGenericNoDelay(fileBytes, fileName);
        logger.debug("Created document block for: {} (fileId: {})", fileName, fileId);
        return ContentBlock.documentBlock(fileId, fileName);
    }

    /**
     * Builds the Responses API payload with all content blocks inline.
     * Images are sent as input_image (base64), documents as input_file (file ID).
     */
    private ObjectNode buildPayload(List<ContentBlock> contentBlocks, String prompt, String instructions) {
        ObjectNode payload = OBJECT_MAPPER.createObjectNode();
        payload.put(OpenAiConfiguration.PARAM_MODEL, modelName);
        payload.put(OpenAiConfiguration.PARAM_TRUNCATION, OpenAiConfiguration.TRUNCATION_AUTO);

        ArrayNode inputArray = OBJECT_MAPPER.createArrayNode();

        // Optional system instructions
        if (instructions != null && !instructions.trim().isEmpty()) {
            ObjectNode systemMessage = OBJECT_MAPPER.createObjectNode();
            systemMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_SYSTEM);
            systemMessage.put(OpenAiConfiguration.PARAM_CONTENT, instructions.trim());
            inputArray.add(systemMessage);
        }

        // User message with content blocks
        ObjectNode userMessage = OBJECT_MAPPER.createObjectNode();
        userMessage.put(OpenAiConfiguration.PARAM_ROLE, OpenAiConfiguration.ROLE_USER);

        ArrayNode contentArray = OBJECT_MAPPER.createArrayNode();

        for (ContentBlock block : contentBlocks) {
            if (block.getType() == ContentBlock.BlockType.INPUT_IMAGE) {
                ObjectNode imageNode = OBJECT_MAPPER.createObjectNode();
                imageNode.put(OpenAiConfiguration.PARAM_TYPE, OpenAiConfiguration.TYPE_INPUT_IMAGE);
                imageNode.put("image_url", block.getDataUrl());
                contentArray.add(imageNode);
            } else {
                ObjectNode fileNode = OBJECT_MAPPER.createObjectNode();
                fileNode.put(OpenAiConfiguration.PARAM_TYPE, OpenAiConfiguration.TYPE_INPUT_FILE);
                fileNode.put(OpenAiConfiguration.PARAM_FILE_ID, block.getFileId());
                contentArray.add(fileNode);
            }
        }

        // Add text prompt
        ObjectNode textBlock = OBJECT_MAPPER.createObjectNode();
        textBlock.put(OpenAiConfiguration.PARAM_TYPE, OpenAiConfiguration.TYPE_INPUT_TEXT);
        textBlock.put(OpenAiConfiguration.PARAM_TEXT, prompt.trim());
        contentArray.add(textBlock);

        userMessage.set(OpenAiConfiguration.PARAM_CONTENT, contentArray);
        inputArray.add(userMessage);

        payload.set(OpenAiConfiguration.PARAM_INPUT, inputArray);

        logger.info("Built Responses API payload with model={}, blocks={}", modelName, contentBlocks.size());
        return payload;
    }
}
