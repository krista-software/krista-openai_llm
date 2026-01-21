# Ask a Question

## Overview

Request to ask anything to the AI model without any prompt engineering. This is a simplified interface that automatically handles prompt formatting and allows optional context to enhance responses.

## Request Details

- **Area**: Query
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Query | Text | Yes | The question or prompt to ask the AI model | "What is artificial intelligence?" |
| Context | Text | No | Additional context to help the AI provide better responses | "Artificial intelligence (AI) is a field that focuses on creating machines..." |

### Parameter Details

#### Query
- **Purpose**: The main question or request for the AI
- **Format**: Plain text, no special formatting required
- **Length**: Recommended to keep under 4000 characters for optimal performance
- **Content**: Can be questions, requests for explanations, creative tasks, etc.

#### Context (Optional)
- **Purpose**: Provides background information to improve response quality
- **Format**: Plain text
- **Usage**: Include relevant information that helps the AI understand the domain or specific requirements
- **Benefits**: More accurate and relevant responses

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Response | Text | The AI model's response to the query |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| Empty Query | Input validation error | Provide a non-empty query parameter |

## Error Handling

For comprehensive troubleshooting steps and solutions, see the [Troubleshooting Guide](pages/Troubleshooting.md).

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- Empty or null query parameter

**Resolution**: 
1. Verify extension configuration is complete
2. Ensure query parameter is provided
3. Check API key and model selection

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Query exceeds maximum length limits
- Invalid characters in query
- Context formatting issues

**Resolution**:
1. Reduce query length if too long
2. Remove special characters that may cause issues
3. Simplify context if causing problems

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or network issues
**Common Scenarios**:
- OpenAI service unavailable
- Network connectivity problems
- API rate limiting
- Model temporarily overloaded

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Wait for rate limit reset
4. Retry the request after a brief delay

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid or expired API key
- Insufficient account permissions
- Billing account issues
- Model access restrictions

**Resolution**:
1. Verify API key is valid and active
2. Check OpenAI account status and billing
3. Ensure selected model is available for your account
4. Regenerate API key if necessary

## Usage Examples

### Example 1: Simple Question
**Input**:
```
Query: "What is artificial intelligence?"
Context: (empty)
```

**Output**:
```
Response: "Artificial intelligence (AI) is a field of computer science that focuses on creating machines and systems capable of performing tasks that typically require human intelligence. These tasks include learning, reasoning, problem-solving, perception, and language understanding. AI systems can analyze data, recognize patterns, make decisions, and adapt to new situations, making them valuable tools across various industries and applications."
```

### Example 2: Question with Context
**Input**:
```
Query: "How can AI help in healthcare?"
Context: "Artificial intelligence (AI) is a field that focuses on creating machines that can learn and improve on their own. AI is often categorized into four main types: reactive machines, limited memory, theory of mind, and self-awareness."
```

**Output**:
```
Response: "AI can significantly transform healthcare in several ways:

1. **Diagnostic Assistance**: AI can analyze medical images, lab results, and patient data to help doctors identify diseases earlier and more accurately.

2. **Drug Discovery**: Machine learning algorithms can accelerate the development of new medications by predicting molecular behavior and identifying promising compounds.

3. **Personalized Treatment**: AI can analyze patient genetics, medical history, and lifestyle factors to recommend tailored treatment plans.

4. **Predictive Analytics**: AI systems can predict patient deterioration, disease outbreaks, or treatment outcomes to enable proactive care.

5. **Administrative Efficiency**: AI can automate scheduling, billing, and documentation tasks, allowing healthcare professionals to focus more on patient care.

Given the context you provided about AI's learning capabilities, these healthcare applications particularly benefit from AI's ability to continuously improve through experience with medical data."
```

### Example 3: Creative Request
**Input**:
```
Query: "Write a short poem about technology"
Context: "Focus on the positive aspects of how technology connects people"
```

**Output**:
```
Response: "Bridges of Light

Across the vast digital sea,
Technology weaves threads of connection,
Bringing distant hearts together,
In moments of shared reflection.

Screens become windows to souls,
Messages carry love and care,
While innovation gently unfolds
New ways to show we're always there.

From video calls that span the globe,
To apps that help us understand,
Technology wraps us in its robe
Of unity, hand in digital hand."
```

## Business Rules

1. **Automatic Prompt Engineering**: The system automatically formats queries for optimal AI interaction
2. **Context Integration**: When provided, context is seamlessly integrated with the query
3. **Model Selection**: Uses the model configured in extension settings
4. **Response Optimization**: Responses are optimized for clarity and relevance
5. **Content Filtering**: Subject to OpenAI's content policy guidelines
6. **Token Management**: Automatically manages token usage for input and output

## Limitations

1. **Token Limits**: Combined query, context, and response cannot exceed model token limits
2. **Rate Limits**: Subject to OpenAI's API rate limiting
3. **Model Availability**: Depends on selected model and account tier
4. **Content Restrictions**: Must comply with OpenAI's usage policies
5. **Response Length**: Limited by model's maximum output token setting
6. **Real-time Data**: AI knowledge is limited to training data cutoff date

## Best Practices

### 1. Craft Clear Queries
- Be specific about what you want to know
- Use clear, concise language
- Avoid ambiguous phrasing

### 2. Provide Relevant Context
- Include background information that helps the AI understand your domain
- Keep context focused and relevant
- Don't overwhelm with unnecessary details

### 3. Optimize for Your Use Case
- For factual questions: Be direct and specific
- For creative tasks: Provide style or format preferences
- For analysis: Include the data or topic to analyze

### 4. Handle Responses Appropriately
- Validate AI responses for accuracy when needed
- Use responses as starting points for further research
- Consider the AI's limitations for critical decisions

## Common Use Cases

### 1. Educational Support
```
Scenario: Students seeking explanations of complex topics
Action: Ask clear questions with relevant context
Result: Detailed, educational responses tailored to the topic
```

### 2. Content Creation
```
Scenario: Writers needing inspiration or assistance
Action: Request creative content with style preferences
Result: Original content that matches specified requirements
```

### 3. Problem Solving
```
Scenario: Professionals seeking solutions to challenges
Action: Describe the problem with relevant background
Result: Structured approaches and potential solutions
```

### 4. Research Assistance
```
Scenario: Researchers needing quick information on topics
Action: Ask specific questions about research areas
Result: Comprehensive overviews and key insights
```

## Related Catalog Requests

- [Ask LLM](pages/AskLLM.md) - Advanced querying with structured prompts
- [Get AI Model Name](pages/GetAIModelName.md) - Check current model configuration
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Verify model capabilities

## Technical Implementation

### Helper Class
- **Class**: QueryArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: Parameter validation and API key verification
- **Service**: QueryImpl.constructPromptAndQueryModel()

### Processing Flow
1. **Input Validation**: Verify required parameters
2. **Prompt Construction**: Automatically format query and context
3. **API Call**: Send request to OpenAI with configured model
4. **Response Processing**: Extract and return AI response
5. **Error Handling**: Manage any errors or retries

### Telemetry Metrics
- **QUERY_INITIATED**: Request started
- **QUERY_COMPLETED**: Successful response
- **QUERY_FAILED**: Error occurred
- **TOKEN_USAGE**: Track token consumption

## Troubleshooting

### No Response or Empty Response
**Cause**: Query may be too vague or context unclear
**Solution**:
1. Make query more specific
2. Add relevant context
3. Try rephrasing the question

### Unexpected Response Quality
**Cause**: Insufficient context or unclear query
**Solution**:
1. Provide more detailed context
2. Break complex queries into simpler parts
3. Specify desired response format

### Rate Limiting Issues
**Cause**: Too many requests in short time period
**Solution**:
1. Space out requests
2. Monitor API usage
3. Consider upgrading API tier

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Ask LLM](pages/AskLLM.md) - Advanced structured querying
