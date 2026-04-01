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

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.Invoker;
import app.krista.extensions.krista.llms.openai_qa.service.FileUploadService;
import app.krista.extensions.krista.llms.openai_qa.service.HttpRequestService;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;
import app.krista.extensions.krista.llms.openai_qa.util.OpenAIConstants;
import app.krista.model.base.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FileAnalysisArea Tests")
class FileAnalysisAreaTest {

    private FileAnalysisArea area;
    private MediaServerFileAccess mediaServerFileAccess;
    private KristaMediaClientUtil kristaMediaClientUtil;
    private FileUploadService fileUploadService;
    private HttpRequestService httpRequestService;

    @BeforeEach
    void setUp() {
        mediaServerFileAccess = mock(MediaServerFileAccess.class);
        kristaMediaClientUtil = mock(KristaMediaClientUtil.class);
        fileUploadService = mock(FileUploadService.class);
        httpRequestService = mock(HttpRequestService.class);

        Invoker invoker = mock(Invoker.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.API_KEY, "sk-test-key");
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);
        when(invoker.getAttributes()).thenReturn(attributes);

        area = new FileAnalysisArea(mediaServerFileAccess, kristaMediaClientUtil,
                fileUploadService, httpRequestService, invoker);
    }

    @Test
    @DisplayName("Should return SUCCESS with Response for valid input")
    void shouldReturnSuccessForValidInput() throws Exception {
        File file = mock(File.class);
        when(file.getFileName()).thenReturn("photo.png");

        java.io.File javaFile = mock(java.io.File.class);
        when(mediaServerFileAccess.downloadFileFromMediaServer(file)).thenReturn(javaFile);
        byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        when(kristaMediaClientUtil.readFileAsBytes(javaFile)).thenReturn(pngBytes);
        when(httpRequestService.sendResponsesApiRequest(any())).thenReturn("AI response here");

        ExtensionResponse response = area.processFiles(List.of(file), "Describe this image", null);

        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());
    }

    @Test
    @DisplayName("Should return FAILURE when API key is missing")
    void shouldReturnFailureWhenApiKeyMissing() {
        Invoker invoker = mock(Invoker.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OpenAIConstants.MODEL, OpenAIConstants.GPT_4);
        // No API key
        when(invoker.getAttributes()).thenReturn(attributes);

        FileAnalysisArea areaNoKey = new FileAnalysisArea(mediaServerFileAccess, kristaMediaClientUtil,
                fileUploadService, httpRequestService, invoker);

        File file = mock(File.class);
        ExtensionResponse response = areaNoKey.processFiles(List.of(file), "Describe", null);

        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
    }

    @Test
    @DisplayName("Should return FAILURE for null files")
    void shouldReturnFailureForNullFiles() {
        ExtensionResponse response = area.processFiles(null, "Describe", null);

        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
    }

    @Test
    @DisplayName("Should return FAILURE for blank prompt")
    void shouldReturnFailureForBlankPrompt() {
        File file = mock(File.class);
        when(file.getFileName()).thenReturn("test.png");

        ExtensionResponse response = area.processFiles(List.of(file), "  ", null);

        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
    }

    @Test
    @DisplayName("Should return FAILURE when service throws exception")
    void shouldReturnFailureOnServiceException() throws Exception {
        File file = mock(File.class);
        when(file.getFileName()).thenReturn("photo.png");
        when(mediaServerFileAccess.downloadFileFromMediaServer(file))
                .thenThrow(new java.io.IOException("Download failed"));

        ExtensionResponse response = area.processFiles(List.of(file), "Describe", null);

        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
    }
}
