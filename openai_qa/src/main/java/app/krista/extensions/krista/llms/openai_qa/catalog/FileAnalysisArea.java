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

package app.krista.extensions.krista.llms.openai_qa.catalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.krista.llms.openai_qa.service.FileAnalysisService;
import app.krista.extensions.krista.llms.openai_qa.service.FileUploadService;
import app.krista.extensions.krista.llms.openai_qa.service.HttpRequestService;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import app.krista.model.base.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.util.ExtensionUtil.createErrorResponse;
import static app.krista.extensions.krista.llms.openai_qa.util.ExtensionUtil.validateAttributes;

/**
 * Catalog area for the "Process Files" request.
 * Accepts any combination of images and documents plus a prompt,
 * sends them to OpenAI's Responses API in a single request, and returns the AI's response.
 */
@Domain(id = "catEntryDomain_6568c455-2416-4fcf-8d7a-2f3e0bef059b",
        name = "LLMs",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "f7a1c3d9-5e28-4b0a-9c6f-8d2e1f4a7b3c")
public class FileAnalysisArea {

    private static final Logger logger = LoggerFactory.getLogger(FileAnalysisArea.class);

    private final FileAnalysisService fileAnalysisService;
    private final Map<String, Object> attributes;

    @Inject
    public FileAnalysisArea(MediaServerFileAccess mediaServerFileAccess,
                            KristaMediaClientUtil kristaMediaClientUtil,
                            FileUploadService fileUploadService,
                            HttpRequestService httpRequestService,
                            Invoker invoker) {
        this.attributes = invoker.getAttributes();
        String modelName = resolveModelName(attributes);
        this.fileAnalysisService = new FileAnalysisService(
                mediaServerFileAccess, kristaMediaClientUtil,
                fileUploadService, httpRequestService, modelName);
    }

    @CatalogRequest(
            id = "localDomainRequest_b4e2c7a1-3f58-4d9e-a6b0-1c8d5e2f9a4b",
            name = "Process Files",
            description = "Analyze images and documents together in a single request. "
                    + "Supports images (JPG, PNG, GIF, WebP) and documents (PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX). "
                    + "Pass any combination of files with a prompt and get an AI-generated text response. "
                    + "Max 20 files, 50 MB total. Requires a vision-capable model (e.g., gpt-4o, gpt-4.1).",
            area = "File Analysis",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field(name = "Response", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L"),
                    @Attribute(name = "tooltip", value = "AI-generated text response based on the provided files and prompt.")},
            options = {})
    public ExtensionResponse processFiles(
            @Field.File(name = "Files", multipleFileUpload = true, required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                            @Attribute(name = "tooltip", value = "Upload one or more files. Supported: JPG, PNG, GIF, WebP, PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX. Max 20 files, 50 MB total.")},
                    options = {}) List<File> files,
            @Field.Text(name = "Prompt", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S"),
                            @Attribute(name = "tooltip", value = "The question or instruction for the AI about the uploaded files.")},
                    options = {}) String prompt,
            @Field(name = "Instructions", type = "Paragraph", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "L"),
                            @Attribute(name = "tooltip", value = "Optional system-level guidance for the AI's behavior (e.g., 'Respond in bullet points').")},
                    options = {}) String instructions) {

        logger.info("Process Files request received with {} file(s)", files != null ? files.size() : 0);

        try {
            ExtensionResponse validationResponse = validateAttributes(attributes);
            if (validationResponse != null) {
                return validationResponse;
            }

            String response = fileAnalysisService.analyzeFiles(files, prompt, instructions);
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS,
                    Map.of("Response", response), null, null, null);

        } catch (Exception cause) {
            logger.info("Process Files request completed with error: {}", cause.getMessage());
            logger.debug("Process Files error details", cause);
            return createErrorResponse(cause.getMessage());
        }
    }

    private static String resolveModelName(Map<String, Object> attrs) {
        Object model = attrs.get(OpenAIConstants.MODEL);
        if (model != null) {
            String modelStr = model.toString();
            // Map display names to API model IDs
            return switch (modelStr) {
                case OpenAIConstants.GPT_4_1 -> "gpt-4.1";
                case OpenAIConstants.GPT_4_1_MINI -> "gpt-4.1-mini";
                case OpenAIConstants.GPT_4_1_NANO -> "gpt-4.1-nano";
                case OpenAIConstants.GPT_LATEST -> "chatgpt-4o-latest";
                case OpenAIConstants.GPT_4 -> "gpt-4o";
                case OpenAIConstants.GPT_35 -> "gpt-3.5-turbo";
                default -> modelStr;
            };
        }
        return "gpt-4o";
    }
}
