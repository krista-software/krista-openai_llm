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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Constants class.
 */
@DisplayName("Constants Tests")
class ConstantsTest {

    @Nested
    @DisplayName("Image Generation Constants")
    class ImageGenerationConstantsTests {

        @Test
        @DisplayName("IMAGE_COUNT_INVALID should contain helpful message")
        void imageCountInvalidShouldContainHelpfulMessage() {
            assertNotNull(Constants.IMAGE_COUNT_INVALID);
            assertTrue(Constants.IMAGE_COUNT_INVALID.contains("1"));
            assertTrue(Constants.IMAGE_COUNT_INVALID.contains("10"));
        }

        @Test
        @DisplayName("IMAGE_MODEL_INVALID should mention valid models")
        void imageModelInvalidShouldMentionValidModels() {
            assertNotNull(Constants.IMAGE_MODEL_INVALID);
            assertTrue(Constants.IMAGE_MODEL_INVALID.contains("dall-e-2"));
            assertTrue(Constants.IMAGE_MODEL_INVALID.contains("dall-e-3"));
        }

        @Test
        @DisplayName("IMAGE_QUALITY_INVALID should mention valid qualities")
        void imageQualityInvalidShouldMentionValidQualities() {
            assertNotNull(Constants.IMAGE_QUALITY_INVALID);
            assertTrue(Constants.IMAGE_QUALITY_INVALID.contains("standard"));
            assertTrue(Constants.IMAGE_QUALITY_INVALID.contains("hd"));
        }

        @Test
        @DisplayName("IMAGE_SIZE_INVALID should mention valid sizes")
        void imageSizeInvalidShouldMentionValidSizes() {
            assertNotNull(Constants.IMAGE_SIZE_INVALID);
            assertTrue(Constants.IMAGE_SIZE_INVALID.contains("256x256"));
            assertTrue(Constants.IMAGE_SIZE_INVALID.contains("512x512"));
            assertTrue(Constants.IMAGE_SIZE_INVALID.contains("1024x1024"));
        }
    }

    @Nested
    @DisplayName("Connection and API Constants")
    class ConnectionConstantsTests {

        @Test
        @DisplayName("CONNECTION_TEST_FAILED should contain status URL")
        void connectionTestFailedShouldContainStatusUrl() {
            assertNotNull(Constants.CONNECTION_TEST_FAILED);
            assertTrue(Constants.CONNECTION_TEST_FAILED.contains("status.openai.com"));
        }

        @Test
        @DisplayName("API_SERVICE_UNAVAILABLE should contain status URL")
        void apiServiceUnavailableShouldContainStatusUrl() {
            assertNotNull(Constants.API_SERVICE_UNAVAILABLE);
            assertTrue(Constants.API_SERVICE_UNAVAILABLE.contains("status.openai.com"));
        }

        @Test
        @DisplayName("REQUEST_PROCESSING_ERROR should be helpful")
        void requestProcessingErrorShouldBeHelpful() {
            assertNotNull(Constants.REQUEST_PROCESSING_ERROR);
            assertTrue(Constants.REQUEST_PROCESSING_ERROR.length() > 0);
        }
    }

    @Nested
    @DisplayName("Configuration Constants")
    class ConfigurationConstantsTests {

        @Test
        @DisplayName("API_KEY_MISSING should contain API key URL")
        void apiKeyMissingShouldContainApiKeyUrl() {
            assertNotNull(Constants.API_KEY_MISSING);
            assertTrue(Constants.API_KEY_MISSING.contains("platform.openai.com"));
        }

        @Test
        @DisplayName("MODEL_NOT_SELECTED should mention available models")
        void modelNotSelectedShouldMentionAvailableModels() {
            assertNotNull(Constants.MODEL_NOT_SELECTED);
            assertTrue(Constants.MODEL_NOT_SELECTED.contains("GPT"));
        }
    }

    @Nested
    @DisplayName("Private Constructor")
    class PrivateConstructorTests {

        @Test
        @DisplayName("Should throw UnsupportedOperationException when instantiated via reflection")
        void shouldThrowExceptionWhenInstantiated() throws Exception {
            Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            InvocationTargetException exception = assertThrows(
                    InvocationTargetException.class,
                    constructor::newInstance
            );

            assertTrue(exception.getCause() instanceof UnsupportedOperationException);
            assertTrue(exception.getCause().getMessage().contains("utility class"));
        }
    }
}

