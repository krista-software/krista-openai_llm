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

import java.util.HashSet;
import java.util.Set;

public class QARegistry {

    private Set<Connection> connections = new HashSet<>();

    public void addConnection(String callbackUrl, ModelType modelType) {
        connections.add(new Connection(callbackUrl, modelType));
    }

    public Set<Connection> getConnections() {
        return connections;
    }

    public void removeConnection(String callbackUrl, ModelType modelType){
        connections.remove(new Connection(callbackUrl, modelType));
    }

}
