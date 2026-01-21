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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CapabilitiesService Tests")
class CapabilitiesServiceTest {

    private CapabilitiesService capabilitiesService;

    @BeforeEach
    void setUp() {
        capabilitiesService = new CapabilitiesService();
    }

    @Test
    @DisplayName("Should return capabilities map")
    void shouldReturnCapabilitiesMap() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertNotNull(capabilities);
        assertFalse(capabilities.isEmpty());
    }

    @Test
    @DisplayName("Should include multimodal support capability")
    void shouldIncludeMultimodalSupportCapability() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertTrue(capabilities.containsKey("Multimodal support"));
        assertEquals(true, capabilities.get("Multimodal support"));
    }

    @Test
    @DisplayName("Should include hard task support capability")
    void shouldIncludeHardTaskSupportCapability() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertTrue(capabilities.containsKey("Hard Task support"));
        assertEquals(true, capabilities.get("Hard Task support"));
    }

    @Test
    @DisplayName("Should include medium task support capability")
    void shouldIncludeMediumTaskSupportCapability() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertTrue(capabilities.containsKey("Medium Task support"));
        assertEquals(true, capabilities.get("Medium Task support"));
    }

    @Test
    @DisplayName("Should include easy task support capability")
    void shouldIncludeEasyTaskSupportCapability() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertTrue(capabilities.containsKey("Easy Task support"));
        assertEquals(true, capabilities.get("Easy Task support"));
    }

    @Test
    @DisplayName("Should return exactly 4 capabilities")
    void shouldReturnExactlyFourCapabilities() {
        Map<String, Object> capabilities = capabilitiesService.getCapabilities();
        
        assertEquals(4, capabilities.size());
    }

    @Test
    @DisplayName("Should return new map instance each time")
    void shouldReturnNewMapInstanceEachTime() {
        Map<String, Object> capabilities1 = capabilitiesService.getCapabilities();
        Map<String, Object> capabilities2 = capabilitiesService.getCapabilities();
        
        assertNotSame(capabilities1, capabilities2);
        assertEquals(capabilities1, capabilities2);
    }
}

