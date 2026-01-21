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

/**
 * Utility class for secure logging operations.
 *
 * <p>This class provides methods to safely log sensitive content by truncating
 * data to prevent exposure of confidential information such as meeting transcripts,
 * user queries, or AI responses in log files.</p>
 *
 * <h3>Security Requirement (KE-2772):</h3>
 * <p>Log output should be limited to first 200 characters to allow troubleshooting
 * without revealing sensitive information.</p>
 *
 * @since 2.1.7
 */
public final class LoggingUtils {

    /**
     * Maximum characters to log for sensitive content.
     * Per security review, this limits exposure of confidential data in logs.
     */
    public static final int LOG_TRUNCATE_LENGTH = 200;

    /**
     * Private constructor to prevent instantiation.
     */
    private LoggingUtils() {
        // Utility class - do not instantiate
    }

    /**
     * Truncates sensitive content for secure logging.
     *
     * <p>This method limits the amount of sensitive data (prompts, responses, transcripts)
     * written to log files to prevent exposure of confidential information.</p>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * LOGGER.debug("Response: {}", LoggingUtils.truncateForLogging(responseBody));
     * LOGGER.error("Failed prompt: {}", LoggingUtils.truncateForLogging(prompt));
     * }</pre>
     *
     * <h3>Output Examples:</h3>
     * <ul>
     *   <li>Short content (≤200 chars): Returns content as-is</li>
     *   <li>Long content (>200 chars): Returns first 200 chars + "... [TRUNCATED - X chars total]"</li>
     *   <li>Null content: Returns "null"</li>
     * </ul>
     *
     * @param content The content to truncate (may be null)
     * @return Truncated content with indicator of total length, or "null" if content is null
     */
    public static String truncateForLogging(String content) {
        if (content == null) {
            return "null";
        }
        if (content.length() <= LOG_TRUNCATE_LENGTH) {
            return content;
        }
        return content.substring(0, LOG_TRUNCATE_LENGTH) + "... [TRUNCATED - " + content.length() + " chars total]";
    }

    /**
     * Truncates sensitive content for secure logging with a custom length limit.
     *
     * <p>Use this method when a different truncation length is required for specific use cases.</p>
     *
     * @param content   The content to truncate (may be null)
     * @param maxLength The maximum number of characters to include before truncation
     * @return Truncated content with indicator of total length, or "null" if content is null
     * @throws IllegalArgumentException if maxLength is less than 1
     */
    public static String truncateForLogging(String content, int maxLength) {
        if (maxLength < 1) {
            throw new IllegalArgumentException("maxLength must be at least 1");
        }
        if (content == null) {
            return "null";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "... [TRUNCATED - " + content.length() + " chars total]";
    }
}

