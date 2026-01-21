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

import org.jvnet.hk2.annotations.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CapabilitiesService {

    public Map<String, Object> getCapabilities(){
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("Multimodal support", true);
        capabilities.put("Hard Task support", true);
        capabilities.put("Medium Task support", true);
        capabilities.put("Easy Task support", true);
        return capabilities;
    }

}
