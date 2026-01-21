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

package app.krista.extensions.krista.llms.openai_qa.factory;

import app.krista.extensions.krista.llms.openai_qa.service.IDiscussionValidator;
import app.krista.extensions.krista.llms.openai_qa.service.IHttpClientFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiscussionFactory class.
 * Tests factory pattern implementation and service creation.
 */
@DisplayName("DiscussionFactory Tests")
class DiscussionFactoryTest {

    private DiscussionFactory factory;

    @BeforeEach
    void setUp() {
        factory = DiscussionFactory.getInstance();
        factory.reset(); // Reset state before each test
    }

    @AfterEach
    void tearDown() {
        factory.reset(); // Clean up after each test
    }

    @Nested
    @DisplayName("Singleton Pattern Tests")
    class SingletonPatternTests {

        @Test
        @DisplayName("Should return same instance on multiple calls")
        void shouldReturnSameInstanceOnMultipleCalls() {
            DiscussionFactory instance1 = DiscussionFactory.getInstance();
            DiscussionFactory instance2 = DiscussionFactory.getInstance();

            assertSame(instance1, instance2);
        }

        @Test
        @DisplayName("getInstance should never return null")
        void getInstanceShouldNeverReturnNull() {
            assertNotNull(DiscussionFactory.getInstance());
        }
    }

    @Nested
    @DisplayName("createDiscussionValidator Tests")
    class CreateDiscussionValidatorTests {

        @Test
        @DisplayName("Should create discussion validator")
        void shouldCreateDiscussionValidator() {
            IDiscussionValidator validator = factory.createDiscussionValidator();

            assertNotNull(validator);
        }

        @Test
        @DisplayName("Should return cached validator on subsequent calls")
        void shouldReturnCachedValidatorOnSubsequentCalls() {
            IDiscussionValidator validator1 = factory.createDiscussionValidator();
            IDiscussionValidator validator2 = factory.createDiscussionValidator();

            assertSame(validator1, validator2);
        }

        @Test
        @DisplayName("Should return new validator after reset")
        void shouldReturnNewValidatorAfterReset() {
            IDiscussionValidator validator1 = factory.createDiscussionValidator();
            factory.reset();
            IDiscussionValidator validator2 = factory.createDiscussionValidator();

            assertNotSame(validator1, validator2);
        }
    }

    @Nested
    @DisplayName("createHttpClientFactory Tests")
    class CreateHttpClientFactoryTests {

        @Test
        @DisplayName("Should create HTTP client factory")
        void shouldCreateHttpClientFactory() {
            IHttpClientFactory httpClientFactory = factory.createHttpClientFactory();

            assertNotNull(httpClientFactory);
        }

        @Test
        @DisplayName("Should return cached HTTP client factory on subsequent calls")
        void shouldReturnCachedHttpClientFactoryOnSubsequentCalls() {
            IHttpClientFactory httpFactory1 = factory.createHttpClientFactory();
            IHttpClientFactory httpFactory2 = factory.createHttpClientFactory();

            assertSame(httpFactory1, httpFactory2);
        }

        @Test
        @DisplayName("Should return new HTTP client factory after reset")
        void shouldReturnNewHttpClientFactoryAfterReset() {
            IHttpClientFactory httpFactory1 = factory.createHttpClientFactory();
            factory.reset();
            IHttpClientFactory httpFactory2 = factory.createHttpClientFactory();

            assertNotSame(httpFactory1, httpFactory2);
        }
    }

    @Nested
    @DisplayName("Reset Tests")
    class ResetTests {

        @Test
        @DisplayName("Should clear all cached instances")
        void shouldClearAllCachedInstances() {
            // Create instances to cache them
            IDiscussionValidator validator = factory.createDiscussionValidator();
            IHttpClientFactory httpFactory = factory.createHttpClientFactory();

            // Reset
            factory.reset();

            // New instances should be different
            IDiscussionValidator newValidator = factory.createDiscussionValidator();
            IHttpClientFactory newHttpFactory = factory.createHttpClientFactory();

            assertNotSame(validator, newValidator);
            assertNotSame(httpFactory, newHttpFactory);
        }

        @Test
        @DisplayName("Reset should not throw exception")
        void resetShouldNotThrowException() {
            assertDoesNotThrow(() -> factory.reset());
        }

        @Test
        @DisplayName("Multiple resets should not cause issues")
        void multipleResetsShouldNotCauseIssues() {
            assertDoesNotThrow(() -> {
                factory.reset();
                factory.reset();
                factory.reset();
            });
        }
    }
}

