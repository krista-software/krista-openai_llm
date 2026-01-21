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

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class KristaMediaClientUtil {

    public static final String TEMP_DIR = "/tmp";
    private final KristaMediaClient kristaMediaClient;
    private static final Logger logger = LoggerFactory.getLogger(KristaMediaClientUtil.class);

    @Inject
    public KristaMediaClientUtil(KristaMediaClient kristaMediaClient) {
        this.kristaMediaClient = kristaMediaClient;
    }

    public File downloadFileHandler(app.krista.model.base.File file) {
        try {
            return kristaMediaClient.toJavaFile(file);
        } catch (IOException cause) {
            logger.error("Error while downloading (toJavaFile) file: {} {}", cause.getMessage(), cause);
            throw new RuntimeException(cause);
        }
    }

    public app.krista.model.base.File uploadFileHandler(File file) {
        try {
            return kristaMediaClient.toKristaFile(file);
        } catch (IOException cause) {
            logger.error("Error while downloading (toKristaFile) file: {} {}", cause.getMessage(), cause);
            throw new RuntimeException(cause);
        }
    }

    /**
     * Reads file content as byte array.
     *
     * @param file The Java File to read
     * @return File content as byte array
     * @throws IOException if reading fails
     */
    public byte[] readFileAsBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            int read = fis.read(fileBytes);
            if (read != fileBytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            return fileBytes;
        }
    }
}