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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.inject.Inject;
import app.krista.extensions.krista.llms.openai_qa.model.Connection;
import app.krista.extensions.krista.llms.openai_qa.model.ModelType;
import app.krista.extensions.krista.llms.openai_qa.model.QARegistry;
import app.krista.extensions.krista.llms.openai_qa.store.QAStore;
import app.krista.extensions.krista.llms.openai_qa.util.LoggingUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RegistryService {

    private final QAStore qaStore;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClients.createDefault();
    private final CapabilitiesService capabilitiesService;
    private static final Logger log = LoggerFactory.getLogger(RegistryService.class);
    private final String LOCAL_HOST_NAME = "extension.local.eng.krista.app";
    private final String RESOLVED_LOCAL_HOST_NAME = "localhost:8765";

    @Inject
    public RegistryService(QAStore qaStore, CapabilitiesService capabilitiesService) {
        this.qaStore = qaStore;
        this.capabilitiesService = capabilitiesService;
    }

    public void informConnections(Map<String, Object> newAttributes, String invokerId) throws IOException {
        log.error("Inside Informing connections.");
        QARegistry qaRegistry = qaStore.get();
        newAttributes.put("Invoker ID", invokerId);
        for (Connection connection : qaRegistry.getConnections()) {
            log.error("Connection found : " + connection.toString());
            if (connection.getModelType().equals(ModelType.DEFAULT)) {
                executeHttpCallToConnection("llm", connection.getCallbackUrl(), gson.toJson(newAttributes));
            } else {
                executeHttpCallToConnection("fallback", connection.getCallbackUrl(), gson.toJson(newAttributes));
            }
        }

    }

    private JsonObject executeHttpCallToConnection(String endPoint, String callbackUrl, String payload) throws IOException {
        // Replace localhost in the callback URL
        callbackUrl = replaceUrl(callbackUrl, LOCAL_HOST_NAME, RESOLVED_LOCAL_HOST_NAME);
        log.info("Preparing to execute HTTP call. Endpoint: {}, Callback URL: {}, Payload size: {} bytes",
                endPoint, callbackUrl, payload != null ? payload.length() : 0);

        String fullUrl = callbackUrl + "/kbnlu/api/config/" + endPoint;
        log.info("Constructed request URL: {}", fullUrl);

        // Create POST request
        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));
        log.debug("HTTP POST request created with payload size: {} bytes", payload != null ? payload.length() : 0);

        // Execute request
        log.info("Executing HTTP POST request to {}", fullUrl);
        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        log.info("Received response with status code: {}", statusCode);

        // Handle non-200 responses
        if (statusCode != 200) {
            log.error("Call to endpoint {} failed with status code: {}", fullUrl, statusCode);
        }

        // Extract response body
        String responseBody = EntityUtils.toString(response.getEntity());
        log.debug("Response body: {}", LoggingUtils.truncateForLogging(responseBody));

        log.info("Successfully processed response from {}", fullUrl);
        return gson.fromJson(responseBody, JsonObject.class);
    }


    public static String replaceUrl(String url, String oldHostname, String newHostname) {
        try {
            URI uri = new URI(url);

            if (uri.getHost().equals(oldHostname)) {
                String scheme = "http";
                String newAuthority = newHostname;

                URI newUri = new URI(
                        scheme,
                        newAuthority,
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment()
                );

                return newUri.toString();
            } else {
                return url;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
