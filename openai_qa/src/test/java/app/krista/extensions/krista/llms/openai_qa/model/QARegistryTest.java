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

package app.krista.extensions.krista.llms.openai_qa.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for QARegistry model class.
 */
@DisplayName("QARegistry Tests")
class QARegistryTest {

    private QARegistry registry;

    @BeforeEach
    void setUp() {
        registry = new QARegistry();
    }

    @Test
    @DisplayName("Should start with empty connections")
    void shouldStartWithEmptyConnections() {
        assertTrue(registry.getConnections().isEmpty());
    }

    @Test
    @DisplayName("Should add connection successfully")
    void shouldAddConnectionSuccessfully() {
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        
        Set<Connection> connections = registry.getConnections();
        assertEquals(1, connections.size());
        
        Connection conn = connections.iterator().next();
        assertEquals("http://test.url", conn.getCallbackUrl());
        assertEquals(ModelType.DEFAULT, conn.getModelType());
    }

    @Test
    @DisplayName("Should add multiple connections")
    void shouldAddMultipleConnections() {
        registry.addConnection("http://test1.url", ModelType.DEFAULT);
        registry.addConnection("http://test2.url", ModelType.FALLBACK);
        
        assertEquals(2, registry.getConnections().size());
    }

    @Test
    @DisplayName("Should not add duplicate connections")
    void shouldNotAddDuplicateConnections() {
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        
        assertEquals(1, registry.getConnections().size());
    }

    @Test
    @DisplayName("Should allow same URL with different model types")
    void shouldAllowSameUrlWithDifferentModelTypes() {
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        registry.addConnection("http://test.url", ModelType.FALLBACK);
        
        assertEquals(2, registry.getConnections().size());
    }

    @Test
    @DisplayName("Should remove connection successfully")
    void shouldRemoveConnectionSuccessfully() {
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        assertEquals(1, registry.getConnections().size());
        
        registry.removeConnection("http://test.url", ModelType.DEFAULT);
        assertTrue(registry.getConnections().isEmpty());
    }

    @Test
    @DisplayName("Should not fail when removing non-existent connection")
    void shouldNotFailWhenRemovingNonExistentConnection() {
        registry.removeConnection("http://nonexistent.url", ModelType.DEFAULT);
        assertTrue(registry.getConnections().isEmpty());
    }

    @Test
    @DisplayName("Should only remove matching connection")
    void shouldOnlyRemoveMatchingConnection() {
        registry.addConnection("http://test1.url", ModelType.DEFAULT);
        registry.addConnection("http://test2.url", ModelType.FALLBACK);
        
        registry.removeConnection("http://test1.url", ModelType.DEFAULT);
        
        assertEquals(1, registry.getConnections().size());
        Connection remaining = registry.getConnections().iterator().next();
        assertEquals("http://test2.url", remaining.getCallbackUrl());
    }

    @Test
    @DisplayName("Should not remove connection with different model type")
    void shouldNotRemoveConnectionWithDifferentModelType() {
        registry.addConnection("http://test.url", ModelType.DEFAULT);
        
        registry.removeConnection("http://test.url", ModelType.FALLBACK);
        
        assertEquals(1, registry.getConnections().size());
    }
}

