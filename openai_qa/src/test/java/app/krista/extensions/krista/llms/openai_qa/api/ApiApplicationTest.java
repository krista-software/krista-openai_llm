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

package app.krista.extensions.krista.llms.openai_qa.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiApplication class.
 */
@DisplayName("ApiApplication Tests")
class ApiApplicationTest {

    @Test
    @DisplayName("Should create ApiApplication instance")
    void shouldCreateApiApplicationInstance() {
        ApiApplication app = new ApiApplication();
        assertNotNull(app);
    }

    @Test
    @DisplayName("getClasses should return set containing ApiResource")
    void getClassesShouldReturnSetContainingApiResource() {
        ApiApplication app = new ApiApplication();
        Set<Class<?>> classes = app.getClasses();

        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertTrue(classes.contains(ApiResource.class));
    }
}

