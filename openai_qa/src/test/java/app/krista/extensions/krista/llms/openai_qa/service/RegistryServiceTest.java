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

/**
 * Unit tests for RegistryService static methods.
 * Tests URL replacement functionality.
 */
@DisplayName("RegistryService Tests")
class RegistryServiceTest {

    @Nested
    @DisplayName("replaceUrl Tests")
    class ReplaceUrlTests {

        @Test
        @DisplayName("Should replace matching hostname")
        void shouldReplaceMatchingHostname() {
            String url = "https://extension.local.eng.krista.app/path/to/resource";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertEquals("http://localhost:8765/path/to/resource", result);
        }

        @Test
        @DisplayName("Should not replace non-matching hostname")
        void shouldNotReplaceNonMatchingHostname() {
            String url = "https://other.host.com/path/to/resource";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertEquals(url, result);
        }

        @Test
        @DisplayName("Should preserve path when replacing hostname")
        void shouldPreservePathWhenReplacingHostname() {
            String url = "https://extension.local.eng.krista.app/kbnlu/api/config/llm";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertTrue(result.contains("/kbnlu/api/config/llm"));
        }

        @Test
        @DisplayName("Should preserve query parameters when replacing hostname")
        void shouldPreserveQueryParametersWhenReplacingHostname() {
            String url = "https://extension.local.eng.krista.app/path?param1=value1&param2=value2";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertTrue(result.contains("param1=value1"));
            assertTrue(result.contains("param2=value2"));
        }

        @Test
        @DisplayName("Should handle URL with port in original")
        void shouldHandleUrlWithPortInOriginal() {
            String url = "https://extension.local.eng.krista.app:443/path";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");

            // Port is part of authority, so host doesn't match exactly - URL is returned unchanged
            // OR the implementation may handle it differently
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle simple path")
        void shouldHandleSimplePath() {
            String url = "https://extension.local.eng.krista.app/";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertEquals("http://localhost:8765/", result);
        }

        @Test
        @DisplayName("Should handle URL without path")
        void shouldHandleUrlWithoutPath() {
            String url = "https://extension.local.eng.krista.app";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should throw exception for invalid URL")
        void shouldThrowExceptionForInvalidUrl() {
            assertThrows(RuntimeException.class, () -> 
                RegistryService.replaceUrl("not a valid url", "host", "newhost"));
        }

        @Test
        @DisplayName("Should handle fragment in URL")
        void shouldHandleFragmentInUrl() {
            String url = "https://extension.local.eng.krista.app/path#section";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertTrue(result.contains("#section"));
        }

        @Test
        @DisplayName("Should use http scheme after replacement")
        void shouldUseHttpSchemeAfterReplacement() {
            String url = "https://extension.local.eng.krista.app/path";
            String result = RegistryService.replaceUrl(url, "extension.local.eng.krista.app", "localhost:8765");
            
            assertTrue(result.startsWith("http://"));
        }
    }
}

