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

import app.krista.extensions.krista.llms.openai_qa.model.QARegistry;
import app.krista.extensions.util.KeyValueStore;
import com.google.gson.Gson;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.annotations.Optional;
import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class QAStore {

    private final KeyValueStore keyValueStore;
    private final Map<String, QARegistry> fallbackStore;
    private static final String REGISTRY = "registry";
    private final Gson gson = new Gson();

    @Inject
    public QAStore(@Optional KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
        this.fallbackStore = new ConcurrentHashMap<>();
    }

    public void put(QARegistry registry) {
        if (keyValueStore != null) {
            keyValueStore.put(REGISTRY, gson.toJson(registry));
        } else {
            fallbackStore.put(REGISTRY, registry);
        }
    }

    public QARegistry get() {
        QARegistry qaRegistry = null;

        if (keyValueStore != null) {
            String registryJson = (String) keyValueStore.get(REGISTRY);
            if (registryJson != null) {
                qaRegistry = gson.fromJson(registryJson, QARegistry.class);
            }
        } else {
            qaRegistry = fallbackStore.get(REGISTRY);
        }

        if (qaRegistry == null) {
            qaRegistry = new QARegistry();
        }
        return qaRegistry;
    }

    public void remove() {
        if (keyValueStore != null) {
            keyValueStore.remove(REGISTRY);
        } else {
            fallbackStore.remove(REGISTRY);
        }
    }

}
