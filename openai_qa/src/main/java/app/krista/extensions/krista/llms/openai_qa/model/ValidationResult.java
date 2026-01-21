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

package app.krista.extensions.krista.llms.openai_qa.model;

/**
 * Represents the result of a validation operation.
 * Contains validation status and optional error message.
 */
public class ValidationResult {
    
    private final boolean valid;
    private final String errorMessage;
    
    /**
     * Creates a validation result.
     * 
     * @param valid Whether the validation passed
     * @param errorMessage Error message if validation failed, null if valid
     */
    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Creates a successful validation result.
     * 
     * @return ValidationResult indicating success
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }
    
    /**
     * Creates a failed validation result with error message.
     * 
     * @param errorMessage The error message describing the validation failure
     * @return ValidationResult indicating failure
     */
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }
    
    /**
     * Checks if validation was successful.
     * 
     * @return true if validation passed, false otherwise
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Gets the error message if validation failed.
     * 
     * @return Error message or null if validation passed
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Checks if validation failed.
     * 
     * @return true if validation failed, false otherwise
     */
    public boolean hasError() {
        return !valid;
    }
    
    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
