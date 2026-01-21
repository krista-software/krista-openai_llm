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

import app.krista.extensions.krista.llms.openai_qa.config.OpenAiConfiguration;
import app.krista.extensions.krista.llms.openai_qa.exception.ConfigurationException;
import app.krista.extensions.krista.llms.openai_qa.util.LoggingUtils;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import app.krista.ksdk.files.FileHandle;
import app.krista.ksdk.files.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static app.krista.extensions.krista.llms.openai_qa.catalog.Constants.*;

public class ImageGenerator {
    private static URL endpoint;
    private static String apiKey;
    private static String model;
    private static ImageGenerator imageGenerator;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageGenerator.class);
    private static final String IMAGE_COMPLETIONS = "https://api.openai.com/v1/images/generations";
    private static final String DALL_E_3 = "dall-e-3";
    private static final String DALL_E_2 = "dall-e-2";

    /**
     * Establishes a connection to the ImageGenerator service.
     *
     * @param attributes A map of attributes required for the connection.
     * @return An instance of ImageGenerator.
     * @throws ConfigurationException If there is an error in the configuration.
     */
    public static synchronized ImageGenerator getConnection(Map<String, Object> attributes) throws ConfigurationException {
        if (imageGenerator == null) {
            try {
                endpoint = new URL(IMAGE_COMPLETIONS);
                apiKey = ((String) attributes.get(OpenAIConstants.API_KEY)).trim();
                model = DALL_E_3; // we are using dall-e-3 model for image generation
                imageGenerator = new ImageGenerator();

            } catch (Exception cause) {
                LOGGER.error(cause.getMessage(), cause);
                throw new ConfigurationException("Please make sure StudioAI is configured properly: " + cause.getMessage());
            }
        }
        return imageGenerator;
    }

    public static ImageGenerator getInstance() {
        return imageGenerator;
    }

    public String generate(String text) {
        ImageDtoIn request = new ImageDtoIn(model, text, 1, "standard", "1024x1024");
        List<ImageMetaData> response = executeRequest(request);
        if (response == null) {
            return null;
        }
        return response.getFirst().getUrl();
    }

    /**
     * Executes the HTTP request to generate an image.
     *
     * @param requestBody The request body containing image generation parameters.
     * @return A list of ImageMetaData containing the generated image details.
     */
    @SuppressWarnings("deprecation")
    private List<ImageMetaData> executeRequest(ImageDtoIn requestBody) {
        try {
            validateImageDto(requestBody);
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))  // Connection timeout
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(endpoint.toURI())
                    .header("Content-Type", APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(OpenAiConfiguration.GSON.toJson(requestBody)))  // Adjust the body as per your requirement
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.error("Error in ChatGPT API call, status code: {}, response body: {}",
                        response.statusCode(), LoggingUtils.truncateForLogging(response.body()));
                return Collections.emptyList();
            }
            return getImageGenerationResponse(response.body());
        } catch (URISyntaxException | IOException | InterruptedException cause) {
            LOGGER.error(cause.getMessage(), cause);
        }
        return List.of();
    }

    /**
     * Validates the ImageDtoIn object to ensure it has valid parameters.
     *
     * @param imageDtoIn The ImageDtoIn object to validate.
     */
    private void validateImageDto(ImageDtoIn imageDtoIn) {
        Integer imageCount = imageDtoIn.getN();
        if (imageCount != null && (imageCount < 1 || imageCount > 10)) {
            throw new IllegalArgumentException(IMAGE_COUNT_INVALID);
        }
        if (!model.equals(DALL_E_2) && !model.equals(DALL_E_3)) {
            throw new IllegalArgumentException(IMAGE_MODEL_INVALID);
        }
        if (!imageDtoIn.getQuality().equals("standard") && !imageDtoIn.getQuality().equals("hd")) {
            throw new IllegalArgumentException(IMAGE_QUALITY_INVALID);
        }
        String size = imageDtoIn.getSize();
        if (!size.equals("256x256") && !size.equals("512x512") && !size.equals("1024x1024")) {
            throw new IllegalArgumentException(IMAGE_SIZE_INVALID);
        }
    }

    /**
     * Parses the response body to extract image generation metadata.
     *
     * @param responseBody The response body from the API call.
     * @return A list of ImageMetaData containing the generated image details.
     * @throws IOException If there is an error parsing the response body.
     */
    private List<ImageMetaData> getImageGenerationResponse(String responseBody) throws IOException {
        if (responseBody == null) {
            LOGGER.error("Empty response body from ChatGPT");
            return Collections.emptyList();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("response body: {}", LoggingUtils.truncateForLogging(responseBody));
            }
        }
        ImageGenerationResponse image = OpenAiConfiguration.GSON.fromJson(responseBody, ImageGenerationResponse.class);
        if (image.getData() == null || image.getData().isEmpty()) {
            LOGGER.info("Got no response choice from ChatGPT!");
            throw new IOException("Got no response choice from ChatGPT!");
        }
        if (image.getData().size() > 1) {
            LOGGER.info("We received > 1 choice but only replying with the first");
        }
        return image.getData();
    }

    /**
     * Retrieves the media ID response from the language model using the provided prompt and image generator.
     *
     * @param prompt The prompt to use for generating the image.
     * @return The Media id of the generated image.
     * @throws ConfigurationException If there is a configuration error.
     */
    public String generateIcon(String prompt, FileRepository fileRepository) throws ConfigurationException {
        String imageUrl = generate(prompt);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                File file = extractImageFromURL(new URL(imageUrl));
                try (final FileHandle fileHandle = fileRepository.createNewFileByName(file.getName())) {
                    fileHandle.setContent(new FileInputStream(file));
                    return fileHandle.getFile().getMediaId();
                }
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }
        return imageUrl;
    }

    private File extractImageFromURL(URL imageUrl) throws IOException {
        // Save the image to a temporary file
        File targetFile = new File(String.format("%s/%s.png", System.getProperty("java.io.tmpdir"), UUID.randomUUID()));

        //Compressing the image to 256x256 size before uploading to media server
        BufferedImage originalImage = ImageIO.read(imageUrl);

        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        // Use Graphics2D to draw the original image onto the resized image
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(256, 256, Image.SCALE_SMOOTH), 0, 0, 256, 256, null);
        g2d.dispose();

        // Write the resized image to the temporary file
        ImageIO.write(resizedImage, "png", targetFile);
        return targetFile;
    }
}
