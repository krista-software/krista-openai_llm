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

import app.krista.ksdk.files.FileHandle;
import app.krista.ksdk.files.FileRepository;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class MediaServerFileAccess {

    protected static final Logger log = LoggerFactory.getLogger(MediaServerFileAccess.class);

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir") + "/MSFA/";
    private final List<String> unSupportedFileFormats =
            Arrays.asList("html", "php5", "pht", "phtml", "shtml", "asa", "cer", "asax", "swf", "xap", "jsp", "exe",
                    "js");

    private final FileRepository fileRepository;

    @Inject
    public MediaServerFileAccess(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Simplified way to fetch a file from our media server
     *
     * @param file the Krista field for the file
     * @return the java.io.File object of the file we are looking for
     */
    public File downloadFileFromMediaServer(app.krista.model.base.File file) throws IOException {

        try (FileHandle fileHandle = fileRepository.getFile(file)) {
            InputStream content = fileHandle.getContent();
            final File input = new File(file.getFileName());
            return convertInputStreamToFile(content, input);
        }
    }

    /**
     * Converts an input stream to a file.
     *
     * @param inputStream The input stream to be converted.
     * @param input       The file to write the input stream content to.
     * @return The file with the content of the input stream.
     * @throws IOException If an I/O error occurs.
     */
    private File convertInputStreamToFile(InputStream inputStream, File input) throws IOException {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(input))) {
            byte[] buffer = new byte[4096]; // Increasing buffer size for better performance with larger files
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return input;
    }

    public String uploadFileToMediaServer(File file) throws IOException {

        System.out.println("File Name " + file.getName());
        if (isUnsupportedFileFormat(file.getName())) {
            String zipFilePath = TMP_DIR + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".zip";
            compressFile(zipFilePath, file.getAbsolutePath());
            file = new File(zipFilePath);
        }
        try (final FileHandle fileHandle = fileRepository.createNewFileByName(file.getName())) {
            fileHandle.setContent(new FileInputStream(file));
            return fileHandle.getFile().getMediaId();
        }

    }

    private boolean isUnsupportedFileFormat(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return unSupportedFileFormats.contains(fileExtension);
    }

    /**
     * Retrieves the file extension from the file name.
     *
     * @param fileName The name of the file.
     * @return The file extension.
     * @throws IllegalArgumentException If the file format is unsupported.
     */
    private String getFileExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring((fileName.lastIndexOf(".") + 1));
        }
        throw new IllegalArgumentException("Unsupported file format");
    }

    /**
     * Compresses a file into a zip archive.
     *
     * @param zipFilePath  The path where the zip file will be created.
     * @param dirPathToZip The path of the directory to be zipped.
     * @throws IOException If an I/O error occurs.
     */
    public static void compressFile(String zipFilePath, String dirPathToZip) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File fileToZip = new File(dirPathToZip);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.closeEntry();
        }
    }

}
