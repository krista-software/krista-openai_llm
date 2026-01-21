# Ask LLM

## Overview

Queries a configured generative AI model with a structured prompt. This request requires input in JSON format following OpenAI's message structure for conversation history and prompt engineering.

## Request Details

- **Area**: Query
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Prompt | Text | Yes | JSON string containing messages array with conversation history | `{"messages": [{"role":"user", "content": "What is AI?"}]}` |

### Prompt Structure

The prompt must be a valid JSON string containing a `messages` array. Each message object should have:

- **role**: "system", "user", or "assistant"
- **content**: The message content

**Example Structure**:
```json
{
  "messages": [
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Hello!"},
    {"role": "assistant", "content": "Hello! How can I assist you today?"},
    {"role": "user", "content": "What is artificial intelligence?"}
  ]
}
```

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| LLM Response | Text | The AI model's response to the prompt |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "OpenAI API key is required to use this extension. Please configure your API key..." | Configure API key in extension settings |
| Model Not Selected | "Please select an OpenAI model to continue. Choose from ChatGPT 3.5..." | Select a model in extension configuration |
| Invalid JSON Format | "Input prompt was not in the correct format!" | Ensure prompt is valid JSON with messages array |
| Empty Prompt | Input validation error | Provide a non-empty prompt parameter |

## Error Handling

For comprehensive troubleshooting steps and solutions, see the [Troubleshooting Guide](pages/Troubleshooting.md).

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- Invalid JSON format in prompt
- Empty prompt parameter

**Resolution**: 
1. Verify extension configuration
2. Validate JSON format
3. Ensure all required parameters are provided

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Malformed message structure
- Invalid role values
- Missing required JSON properties

**Resolution**:
1. Follow OpenAI message format specification
2. Use valid role values: "system", "user", "assistant"
3. Include required "content" field in each message

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or network issues
**Common Scenarios**:
- OpenAI service unavailable
- Network connectivity issues
- API rate limiting
- Model overloaded

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Wait for rate limit reset
4. Retry the request

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid API key
- Expired API key
- Insufficient permissions
- Billing account issues

**Resolution**:
1. Verify API key is correct and active
2. Check OpenAI account status
3. Ensure billing is set up
4. Regenerate API key if necessary

## Usage Examples

### Example 1: Simple Question
**Input**:
```
Prompt: {"messages": [{"role":"user", "content": "What is artificial intelligence?"}]}
```

**Output**:
```json
{
  "LLM Response": "Artificial intelligence (AI) is a field of computer science that focuses on creating machines and systems capable of performing tasks that typically require human intelligence..."
}
```

### Example 2: Conversation with Context
**Input**:
```
Prompt: {"messages": [
  {"role": "system", "content": "You are a helpful coding assistant."},
  {"role": "user", "content": "How do I create a function in Python?"},
  {"role": "assistant", "content": "To create a function in Python, use the 'def' keyword..."},
  {"role": "user", "content": "Can you show me an example?"}
]}
```

**Output**:
```json
{
  "LLM Response": "Here's a simple example of a Python function:\n\n```python\ndef greet(name):\n    return f'Hello, {name}!'\n\n# Usage\nresult = greet('Alice')\nprint(result)  # Output: Hello, Alice!\n```"
}
```

### Example 3: Retry Scenario
**Input**: Same as Example 1
**Initial Response**: Rate limit error (429)
**Retry Response**: Successful response after automatic retry

## Business Rules

1. **JSON Format Required**: Prompt must be valid JSON with messages array
2. **Message Structure**: Each message must have "role" and "content" fields
3. **Valid Roles**: Only "system", "user", and "assistant" roles are supported
4. **Token Limits**: Responses are limited by model's maximum token capacity
5. **Rate Limiting**: Automatic handling of OpenAI rate limits with retry logic
6. **Model Selection**: Uses the model configured in extension settings

## Limitations

1. **Token Limits**: Combined input and output tokens cannot exceed model limits
2. **Rate Limits**: Subject to OpenAI's rate limiting policies
3. **Model Availability**: Depends on selected model and account tier
4. **JSON Parsing**: Strict JSON format validation
5. **Content Filtering**: Subject to OpenAI's content policy restrictions

## Best Practices

### 1. Optimize Token Usage
- Keep prompts concise but clear
- Remove unnecessary conversation history
- Use appropriate models for task complexity

### 2. Structure Conversations Effectively
- Use system messages to set context
- Maintain logical conversation flow
- Include relevant history for context

### 3. Handle Errors Gracefully
- Implement proper error handling
- Provide fallback responses
- Monitor API usage and limits

### 4. Security Considerations
- Never include sensitive data in prompts
- Validate and sanitize user inputs
- Monitor for inappropriate content

## Common Use Cases

### 1. Chatbot Integration
```
Scenario: Building a conversational AI assistant
Action: Use structured message history to maintain context
Result: Natural, contextual conversations
```

### 2. Content Generation
```
Scenario: Generating articles or documentation
Action: Provide detailed system prompts with requirements
Result: High-quality, structured content
```

### 3. Code Assistance
```
Scenario: Helping developers with coding questions
Action: Include code context and specific requirements
Result: Accurate code suggestions and explanations
```

## Related Catalog Requests

- [Ask a Question](pages/AskAQuestion.md) - Simplified querying without JSON structure
- [Get AI Model Name](pages/GetAIModelName.md) - Check current model configuration
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Verify model capabilities

## Technical Implementation

### Helper Class
- **Class**: QueryArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: ExtensionUtil.validateAttributes()
- **Service**: QueryImpl.queryModel()

### Telemetry Metrics
- **REQUEST_STARTED**: Request initiation
- **REQUEST_COMPLETED**: Successful completion
- **REQUEST_FAILED**: Error scenarios
- **TOKEN_USAGE**: Token consumption tracking

## Troubleshooting

### JSON Format Issues
**Cause**: Invalid JSON structure
**Solution**: 
1. Validate JSON syntax
2. Ensure proper escaping of quotes
3. Use JSON validation tools

### Rate Limiting
**Cause**: Too many requests to OpenAI API
**Solution**:
1. Implement request spacing
2. Monitor usage dashboard
3. Consider upgrading API tier

### Model Access Issues
**Cause**: Model not available for account
**Solution**:
1. Check model availability
2. Verify billing setup
3. Try alternative models

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Ask a Question](pages/AskAQuestion.md) - Simplified alternative
