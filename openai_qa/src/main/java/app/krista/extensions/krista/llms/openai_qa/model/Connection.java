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

import java.util.Objects;

public class Connection {

    private final String callbackUrl;

    private final ModelType modelType;

    public Connection(String callbackUrl, ModelType modelType) {
        this.callbackUrl = callbackUrl;
        this.modelType = modelType;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(callbackUrl, that.callbackUrl) && modelType == that.modelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(callbackUrl, modelType);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "callbackUrl='" + callbackUrl + '\'' +
                ", modelType=" + modelType +
                '}';
    }

}
