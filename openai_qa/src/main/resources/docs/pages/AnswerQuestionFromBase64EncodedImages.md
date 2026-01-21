# Answer Question from Base64 Encoded Images

## Overview

This request takes multiple base64-encoded images and a specific question as input, then uses OpenAI's vision capabilities to analyze all images collectively and provide a comprehensive answer based on the combined visual information.

## Request Details

- **Area**: Multi Modal
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Base64 Encoded Images | List | Yes | Array of objects containing file names and base64-encoded image strings | `[{"File Name": "page1.jpg", "Base 64 Encoded Image String": "data:image/jpeg;base64,/9j/4AAQ..."}]` |
| Question | Text | Yes | Specific question about the content across all images | "What is the total amount across all invoices?" |
| Context | Text | No | Additional context to help answer the question | "These are monthly invoices from different vendors" |

### Base64 Images Array Structure
Each image object in the array must contain:
- **File Name**: Descriptive name for the image (for reference)
- **Base 64 Encoded Image String**: Complete base64-encoded image data

### Supported Image Formats (when decoded)
- **JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- **WebP** (.webp)
- **BMP** (.bmp)

### Limitations per Request
- **Maximum Images**: 10 images per request
- **Total Size**: Combined decoded images should not exceed 100MB
- **Individual Size**: Each image maximum 20MB when decoded

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Answer | Text | Comprehensive answer based on analysis of all provided images |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| Images Array Empty | Input validation error | Provide at least one image in the array |
| Question Not Provided | Input validation error | Provide a specific question |
| Invalid Base64 Format | Encoding error | Ensure proper base64 encoding for all images |
| Too Many Images | Input validation error | Limit to maximum 10 images per request |
| Combined Size Too Large | Size error | Reduce total image size or number of images |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- Empty images array
- Invalid base64 encoding in one or more images
- Missing question parameter
- Too many images provided

**Resolution**: 
1. Verify extension configuration
2. Ensure images array contains valid base64 data
3. Provide clear, specific question
4. Limit to maximum 10 images
5. Validate all base64 encodings

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Question cannot be answered from provided images
- Inconsistent image formats
- Corrupted base64 data in one or more images

**Resolution**:
1. Ensure question relates to content visible across images
2. Verify all images are properly encoded
3. Check image quality and readability

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or processing issues
**Common Scenarios**:
- OpenAI vision service unavailable
- Network connectivity issues
- Processing timeout due to multiple images
- Memory issues with large image sets

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Reduce number or size of images
4. Retry the request

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid API key
- Insufficient permissions for vision models
- Billing account issues
- Usage quota exceeded

**Resolution**:
1. Verify API key is correct and active
2. Ensure vision model access is enabled
3. Check OpenAI account billing status
4. Monitor usage quotas

## Usage Examples

### Example 1: Multi-Page Document Analysis
**Input**:
```json
{
  "Base64 Encoded Images": [
    {
      "File Name": "invoice_page1.jpg",
      "Base 64 Encoded Image String": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..."
    },
    {
      "File Name": "invoice_page2.jpg", 
      "Base 64 Encoded Image String": "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAB..."
    }
  ],
  "Question": "What is the total amount due across all pages?",
  "Context": "Multi-page invoice document"
}
```

**Output**:
```
Answer: "Based on analysis of both pages, the total amount due is $3,247.85. Page 1 shows line items totaling $2,100.50, and page 2 shows additional charges of $1,147.35, resulting in the combined total of $3,247.85 as shown in the final summary section."
```

### Example 2: Comparative Analysis
**Input**:
```json
{
  "Base64 Encoded Images": [
    {
      "File Name": "chart_q1.png",
      "Base 64 Encoded Image String": "iVBORw0KGgoAAAANSUhEUgAAAlgAAAJYCAYAAAC..."
    },
    {
      "File Name": "chart_q2.png",
      "Base 64 Encoded Image String": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAB..."
    }
  ],
  "Question": "Which quarter had better sales performance?",
  "Context": "Quarterly sales comparison charts"
}
```

**Output**:
```
Answer: "Q2 had better sales performance compared to Q1. The Q1 chart shows total sales of $450,000, while the Q2 chart shows total sales of $620,000, representing a 37.8% increase in sales performance from Q1 to Q2."
```

### Example 3: Sequential Process Analysis
**Input**:
```json
{
  "Base64 Encoded Images": [
    {
      "File Name": "step1.jpg",
      "Base 64 Encoded Image String": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..."
    },
    {
      "File Name": "step2.jpg",
      "Base 64 Encoded Image String": "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAB..."
    },
    {
      "File Name": "step3.jpg",
      "Base 64 Encoded Image String": "data:image/jpeg;base64,R0lGODlhAQABAIAAAAAAAP..."
    }
  ],
  "Question": "What changes occur throughout this process?",
  "Context": "Manufacturing process documentation"
}
```

**Output**:
```
Answer: "The process shows a clear transformation sequence: Step 1 shows raw materials being prepared and measured. Step 2 demonstrates the mixing and heating phase where materials are combined at high temperature. Step 3 shows the final cooling and shaping stage where the product takes its final form. The key changes are: material state (solid to liquid to solid), color (from white/clear to amber to final brown), and shape (loose materials to mixed solution to formed product)."
```

## Business Rules

1. **Multi-Image Analysis**: AI analyzes all images collectively to provide comprehensive answers
2. **Sequential Processing**: Images are processed in the order provided in the array
3. **Cross-Reference Capability**: Can identify relationships and patterns across multiple images
4. **Context Integration**: Uses provided context to better understand the relationship between images
5. **Comprehensive Responses**: Answers consider information from all provided images

## Limitations

1. **Image Count**: Maximum 10 images per request
2. **Total Size**: Combined decoded images limited to 100MB
3. **Processing Time**: Multiple images increase processing time significantly
4. **Memory Usage**: Large image sets require substantial memory
5. **Complexity**: More images may reduce accuracy for very specific details
6. **Token Limits**: Combined image analysis may approach model token limits

## Best Practices

### 1. Image Organization
- Provide images in logical order (chronological, sequential, etc.)
- Use descriptive file names for better context
- Ensure all images are relevant to the question

### 2. Question Formulation
- Ask questions that require analysis across multiple images
- Be specific about what relationships or patterns to identify
- Consider the collective information available

### 3. Performance Optimization
- Limit to necessary images only
- Compress images before base64 encoding
- Use appropriate image formats for content type

### 4. Context Usage
- Explain the relationship between images
- Provide background about the sequence or collection
- Include relevant domain-specific information

## Common Use Cases

### 1. Document Processing
```
Scenario: Analyzing multi-page documents or forms
Action: Process all pages together for comprehensive analysis
Result: Complete understanding of document content across pages
```

### 2. Comparative Analysis
```
Scenario: Comparing multiple charts, reports, or visual data
Action: Analyze differences, trends, or patterns across images
Result: Comprehensive comparative insights
```

### 3. Process Documentation
```
Scenario: Understanding sequential processes or workflows
Action: Analyze step-by-step visual documentation
Result: Complete process understanding and change identification
```

### 4. Quality Control
```
Scenario: Inspecting multiple views or stages of products
Action: Analyze quality indicators across multiple images
Result: Comprehensive quality assessment
```

## Related Catalog Requests

- [Answer Question from Base64 Image](pages/AnswerQuestionFromBase64EncodedImage.md) - Single image analysis
- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - File-based image analysis
- [Summarize Image](pages/SummarizeImage.md) - Individual image summaries

## Technical Implementation

### Helper Class
- **Class**: MultiModalArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: Multi-image validation and processing
- **Service**: QueryImpl.queryModel() with multi-image support

### Processing Flow
1. **Input Validation**: Verify images array, question, and configuration
2. **Base64 Processing**: Decode and validate all images
3. **Size Validation**: Check individual and combined image sizes
4. **Prompt Construction**: Combine question, context, and all images
5. **API Call**: Send multi-image request to OpenAI vision model
6. **Response Processing**: Extract comprehensive answer

### Multi-Image Handling
- **Sequential Processing**: Images processed in array order
- **Memory Management**: Efficient handling of multiple large images
- **Error Isolation**: Individual image validation with collective processing
- **Token Optimization**: Efficient prompt construction for multiple images

### Telemetry Metrics
- **MULTI_IMAGE_QUESTION_INITIATED**: Request started
- **MULTI_IMAGE_QUESTION_COMPLETED**: Successful response
- **MULTI_IMAGE_QUESTION_FAILED**: Error occurred
- **IMAGE_COUNT**: Number of images processed
- **TOTAL_PROCESSING_TIME**: Complete processing duration

## Troubleshooting

### Processing Timeouts
**Cause**: Too many or too large images
**Solution**:
1. Reduce number of images
2. Compress images before encoding
3. Split into multiple requests if needed

### Memory Issues
**Cause**: Large combined image size
**Solution**:
1. Optimize image sizes before encoding
2. Use more efficient image formats
3. Process in smaller batches

### Inconsistent Results
**Cause**: Too many images or unclear question
**Solution**:
1. Limit to most relevant images
2. Make question more specific
3. Provide better context about image relationships

### Base64 Encoding Errors
**Cause**: Invalid encoding in one or more images
**Solution**:
1. Validate each base64 string individually
2. Check for proper formatting
3. Ensure all images use supported formats

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Answer Question from Base64 Image](pages/AnswerQuestionFromBase64EncodedImage.md) - Single image alternative
