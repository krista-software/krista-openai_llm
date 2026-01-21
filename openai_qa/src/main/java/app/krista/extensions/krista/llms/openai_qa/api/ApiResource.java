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

import app.krista.extensions.krista.llms.openai_qa.model.ModelType;
import app.krista.extensions.krista.llms.openai_qa.model.QARegistry;
import app.krista.extensions.krista.llms.openai_qa.store.QAStore;
import com.google.gson.JsonObject;
import org.jvnet.hk2.annotations.Service;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Service
@Path("/")
public class ApiResource {

    private final QAStore qaStore;

    @Inject
    public ApiResource(QAStore qaStore) {
        this.qaStore = qaStore;
    }

    @POST
    @Path("/register")
    @Produces("application/json")
    @Consumes("application/json")
    public Map<String, Object> registerQA(JsonObject payload) {
        String callbackUrl = payload.get("callback").getAsString();
        ModelType modelType = ModelType.valueOf(payload.get("modelType").getAsString());
        QARegistry qaRegistry = qaStore.get();
        qaRegistry.addConnection(callbackUrl, modelType);
        qaStore.put(qaRegistry);
        Map<String,Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }



}
