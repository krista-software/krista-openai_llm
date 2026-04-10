# Get AI Model Name

## Overview

Returns the name of the AI model that is currently being queried. This request provides information about which OpenAI model is configured and active for the extension.

## Request Details

- **Area**: Query
- **Type**: QUERY_SYSTEM
- **Retry Support**: ❌ No (Simple configuration lookup, no external API calls required)

## Input Parameters

This request does not require any input parameters.

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| AI Model | Text | The display name of the currently configured OpenAI model |

### Possible Return Values

| Returned Value | Description | Corresponding Configuration |
|----------------|-------------|----------------------------|
| "OpenAI ChatGPT 4" | GPT-4 model is selected | "ChatGPT 4" in configuration |
| "OpenAI ChatGPT 3.5" | GPT-3.5 model is selected | "ChatGPT 3.5" in configuration |

## Validation Rules

This request performs minimal validation as it only retrieves configuration information:

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Extension Not Configured | Configuration error | Complete extension configuration |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Extension configuration issues
**Common Scenarios**:
- Extension not properly initialized
- Configuration data corrupted

**Resolution**: 
1. Verify extension is properly configured
2. Re-configure the extension if necessary
3. Check extension installation

### Logic Errors (LOGIC_ERROR)
**Cause**: Internal logic issues (rare)
**Common Scenarios**:
- Model configuration mapping issues
- Unexpected model values

**Resolution**:
1. Re-configure the model selection
2. Contact support if issue persists

### System Errors (SYSTEM_ERROR)
**Cause**: System-level issues (very rare for this request)
**Common Scenarios**:
- Extension service unavailable
- Configuration service issues

**Resolution**:
1. Restart the extension
2. Check system status
3. Contact support if needed

## Usage Examples

### Example 1: GPT-4 Configuration
**Input**: (No parameters required)

**Output**:
```
AI Model: "OpenAI ChatGPT 4"
```

**Description**: Extension is configured to use ChatGPT 4 model

### Example 2: GPT-3.5 Configuration
**Input**: (No parameters required)

**Output**:
```
AI Model: "OpenAI ChatGPT 3.5"
```

**Description**: Extension is configured to use ChatGPT 3.5 model

### Example 3: Integration Check
**Input**: (No parameters required)

**Output**:
```
AI Model: "OpenAI ChatGPT 4"
```

**Description**: Used to verify model configuration before making other AI requests

## Business Rules

1. **Configuration Dependency**: Returns the model selected in extension configuration
2. **No External Calls**: Does not make API calls to OpenAI
3. **Real-time Configuration**: Reflects current configuration state
4. **Display Format**: Returns user-friendly model names
5. **Consistency**: Always returns the same value until configuration changes

## Limitations

1. **Configuration Only**: Only shows configured model, not actual API model used
2. **No Validation**: Does not verify if the model is accessible via API
3. **Static Information**: Does not provide model capabilities or status
4. **No Version Details**: Does not include specific model version information

## Best Practices

### 1. Configuration Verification
- Use this request to verify model configuration before complex operations
- Include in diagnostic workflows
- Check after configuration changes

### 2. Workflow Integration
- Call before other AI requests to log current model
- Use in conditional logic based on model type
- Include in error reporting for troubleshooting

### 3. User Interface
- Display current model to users
- Show in configuration summaries
- Include in system status displays

## Common Use Cases

### 1. Configuration Verification
```
Scenario: Verifying extension setup after configuration
Action: Call Get AI Model Name to confirm model selection
Result: Displays currently configured model for verification
```

### 2. Conditional Logic
```
Scenario: Different behavior based on model type
Action: Check model name and adjust request parameters accordingly
Result: Optimized requests based on model capabilities
```

### 3. Troubleshooting
```
Scenario: Investigating AI response issues
Action: Check current model configuration
Result: Confirms which model is being used for debugging
```

### 4. Audit and Logging
```
Scenario: Tracking model usage across workflows
Action: Log current model before AI operations
Result: Audit trail of model usage patterns
```

## Related Catalog Requests

- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Check model capabilities and features
- [Ask LLM](pages/AskLLM.md) - Use the configured model for queries
- [Ask a Question](pages/AskAQuestion.md) - Simple queries with the configured model

## Technical Implementation

### Helper Class
- **Class**: QueryArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Method**: getAIModelName()
- **Service**: QueryImpl.getAIModel()

### Implementation Logic
```java
public String getAIModel() {
    if (modelName.get().equals(OpenAIConstants.GPT_4)) {
        return OpenAIConstants.BASE_MODEL_NAME + "4";
    }
    return OpenAIConstants.BASE_MODEL_NAME + "3.5";
}
```

### Model Mapping
- **ChatGPT 4** → "OpenAI ChatGPT 4"
- **ChatGPT 3.5** → "OpenAI ChatGPT 3.5"
- **ChatGPT 4.1 Mini** → "OpenAI ChatGPT 3.5" (fallback)
- **ChatGPT 5.4** → "OpenAI ChatGPT 3.5" (fallback)
- **ChatGPT 5.4 Mini** → "OpenAI ChatGPT 3.5" (fallback)
- **ChatGPT 5.4 Nano** → "OpenAI ChatGPT 3.5" (fallback)

### Telemetry Metrics
- **MODEL_NAME_REQUESTED**: Request initiated
- **MODEL_NAME_RETURNED**: Successful response

## Troubleshooting

### Unexpected Model Name
**Cause**: Configuration may not match expected values
**Solution**:
1. Check extension configuration
2. Verify model selection in settings
3. Re-configure if necessary

### Empty or Null Response
**Cause**: Configuration not properly initialized
**Solution**:
1. Complete extension configuration
2. Ensure model is selected
3. Restart extension if needed

### Configuration Mismatch
**Cause**: Display name doesn't match expected model
**Solution**:
1. Verify model selection in configuration
2. Check for configuration updates
3. Re-select model if needed

## Integration Notes

### Workflow Integration
- Use early in workflows to establish model context
- Include in error handling to provide model information
- Log for audit and troubleshooting purposes

### API Integration
- No external API calls required
- Fast response time
- Suitable for frequent calls

### Configuration Dependencies
- Requires completed extension configuration
- Reflects real-time configuration state
- Updates automatically when configuration changes

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure model selection
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Check model capabilities
- [Authentication](pages/Authentication.md) - Extension authentication setup
