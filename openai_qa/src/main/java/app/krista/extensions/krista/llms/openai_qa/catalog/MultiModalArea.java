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
import app.krista.extensions.krista.llms.openai_qa.impl.QueryImpl;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.ksdk.files.FileRepository;
import app.krista.model.base.File;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static app.krista.extensions.krista.llms.openai_qa.util.ExtensionUtil.validateAttributes;

/**
 * Catalog area for multimodal AI operations including image analysis and processing.
 *
 * <p>This class provides catalog requests for vision-enabled AI capabilities, allowing
 * users to analyze images, answer questions about visual content, and perform
 * multimodal interactions combining text and images. It leverages OpenAI's vision
 * models to provide comprehensive image understanding.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Image Summarization:</strong> Generate descriptive summaries of uploaded images</li>
 *   <li><strong>Visual Q&A:</strong> Answer questions about image content</li>
 *   <li><strong>Base64 Support:</strong> Process images from base64-encoded strings</li>
 *   <li><strong>Multiple Image Analysis:</strong> Analyze multiple images simultaneously</li>
 *   <li><strong>File Integration:</strong> Seamless integration with Krista file repository</li>
 * </ul>
 *
 * <h3>Supported Image Formats:</h3>
 * <ul>
 *   <li>JPEG (.jpg, .jpeg)</li>
 *   <li>PNG (.png)</li>
 *   <li>GIF (.gif)</li>
 *   <li>WebP (.webp)</li>
 * </ul>
 *
 * <h3>Architecture:</h3>
 * <p>Uses dependency injection for core services and integrates with:</p>
 * <ul>
 *   <li>{@link QueryImpl} - Core AI query processing</li>
 *   <li>{@link MediaServerFileAccess} - File access and processing</li>
 *   <li>{@link FileRepository} - Krista file management</li>
 * </ul>
 *
 * @author Krista Extensions Team
 * @version 2.1.5
 * @since 1.0.0
 * @see QueryImpl for underlying AI processing
 * @see MediaServerFileAccess for file handling
 */
@Domain(id = "catEntryDomain_6568c455-2416-4fcf-8d7a-2f3e0bef059b",
        name = "LLMs",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "a8c3f456-2ad2-4787-97a2-28045d59cbd7")
public class MultiModalArea {

    /** Core AI query implementation for image processing. */
    private final QueryImpl queryImpl;

    /** Media server file access service for image retrieval. */
    private final MediaServerFileAccess mediaServerFileAccess;

    /** Extension configuration attributes. */
    private final Map<String, Object> attributes;

    /** Krista file repository for file management. */
    private final FileRepository fileRepository;

    /**
     * Constructs the MultiModalArea with required dependencies.
     *
     * @param queryImpl The query implementation for AI processing
     * @param mediaServerFileAccess Service for accessing media files
     * @param invoke The invoker providing configuration attributes
     * @param fileRepository Repository for file management operations
     */
    @Inject
    public MultiModalArea(QueryImpl queryImpl, MediaServerFileAccess mediaServerFileAccess, Invoker invoke, FileRepository fileRepository) {
        this.queryImpl = queryImpl;
        this.mediaServerFileAccess = mediaServerFileAccess;
        this.attributes = invoke.getAttributes();
        this.fileRepository = fileRepository;
    }

    /**
     * Generates a comprehensive summary of an uploaded image.
     *
     * <p>This catalog request analyzes an uploaded image file and provides a detailed
     * textual summary of its contents. The AI model examines visual elements, objects,
     * scenes, text, and other relevant details to create a comprehensive description
     * suitable for documentation, accessibility, or content management purposes.</p>
     *
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Downloads the image file from the media server</li>
     *   <li>Converts the image to a format suitable for AI processing</li>
     *   <li>Sends the image to OpenAI's vision model for analysis</li>
     *   <li>Returns a comprehensive textual summary</li>
     * </ol>
     *
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Content cataloging and indexing</li>
     *   <li>Accessibility descriptions for visually impaired users</li>
     *   <li>Automated image documentation</li>
     *   <li>Content moderation and classification</li>
     * </ul>
     *
     * @param inputImage The image file to analyze (JPEG, PNG, GIF, WebP supported)
     * @return A comprehensive textual summary of the image contents
     * @throws RuntimeException if image processing or AI analysis fails
     */
    @CatalogRequest(
            id = "localDomainRequest_cf1499da-7373-473a-81af-5d71bc319220",
            name = "Summarize Image",
            description = "This request takes in an Image as file input and brings in a summary of that image",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Summary", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public String summarizeImage(
            @Field.File(name = "Input Image", attributes = {@Attribute(name = "visualWidth", value = "S")}) File inputImage) {
        try {
            java.io.File inputFile = mediaServerFileAccess.downloadFileFromMediaServer(inputImage);
            return queryImpl.constructPromptAndQueryModelForImageFile("Please provide a comprehensive summary of the given image.", "", inputFile);
//            return queryImpl.constructPromptAndQueryModelForImageFile("Please summarise the given image and provide details in the summary which would be useful in similarity search and Q&A", "", inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a comprehensive summary from a base64-encoded image string.
     *
     * <p>This catalog request analyzes an image provided as a base64-encoded string
     * and generates a detailed textual summary. This method is useful when images
     * are already encoded (e.g., from web uploads, API responses, or embedded data)
     * and don't require file system access.</p>
     *
     * <h3>Input Format:</h3>
     * <p>Accepts base64-encoded image data with or without data URL prefix:</p>
     * <ul>
     *   <li>Raw base64: {@code /9j/4AAQSkZJRgABAQEAYABgAAD...}</li>
     *   <li>Data URL: {@code data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...}</li>
     * </ul>
     *
     * <h3>Advantages:</h3>
     * <ul>
     *   <li>No file system dependencies</li>
     *   <li>Direct processing from encoded data</li>
     *   <li>Suitable for API integrations</li>
     *   <li>Faster processing (no file I/O)</li>
     * </ul>
     *
     * @param base64EncodedImageString Base64-encoded image data
     * @return A comprehensive textual summary of the image contents
     */
    @CatalogRequest(
            id = "localDomainRequest_9470936c-7626-4005-86f2-3c7a940f33fe",
            name = "Summarize Image from Base 64 Encoded",
            description = "Summarise an image taking in Base 64 encoded string as an input",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Image Summary", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public String summarizeImageFromBase64Encoded(
            @Field.Text(name = "Base 64 Encoded Image String", attributes = {@Attribute(name = "visualWidth", value = "S")}) String base64EncodedImageString) {
        return queryImpl.constructPromptAndQueryModelForImageString("Please provide a comprehensive summary of the given image.", "", base64EncodedImageString);
//        return queryImpl.constructPromptAndQueryModelForImageString("Please summarise the given image and provide details in the summary which would be useful in similarity search and Q&A", "", base64EncodedImageString);
    }

    /**
     * Answers specific questions about a base64-encoded image.
     *
     * <p>This catalog request performs visual question answering (VQA) on an image
     * provided as base64-encoded data. Users can ask specific questions about the
     * image content, and the AI will analyze the visual information to provide
     * accurate, contextual answers.</p>
     *
     * <h3>Question Types:</h3>
     * <ul>
     *   <li><strong>Object Detection:</strong> "What objects are in this image?"</li>
     *   <li><strong>Scene Description:</strong> "Where was this photo taken?"</li>
     *   <li><strong>Text Recognition:</strong> "What text is visible in the image?"</li>
     *   <li><strong>Color Analysis:</strong> "What are the dominant colors?"</li>
     *   <li><strong>Counting:</strong> "How many people are in the image?"</li>
     *   <li><strong>Spatial Relationships:</strong> "What is to the left of the building?"</li>
     * </ul>
     *
     * <h3>Context Usage:</h3>
     * <p>Optional context can provide additional information to improve answer accuracy:</p>
     * <ul>
     *   <li>Background information about the image</li>
     *   <li>Specific domain knowledge</li>
     *   <li>Previous conversation context</li>
     * </ul>
     *
     * @param base64EncodedImageString Base64-encoded image data
     * @param question The specific question to answer about the image
     * @param context Optional context to guide the analysis (can be null or empty)
     * @return A detailed answer to the question based on image analysis
     */
    @CatalogRequest(
            id = "localDomainRequest_d8ce9a7d-58d8-4f2a-8b05-9193e17a4c3b",
            name = "Answer Question From Base 64 Encoded Image",
            description = "This request takes in a base64 encoded string from an image and asnwers a question on the basis of that",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Answer", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public String answerQuestionFromBase64EncodedImage(
            @Field.Text(name = "Base 64 Encoded Image String", attributes = {@Attribute(name = "visualWidth", value = "S")}) String base64EncodedImageString,
            @Field.Text(name = "Question", attributes = {@Attribute(name = "visualWidth", value = "S")}) String question,
            @Field.Text(name = "Context", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}) String context) {
        return queryImpl.constructPromptAndQueryModelForImageString(question, context, base64EncodedImageString);
    }

    /**
     * Answers specific questions about an uploaded image file.
     *
     * <p>This catalog request performs visual question answering (VQA) on an uploaded
     * image file. It combines file handling with AI vision capabilities to provide
     * accurate answers to user questions about image content. The method handles
     * file download, processing, and AI analysis seamlessly.</p>
     *
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Downloads the image file from the media server</li>
     *   <li>Converts the file to a format suitable for AI processing</li>
     *   <li>Sends the image and question to the vision model</li>
     *   <li>Returns a detailed answer based on visual analysis</li>
     * </ol>
     *
     * <h3>Advantages over Base64 Method:</h3>
     * <ul>
     *   <li>Direct file upload support</li>
     *   <li>No encoding/decoding overhead</li>
     *   <li>Better integration with file management systems</li>
     *   <li>Automatic file format handling</li>
     * </ul>
     *
     * @param inputImage The image file to analyze
     * @param question The specific question to answer about the image
     * @param context Optional context to guide the analysis (can be null or empty)
     * @return A detailed answer to the question based on image analysis
     * @throws RuntimeException if file processing or AI analysis fails
     */
    @CatalogRequest(
            id = "localDomainRequest_d2e60c5f-cdeb-457c-ae3b-8bf97e114258",
            name = "Answer Question from Image",
            description = "Request Description",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Answer", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public String answerQuestionFromImage(
            @Field.File(name = "Input Image", attributes = {@Attribute(name = "visualWidth", value = "S")}) File inputImage,
            @Field.Text(name = "Question", attributes = {@Attribute(name = "visualWidth", value = "S")}) String question,
            @Field.Text(name = "Context", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}) String context) {
        java.io.File inputFile = null;
        try {
            inputFile = mediaServerFileAccess.downloadFileFromMediaServer(inputImage);
            return queryImpl.constructPromptAndQueryModelForImageFile(question, context, inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates an image from a text prompt using OpenAI's DALL-E model.
     *
     * <p>This catalog request creates original images based on textual descriptions
     * using OpenAI's image generation capabilities. The generated image is stored
     * in the Krista file repository and a media ID is returned for future reference.</p>
     *
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Validates extension configuration attributes</li>
     *   <li>Creates an ImageGenerator instance with API credentials</li>
     *   <li>Sends the prompt to OpenAI's DALL-E API</li>
     *   <li>Stores the generated image in the file repository</li>
     *   <li>Returns the media ID for the stored image</li>
     * </ol>
     *
     * <h3>Prompt Guidelines:</h3>
     * <ul>
     *   <li><strong>Be Descriptive:</strong> Include details about style, colors, composition</li>
     *   <li><strong>Specify Style:</strong> "photorealistic", "cartoon", "oil painting", etc.</li>
     *   <li><strong>Include Context:</strong> Setting, mood, lighting conditions</li>
     *   <li><strong>Avoid Copyrighted Content:</strong> Don't reference specific brands or characters</li>
     * </ul>
     *
     * <h3>Example Prompts:</h3>
     * <ul>
     *   <li>"A serene mountain landscape at sunset with a lake reflection"</li>
     *   <li>"A modern office workspace with plants and natural lighting"</li>
     *   <li>"An abstract geometric pattern in blue and gold colors"</li>
     * </ul>
     *
     * @param prompt The text description of the image to generate
     * @return ExtensionResponse containing the media ID on success, or error details on failure
     */
    @CatalogRequest(
            id = "localDomainRequest_142590f5-5dd8-47bc-9444-4f378c63a73b",
            name = "Generate Image",
            description = "Generate image using given prompt and return media id.",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Media Id", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public ExtensionResponse generateImage(
            @Field.Text(name = "Prompt", attributes = {@Attribute(name = "visualWidth", value = "S")}) String prompt) {
        try {
            ExtensionResponse validationResponse = validateAttributes(attributes);
            if (validationResponse != null) {
                return validationResponse;
            }
            ImageGenerator imageGenerator = ImageGenerator.getConnection(attributes);
            String iconOfTopic = imageGenerator.generateIcon(prompt, fileRepository);
            if (iconOfTopic == null) {
                ExtensionResponse.Error error = new ExtensionResponse.Error("No content generated", System.currentTimeMillis(), ExtensionResponse.Error.ExceptionType.INPUT_ERROR, null);
                return new ExtensionResponse(ExtensionResponse.Result.FAILURE, Map.of(), error, null, null);
            }
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("Media Id", iconOfTopic), null, null, null);
        } catch (Exception cause) {
            ExtensionResponse.Error error = new ExtensionResponse.Error("No content generated", System.currentTimeMillis(), ExtensionResponse.Error.ExceptionType.INPUT_ERROR, null);
            return new ExtensionResponse(ExtensionResponse.Result.FAILURE, Map.of(), error, null, null);
        }

    }

    /**
     * Answers questions based on multiple base64-encoded images with advanced parameter control.
     *
     * <p>This advanced catalog request enables sophisticated multimodal analysis by processing
     * multiple images simultaneously and providing fine-grained control over AI model parameters.
     * It's ideal for complex visual analysis tasks requiring comparison, correlation, or
     * comprehensive understanding across multiple visual inputs.</p>
     *
     * <h3>Input Format:</h3>
     * <p>Each image is provided as a map containing:</p>
     * <ul>
     *   <li><strong>"File Name":</strong> Identifier for the image (e.g., "image1.jpg")</li>
     *   <li><strong>"Base 64 Encoded Image String":</strong> Base64-encoded image data</li>
     * </ul>
     *
     * <h3>Advanced Parameters:</h3>
     * <ul>
     *   <li><strong>Temperature (0.0-1.0):</strong> Controls response creativity and randomness</li>
     *   <li><strong>Top P (0.0-1.0):</strong> Nucleus sampling for response diversity</li>
     *   <li><strong>Max Tokens:</strong> Maximum response length</li>
     *   <li><strong>Top K:</strong> Token consideration limit (ignored by OpenAI API)</li>
     * </ul>
     *
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li><strong>Comparative Analysis:</strong> "Compare these product images"</li>
     *   <li><strong>Sequential Analysis:</strong> "Describe the progression in these images"</li>
     *   <li><strong>Multi-angle Analysis:</strong> "Analyze this object from different angles"</li>
     *   <li><strong>Document Processing:</strong> "Extract information from these document pages"</li>
     * </ul>
     *
     * @param base64EncodedImages List of maps containing filename and base64 image data
     * @param question The question to answer based on all provided images
     * @param context Optional context to guide the analysis
     * @param temperature Response creativity (0.0-1.0), null for default
     * @param topK Token consideration limit (ignored by OpenAI API)
     * @param topP Nucleus sampling threshold (0.0-1.0), null for default
     * @param maxTokens Maximum response tokens, null for automatic calculation
     * @return ExtensionResponse containing the answer or error details
     */
    @CatalogRequest(
            id = "localDomainRequest_e938c1da-32ab-42d3-90b9-bd99be9697de",
            name = "Answer Question From Base 64 Encoded Images",
            description = "Answer user questions based on one or more Base64-encoded images and context.",
            area = "Multi Modal",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "Answer", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")})
    public ExtensionResponse answerQuestionFromBase64EncodedImages(
            @Field.Desc(name = "Base64 Encoded Images", type = "[ { File Name: Text, Base 64 Encoded Image String: Text } ]") List<Map<String, Object>> base64EncodedImages,
            @Field.Text(name = "Question", attributes = {@Attribute(name = "visualWidth", value = "S")}) String question,
            @Field.Text(name = "Context", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}) String context,
            @Field(name = "Temperature", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'In large language models (LLMs), temperature is a parameter that controls the randomness and creativity of the model\\'s output. It essentially adjusts how confident the model is when choosing the next word in a sequence. A lower temperature (e.g., 0.2) leads to more predictable and deterministic outputs, while a higher temperature (e.g., 0.8) introduces more randomness and creativity.'")}) Double temperature,
            @Field(name = "Top K", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'It determines how many of the most likely tokens should be considered when generating a response'")}) Double topK,
            @Field(name = "Top P", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Top P means, Only consider the possibilities that equal or exceed this value. This parameter is expressed as a number between 0.0 and 1.0, with 1.0 being 100% and 0 being 0%'")}) Double topP,
            @Field(name = "Max Tokens", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'This refers to the maximum number of tokens that can be processed in a single input or output for a given model. This is a crucial parameter for controlling the length and cost of interactions with LLMs'")}) Double maxTokens) {
        try {
            ExtensionResponse validationResponse = validateAttributes(attributes);
            if (validationResponse != null) {
                return validationResponse;
            }
            String answer = queryImpl.constructPromptAndQueryModelForImageMap(question, context, base64EncodedImages, temperature, topK, topP, maxTokens);
            return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, Map.of("Answer", answer), null, null, null);
        } catch (Exception cause) {
            ExtensionResponse.Error error = new ExtensionResponse.Error(cause.getMessage(), System.currentTimeMillis(), ExtensionResponse.Error.ExceptionType.SYSTEM_ERROR, Arrays.toString(cause.getStackTrace()));
            return new ExtensionResponse(ExtensionResponse.Result.FAILURE, Map.of(), error, null, null);
        }
    }
}