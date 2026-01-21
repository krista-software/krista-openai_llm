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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Connection model class.
 */
@DisplayName("Connection Tests")
class ConnectionTest {

    @Test
    @DisplayName("Should create connection with callback URL and model type")
    void shouldCreateConnectionWithParameters() {
        Connection connection = new Connection("http://callback.url", ModelType.DEFAULT);
        
        assertEquals("http://callback.url", connection.getCallbackUrl());
        assertEquals(ModelType.DEFAULT, connection.getModelType());
    }

    @Test
    @DisplayName("Should create connection with FALLBACK model type")
    void shouldCreateConnectionWithFallbackType() {
        Connection connection = new Connection("http://fallback.url", ModelType.FALLBACK);
        
        assertEquals("http://fallback.url", connection.getCallbackUrl());
        assertEquals(ModelType.FALLBACK, connection.getModelType());
    }

    @Test
    @DisplayName("Should handle null callback URL")
    void shouldHandleNullCallbackUrl() {
        Connection connection = new Connection(null, ModelType.DEFAULT);
        
        assertNull(connection.getCallbackUrl());
        assertEquals(ModelType.DEFAULT, connection.getModelType());
    }

    @Test
    @DisplayName("equals should return true for same values")
    void equalsShouldReturnTrueForSameValues() {
        Connection conn1 = new Connection("http://test.url", ModelType.DEFAULT);
        Connection conn2 = new Connection("http://test.url", ModelType.DEFAULT);
        
        assertEquals(conn1, conn2);
    }

    @Test
    @DisplayName("equals should return false for different URLs")
    void equalsShouldReturnFalseForDifferentUrls() {
        Connection conn1 = new Connection("http://test1.url", ModelType.DEFAULT);
        Connection conn2 = new Connection("http://test2.url", ModelType.DEFAULT);
        
        assertNotEquals(conn1, conn2);
    }

    @Test
    @DisplayName("equals should return false for different model types")
    void equalsShouldReturnFalseForDifferentModelTypes() {
        Connection conn1 = new Connection("http://test.url", ModelType.DEFAULT);
        Connection conn2 = new Connection("http://test.url", ModelType.FALLBACK);
        
        assertNotEquals(conn1, conn2);
    }

    @Test
    @DisplayName("equals should return true for same instance")
    void equalsShouldReturnTrueForSameInstance() {
        Connection conn = new Connection("http://test.url", ModelType.DEFAULT);
        assertEquals(conn, conn);
    }

    @Test
    @DisplayName("equals should return false for null")
    void equalsShouldReturnFalseForNull() {
        Connection conn = new Connection("http://test.url", ModelType.DEFAULT);
        assertNotEquals(null, conn);
    }

    @Test
    @DisplayName("equals should return false for different class")
    void equalsShouldReturnFalseForDifferentClass() {
        Connection conn = new Connection("http://test.url", ModelType.DEFAULT);
        assertNotEquals("string", conn);
    }

    @Test
    @DisplayName("hashCode should be equal for equal objects")
    void hashCodeShouldBeEqualForEqualObjects() {
        Connection conn1 = new Connection("http://test.url", ModelType.DEFAULT);
        Connection conn2 = new Connection("http://test.url", ModelType.DEFAULT);
        
        assertEquals(conn1.hashCode(), conn2.hashCode());
    }

    @Test
    @DisplayName("hashCode should be different for different objects")
    void hashCodeShouldBeDifferentForDifferentObjects() {
        Connection conn1 = new Connection("http://test1.url", ModelType.DEFAULT);
        Connection conn2 = new Connection("http://test2.url", ModelType.FALLBACK);
        
        assertNotEquals(conn1.hashCode(), conn2.hashCode());
    }

    @Test
    @DisplayName("toString should contain callback URL and model type")
    void toStringShouldContainFields() {
        Connection conn = new Connection("http://test.url", ModelType.DEFAULT);
        String str = conn.toString();
        
        assertTrue(str.contains("http://test.url"));
        assertTrue(str.contains("DEFAULT"));
        assertTrue(str.contains("Connection"));
    }
}

