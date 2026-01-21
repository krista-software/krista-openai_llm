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

package app.krista.extensions.krista.llms.openai_qa.store;

import app.krista.extensions.krista.llms.openai_qa.model.ModelType;
import app.krista.extensions.krista.llms.openai_qa.model.QARegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QAStore class.
 * Tests storage operations using fallback store (no KeyValueStore dependency).
 */
@DisplayName("QAStore Tests")
class QAStoreTest {

    private QAStore qaStore;

    @BeforeEach
    void setUp() {
        // Create QAStore with null KeyValueStore to use fallback store
        qaStore = new QAStore(null);
    }

    @Nested
    @DisplayName("Put and Get Tests")
    class PutAndGetTests {

        @Test
        @DisplayName("Should store and retrieve QARegistry")
        void shouldStoreAndRetrieveQARegistry() {
            QARegistry registry = new QARegistry();
            registry.addConnection("http://callback.url", ModelType.DEFAULT);

            qaStore.put(registry);
            QARegistry retrieved = qaStore.get();

            assertNotNull(retrieved);
            assertEquals(1, retrieved.getConnections().size());
        }

        @Test
        @DisplayName("Should return empty QARegistry when nothing stored")
        void shouldReturnEmptyQARegistryWhenNothingStored() {
            QARegistry retrieved = qaStore.get();

            assertNotNull(retrieved);
            assertTrue(retrieved.getConnections().isEmpty());
        }

        @Test
        @DisplayName("Should overwrite existing registry")
        void shouldOverwriteExistingRegistry() {
            QARegistry registry1 = new QARegistry();
            registry1.addConnection("http://url1.com", ModelType.DEFAULT);
            qaStore.put(registry1);

            QARegistry registry2 = new QARegistry();
            registry2.addConnection("http://url2.com", ModelType.FALLBACK);
            registry2.addConnection("http://url3.com", ModelType.DEFAULT);
            qaStore.put(registry2);

            QARegistry retrieved = qaStore.get();
            assertEquals(2, retrieved.getConnections().size());
        }
    }

    @Nested
    @DisplayName("Remove Tests")
    class RemoveTests {

        @Test
        @DisplayName("Should remove stored registry")
        void shouldRemoveStoredRegistry() {
            QARegistry registry = new QARegistry();
            registry.addConnection("http://callback.url", ModelType.DEFAULT);
            qaStore.put(registry);

            qaStore.remove();
            QARegistry retrieved = qaStore.get();

            assertNotNull(retrieved);
            assertTrue(retrieved.getConnections().isEmpty());
        }

        @Test
        @DisplayName("Should handle remove when nothing stored")
        void shouldHandleRemoveWhenNothingStored() {
            assertDoesNotThrow(() -> qaStore.remove());
        }
    }

    @Nested
    @DisplayName("Fallback Store Tests")
    class FallbackStoreTests {

        @Test
        @DisplayName("Should use fallback store when KeyValueStore is null")
        void shouldUseFallbackStoreWhenKeyValueStoreIsNull() {
            QAStore storeWithNullKVS = new QAStore(null);

            QARegistry registry = new QARegistry();
            registry.addConnection("http://test.url", ModelType.DEFAULT);

            storeWithNullKVS.put(registry);
            QARegistry retrieved = storeWithNullKVS.get();

            assertNotNull(retrieved);
            assertEquals(1, retrieved.getConnections().size());
        }

        @Test
        @DisplayName("Should maintain separate instances")
        void shouldMaintainSeparateInstances() {
            QAStore store1 = new QAStore(null);
            QAStore store2 = new QAStore(null);

            QARegistry registry1 = new QARegistry();
            registry1.addConnection("http://url1.com", ModelType.DEFAULT);
            store1.put(registry1);

            QARegistry retrieved2 = store2.get();
            assertTrue(retrieved2.getConnections().isEmpty());
        }
    }
}

