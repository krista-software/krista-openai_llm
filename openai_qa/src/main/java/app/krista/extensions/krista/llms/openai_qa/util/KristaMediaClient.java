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

package app.krista.extensions.krista.llms.openai_qa.util;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Media client for handling file conversions between Krista and Java File objects
 */
@Service
public class KristaMediaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(KristaMediaClient.class);

    /**
     * Converts Krista File to Java File
     */
    public File toJavaFile(app.krista.model.base.File kristaFile) throws IOException {
        if (kristaFile == null) {
            throw new IllegalArgumentException("Krista file cannot be null");
        }

        LOGGER.debug("Converting Krista file to Java file: {}", kristaFile.getFileName());

        // Create a temporary file based on the Krista file
        File tempFile = new File(KristaMediaClientUtil.TEMP_DIR, kristaFile.getFileName());

        // In a real implementation, this would download/copy the file content
        // For now, we'll create a placeholder
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }

        LOGGER.debug("Java file created: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Converts Java File to Krista File
     */
    public app.krista.model.base.File toKristaFile(File javaFile) throws IOException {
        if (javaFile == null || !javaFile.exists()) {
            throw new IllegalArgumentException("Java file must exist");
        }

        LOGGER.debug("Converting Java file to Krista file: {}", javaFile.getName());

        // Create a Krista file representation
        // In a real implementation, this would upload the file to Krista's file system
        // Based on the constructor error, we need to provide: long, String, String, String
        app.krista.model.base.File kristaFile = new app.krista.model.base.File(
                javaFile.length(),  // size
                javaFile.getName(), // fileName
                "application/octet-stream", // mimeType
                "temp-media-id-" + System.currentTimeMillis() // mediaId
        );

        LOGGER.debug("Krista file created: {}", kristaFile.getFileName());
        return kristaFile;
    }
}
