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

/* prompt has max length of 1000 characters
    n defaults to 1, which happens if n is null. Otherwise must be between 1 and 10.
    Note: for dall-e-3, only n = 1 is supported
    size defaults to 1024x1024. Also valid are 256x256 and 512x512 (dall-e-2 only)
    model is "dall-e-2" or "dall-e-3"
    quality is "standard" (default) or "hd" (dall-e-3 only)
    NOTE: response_format can be "url" or "b64_json". Default is url.

    Example request:
    {
     "model": "dall-e-2",
     "prompt": "a painting of a rose",
     "n": 1,
     "quality": "standard",
     "size": "1024x1024",
     "response_format": "url"
    }
  */
public class ImageDtoIn {
    private final String model;
    private final String prompt;
    private final Integer n;
    private final String quality;
    private final String size;

    public String getModel() {
        return model;
    }

    public String getPrompt() {
        return prompt;
    }

    public Integer getN() {
        return n;
    }

    public String getQuality() {
        return quality;
    }

    public String getSize() {
        return size;
    }

    public ImageDtoIn(String model,
                      String prompt,
                      Integer n,
                      String quality,
                      String size) {
        this.model = model;
        this.prompt = prompt;
        this.n = n;
        this.quality = quality;
        this.size = size;
    }
}
