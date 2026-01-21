# Get LLM Capabilities

## Overview

Gets the capabilities of the OpenAI generative AI provider. This request returns information about what features and task complexities are supported by the OpenAI extension.

## Request Details

- **Area**: Query
- **Type**: QUERY_SYSTEM
- **Retry Support**: ❌ No (Static capability information, no external API calls required)

## Input Parameters

This request does not require any input parameters.

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Multimodal support | Boolean | Whether the provider supports processing multiple types of media (text, images) |
| Hard Task support | Boolean | Whether the provider can handle complex, challenging tasks |
| Medium Task support | Boolean | Whether the provider can handle moderately complex tasks |
| Easy Task support | Boolean | Whether the provider can handle simple, straightforward tasks |

### Capability Definitions

#### Multimodal Support
- **Definition**: Ability to process and understand multiple types of input (text, images, audio)
- **OpenAI Support**: ✅ Yes - Supports text and image processing
- **Examples**: Image analysis, visual question answering, image generation

#### Hard Task Support
- **Definition**: Complex reasoning, advanced analysis, sophisticated problem-solving
- **OpenAI Support**: ✅ Yes - Advanced reasoning capabilities
- **Examples**: Complex code generation, detailed analysis, multi-step reasoning

#### Medium Task Support
- **Definition**: Moderate complexity tasks requiring some reasoning
- **OpenAI Support**: ✅ Yes - Well-suited for standard tasks
- **Examples**: Content creation, basic analysis, structured responses

#### Easy Task Support
- **Definition**: Simple, straightforward tasks with minimal complexity
- **OpenAI Support**: ✅ Yes - Excellent for basic tasks
- **Examples**: Simple questions, basic text generation, formatting

## Validation Rules

This request performs minimal validation as it returns static capability information:

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Service Unavailable | Service error | Check extension status |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Extension configuration issues (rare)
**Common Scenarios**:
- Extension not properly initialized

**Resolution**: 
1. Verify extension is properly configured
2. Check extension installation

### System Errors (SYSTEM_ERROR)
**Cause**: System-level issues (very rare)
**Common Scenarios**:
- Extension service unavailable
- Capability service issues

**Resolution**:
1. Restart the extension
2. Check system status
3. Contact support if needed

## Usage Examples

### Example 1: Standard Capability Check
**Input**: (No parameters required)

**Output**:
```json
{
  "Multimodal support": true,
  "Hard Task support": true,
  "Medium Task support": true,
  "Easy Task support": true
}
```

**Description**: OpenAI supports all capability levels and multimodal processing

### Example 2: Integration Planning
**Input**: (No parameters required)

**Output**:
```json
{
  "Multimodal support": true,
  "Hard Task support": true,
  "Medium Task support": true,
  "Easy Task support": true
}
```

**Description**: Use this information to plan which types of tasks to route to OpenAI

### Example 3: Feature Discovery
**Input**: (No parameters required)

**Output**:
```json
{
  "Multimodal support": true,
  "Hard Task support": true,
  "Medium Task support": true,
  "Easy Task support": true
}
```

**Description**: Discover available features before implementing AI workflows

## Business Rules

1. **Static Capabilities**: Returns fixed capability information for OpenAI
2. **Provider-Specific**: Reflects OpenAI's actual capabilities
3. **Comprehensive Support**: OpenAI supports all defined capability levels
4. **Multimodal Ready**: Confirms support for text and image processing
5. **Task Complexity**: Supports all complexity levels from easy to hard

## Limitations

1. **Static Information**: Does not reflect real-time API availability
2. **General Capabilities**: Does not specify model-specific differences
3. **No Performance Metrics**: Does not indicate speed or quality levels
4. **No Cost Information**: Does not include pricing or usage cost details
5. **No Rate Limits**: Does not specify current rate limiting status

## Best Practices

### 1. Integration Planning
- Check capabilities before designing AI workflows
- Use capability information to route appropriate tasks
- Plan feature sets based on supported capabilities

### 2. User Interface Design
- Display available capabilities to users
- Enable/disable features based on capabilities
- Provide appropriate task options

### 3. Workflow Optimization
- Route complex tasks to providers with hard task support
- Utilize multimodal capabilities for rich content processing
- Match task complexity to provider capabilities

## Common Use Cases

### 1. Feature Discovery
```
Scenario: New integration planning
Action: Check LLM capabilities to understand available features
Result: Comprehensive list of supported capabilities for planning
```

### 2. Task Routing
```
Scenario: Multi-provider AI system
Action: Check capabilities to route tasks to appropriate providers
Result: Optimal task distribution based on provider strengths
```

### 3. User Interface Configuration
```
Scenario: Dynamic UI based on AI capabilities
Action: Query capabilities to enable/disable features
Result: UI that reflects actual available functionality
```

### 4. Workflow Validation
```
Scenario: Validating planned AI workflows
Action: Confirm required capabilities are supported
Result: Validated workflow design with supported features
```

## Related Catalog Requests

- [Get AI Model Name](pages/GetAIModelName.md) - Check current model configuration
- [Ask LLM](pages/AskLLM.md) - Use hard task capabilities for complex queries
- [Ask a Question](pages/AskAQuestion.md) - Use easy task capabilities for simple queries
- [Summarize Image](pages/SummarizeImage.md) - Use multimodal capabilities for image processing

## Technical Implementation

### Helper Class
- **Class**: QueryArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Service**: CapabilitiesService.getCapabilities()

### Implementation Details
```java
public Map<String, Object> getCapabilities(){
    Map<String, Object> capabilities = new HashMap<>();
    capabilities.put("Multimodal support", true);
    capabilities.put("Hard Task support", true);
    capabilities.put("Medium Task support", true);
    capabilities.put("Easy Task support", true);
    return capabilities;
}
```

### Capability Mapping
- **Multimodal support**: true (Text + Image processing)
- **Hard Task support**: true (Complex reasoning, advanced analysis)
- **Medium Task support**: true (Standard content generation, analysis)
- **Easy Task support**: true (Simple questions, basic tasks)

### Telemetry Metrics
- **CAPABILITIES_REQUESTED**: Request initiated
- **CAPABILITIES_RETURNED**: Successful response

## Capability Details

### Multimodal Support Features
- **Text Processing**: Natural language understanding and generation
- **Image Analysis**: Visual content understanding and description
- **Image Generation**: Creating images from text descriptions
- **Combined Processing**: Analyzing text and images together

### Task Complexity Support

#### Easy Tasks
- Simple question answering
- Basic text formatting
- Straightforward content generation
- Simple translations

#### Medium Tasks
- Content creation with specific requirements
- Structured data analysis
- Code explanation and documentation
- Moderate complexity problem solving

#### Hard Tasks
- Complex reasoning and analysis
- Advanced code generation
- Multi-step problem solving
- Sophisticated content creation with multiple constraints

## Troubleshooting

### Unexpected Capability Values
**Cause**: Service configuration issues (very rare)
**Solution**:
1. Restart the extension
2. Check service status
3. Contact support if values are incorrect

### Service Unavailable
**Cause**: Extension or capability service issues
**Solution**:
1. Verify extension is running
2. Check system status
3. Restart extension if needed

### Integration Issues
**Cause**: Misunderstanding capability definitions
**Solution**:
1. Review capability definitions above
2. Test with appropriate task types
3. Consult documentation for task examples

## Integration Notes

### Workflow Planning
- Use capability information early in workflow design
- Plan task distribution based on supported capabilities
- Design fallback strategies for unsupported features

### Performance Considerations
- Fast response time (no external API calls)
- Suitable for frequent capability checks
- Cache results if needed for high-frequency access

### Feature Enablement
- Enable multimodal features when multimodal support is true
- Provide appropriate task options based on complexity support
- Design UI elements based on available capabilities

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Get AI Model Name](pages/GetAIModelName.md) - Check current model
- [Multimodal Operations](pages/SummarizeImage.md) - Use multimodal capabilities
