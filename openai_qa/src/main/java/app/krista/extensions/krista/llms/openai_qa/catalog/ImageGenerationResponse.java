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

package app.krista.extensions.krista.llms.openai_qa.catalog;

import java.util.List;

public class ImageGenerationResponse {
    private final long created;
    private final List<ImageMetaData> data;

    public ImageGenerationResponse(long created, List<ImageMetaData> images) {
        this.created = created;
        this.data = images;
    }

    public long getCreated() {
        return created;
    }

    public List<ImageMetaData> getData() {
        return data;
    }

}
