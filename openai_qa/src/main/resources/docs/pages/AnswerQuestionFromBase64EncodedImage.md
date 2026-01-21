# Answer Question from Base64 Encoded Image

## Overview

This request takes a base64-encoded image string and a specific question as input, then uses OpenAI's vision capabilities to analyze the image and provide a targeted answer to the question.

## Request Details

- **Area**: Multi Modal
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Base 64 Encoded Image String | Text | Yes | Base64-encoded string representation of the image | "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..." |
| Question | Text | Yes | Specific question about the image content | "What text is visible in this image?" |
| Context | Text | No | Additional context to help answer the question | "This is a screenshot of a mobile app interface" |

### Base64 Format Requirements
- **Format**: Standard base64 encoding
- **Data URI**: Can include data URI prefix (e.g., "data:image/jpeg;base64,")
- **Image Types**: JPEG, PNG, GIF, WebP, BMP
- **Size Limit**: Equivalent to 20MB when decoded

### Supported Image Formats (when decoded)
- **JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- **WebP** (.webp)
- **BMP** (.bmp)

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Answer | Text | Specific answer to the question based on image analysis |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| Base64 String Not Provided | Input validation error | Provide a valid base64-encoded image string |
| Question Not Provided | Input validation error | Provide a specific question |
| Invalid Base64 Format | Encoding error | Ensure proper base64 encoding |
| Decoded Image Too Large | Size error | Reduce image size before encoding |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- Invalid base64 string
- Empty or missing question
- Malformed base64 encoding

**Resolution**: 
1. Verify extension configuration
2. Ensure valid base64-encoded image string
3. Provide clear, specific question
4. Validate base64 encoding format

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Question cannot be answered from image content
- Corrupted base64 data
- Unsupported image format after decoding

**Resolution**:
1. Verify base64 string represents valid image
2. Ensure question relates to visible content
3. Use supported image formats

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or processing issues
**Common Scenarios**:
- OpenAI vision service unavailable
- Network connectivity issues
- Base64 decoding failures
- Image processing errors

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Validate base64 encoding
4. Retry the request

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid API key
- Insufficient permissions for vision models
- Billing account issues

**Resolution**:
1. Verify API key is correct and active
2. Ensure vision model access is enabled
3. Check OpenAI account billing status

## Usage Examples

### Example 1: Screenshot Analysis
**Input**:
```
Base 64 Encoded Image String: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB..."
Question: "What is the main button text in this screenshot?"
Context: "Mobile app login screen"
```

**Output**:
```
Answer: "The main button text in this screenshot is 'Sign In'. It appears as a blue button prominently displayed in the center of the login screen."
```

### Example 2: Document Text Extraction
**Input**:
```
Base 64 Encoded Image String: "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj..."
Question: "What is the invoice number?"
Context: "Business invoice document"
```

**Output**:
```
Answer: "The invoice number is INV-2023-001247. This number is displayed in the top right section of the invoice header."
```

### Example 3: Chart Data Reading
**Input**:
```
Base 64 Encoded Image String: "iVBORw0KGgoAAAANSUhEUgAAAlgAAAJYCAYAAAC+ZpjcAAAABHNCSVQICAgIfAhkiAAAAAlwSFlz..."
Question: "What is the highest value shown on this bar chart?"
Context: "Sales performance data visualization"
```

**Output**:
```
Answer: "The highest value shown on this bar chart is $450,000, which corresponds to Q4 sales performance. This is represented by the tallest bar on the right side of the chart."
```

## Business Rules

1. **Base64 Processing**: Automatically decodes base64 strings to image format
2. **Question-Focused Analysis**: AI focuses specifically on answering the provided question
3. **Context Integration**: Optional context improves answer accuracy
4. **Format Flexibility**: Accepts base64 with or without data URI prefix
5. **Size Validation**: Ensures decoded image meets size requirements

## Limitations

1. **Encoding Overhead**: Base64 encoding increases data size by ~33%
2. **Processing Time**: Base64 decoding adds processing overhead
3. **Size Limits**: Decoded image must be under 20MB
4. **Format Support**: Limited to standard image formats
5. **Visual Content Only**: Can only answer about visible image content
6. **Memory Usage**: Large base64 strings require significant memory

## Best Practices

### 1. Base64 Encoding
- Use standard base64 encoding
- Include appropriate data URI prefix when possible
- Validate encoding before sending
- Optimize image size before encoding

### 2. Question Formulation
- Be specific about what information you need
- Ask about elements visible in the image
- Use clear, unambiguous language

### 3. Performance Optimization
- Compress images before base64 encoding
- Use appropriate image formats (JPEG for photos, PNG for graphics)
- Consider file size impact of base64 encoding

### 4. Error Handling
- Validate base64 format before processing
- Handle encoding/decoding errors gracefully
- Implement retry logic for temporary failures

## Common Use Cases

### 1. API Integration
```
Scenario: Processing images received via API as base64 strings
Action: Analyze base64 images and extract specific information
Result: Automated processing of API-delivered image content
```

### 2. Mobile App Integration
```
Scenario: Processing camera captures from mobile applications
Action: Send base64-encoded camera images for analysis
Result: Real-time image analysis in mobile workflows
```

### 3. Web Application Processing
```
Scenario: Analyzing images uploaded through web forms
Action: Convert uploaded images to base64 and process
Result: Seamless web-based image analysis
```

### 4. Batch Processing
```
Scenario: Processing multiple images stored as base64 strings
Action: Iterate through base64 images with specific questions
Result: Automated batch analysis of image collections
```

## Related Catalog Requests

- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - Process image files directly
- [Answer Question from Multiple Base64 Images](pages/AnswerQuestionFromBase64EncodedImages.md) - Analyze multiple base64 images
- [Summarize Image from Base64 Encoded](pages/SummarizeImageFromBase64Encoded.md) - Get comprehensive summaries

## Technical Implementation

### Helper Class
- **Class**: MultiModalArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: Base64 format and parameter validation
- **Service**: QueryImpl.queryModel() with base64 processing

### Processing Flow
1. **Input Validation**: Verify base64 string and question
2. **Base64 Decoding**: Convert string to image format
3. **Image Validation**: Check decoded image format and size
4. **Prompt Construction**: Combine question, context, and image
5. **API Call**: Send to OpenAI vision model
6. **Response Processing**: Extract targeted answer

### Base64 Handling
- **Data URI Support**: Handles "data:image/type;base64," prefixes
- **Format Detection**: Automatically detects image format
- **Size Validation**: Checks decoded image size
- **Memory Management**: Efficient handling of large base64 strings

### Telemetry Metrics
- **BASE64_QUESTION_INITIATED**: Request started
- **BASE64_QUESTION_COMPLETED**: Successful response
- **BASE64_QUESTION_FAILED**: Error occurred
- **BASE64_DECODE_TIME**: Decoding duration

## Troubleshooting

### Base64 Decoding Errors
**Cause**: Invalid or corrupted base64 string
**Solution**:
1. Validate base64 encoding format
2. Check for missing or extra characters
3. Ensure proper padding
4. Verify data URI format if used

### Large File Issues
**Cause**: Base64 string represents image too large
**Solution**:
1. Compress image before encoding
2. Reduce image resolution
3. Use more efficient image formats

### Poor Answer Quality
**Cause**: Unclear question or poor image quality
**Solution**:
1. Make questions more specific
2. Ensure original image quality before encoding
3. Add relevant context information

### Memory Issues
**Cause**: Very large base64 strings
**Solution**:
1. Optimize image size before encoding
2. Use streaming processing if available
3. Consider using file-based alternatives

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - File-based alternative
