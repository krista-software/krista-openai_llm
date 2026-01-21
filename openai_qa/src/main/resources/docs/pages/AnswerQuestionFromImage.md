# Answer Question from Image

## Overview

This request takes an image file and a specific question as input, then uses OpenAI's vision capabilities to analyze the image and provide a targeted answer to the question.

## Request Details

- **Area**: Multi Modal
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Input Image | File | Yes | Image file to be analyzed | document.pdf, photo.jpg |
| Question | Text | Yes | Specific question about the image content | "What is the total amount on this invoice?" |
| Context | Text | No | Additional context to help answer the question | "This is a business invoice from Q3 2023" |

### Supported Image Formats
- **JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- **WebP** (.webp)
- **BMP** (.bmp)
- **PDF** (first page only)

### Image Requirements
- **Maximum Size**: 20MB per image
- **Minimum Resolution**: 32x32 pixels
- **Maximum Resolution**: 8192x8192 pixels
- **Color Depth**: Supports both color and grayscale images

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Answer | Text | Specific answer to the question based on image analysis |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| File Not Provided | Input validation error | Provide a valid image file |
| Question Not Provided | Input validation error | Provide a specific question |
| Invalid File Format | File format error | Use supported image formats |
| File Too Large | File size error | Reduce image size to under 20MB |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- No image file provided
- Empty or missing question
- Invalid file format

**Resolution**: 
1. Verify extension configuration
2. Ensure both image and question are provided
3. Check file format and size
4. Use clear, specific questions

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Question cannot be answered from image content
- Corrupted or unreadable image
- Ambiguous question phrasing

**Resolution**:
1. Ensure question relates to visible image content
2. Use clear, specific question phrasing
3. Verify image quality and readability

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or system issues
**Common Scenarios**:
- OpenAI vision service unavailable
- Network connectivity issues
- File processing failures
- Media server access problems

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Retry the request
4. Check file accessibility

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid API key
- Insufficient permissions for vision models
- Billing account issues
- Model access restrictions

**Resolution**:
1. Verify API key is correct and active
2. Ensure vision model access is enabled
3. Check OpenAI account billing status
4. Verify model permissions

## Usage Examples

### Example 1: Invoice Analysis
**Input**:
```
Input Image: invoice.pdf
Question: "What is the total amount due on this invoice?"
Context: "This is a business invoice from our vendor"
```

**Output**:
```
Answer: "The total amount due on this invoice is $2,847.50. This amount is clearly displayed in the 'Total Due' section at the bottom of the invoice."
```

### Example 2: Chart Data Extraction
**Input**:
```
Input Image: sales_chart.png
Question: "What was the sales figure for Q3?"
Context: "Quarterly sales performance chart"
```

**Output**:
```
Answer: "According to the chart, the sales figure for Q3 was $350,000. This is shown by the third bar in the quarterly sales chart, which reaches the $350K mark on the vertical axis."
```

### Example 3: Photo Information
**Input**:
```
Input Image: event_photo.jpg
Question: "How many people are in this photo?"
Context: (empty)
```

**Output**:
```
Answer: "There are 8 people visible in this photo. I can see 5 people standing in the front row and 3 people in the back row, all appearing to be at some kind of group event or gathering."
```

### Example 4: Document Text Extraction
**Input**:
```
Input Image: contract.pdf
Question: "What is the contract expiration date?"
Context: "Legal contract document"
```

**Output**:
```
Answer: "The contract expiration date is December 31, 2024. This date is specified in Section 3 of the contract under 'Term and Termination' clause."
```

## Business Rules

1. **Question-Focused Analysis**: AI focuses specifically on answering the provided question
2. **Context Integration**: Optional context helps improve answer accuracy and relevance
3. **Visual Content Only**: Can only answer questions about what's visible in the image
4. **Accuracy Priority**: Aims to provide precise, factual answers based on image content
5. **Clarity Requirement**: Questions should be clear and specific for best results

## Limitations

1. **Visual Content Only**: Cannot answer questions about information not visible in the image
2. **File Size**: Maximum 20MB per image
3. **Resolution Limits**: Maximum 8192x8192 pixels
4. **Text Readability**: Text in images must be clear and legible
5. **Question Scope**: Questions must relate to visible image content
6. **Model Availability**: Requires access to vision-capable models

## Best Practices

### 1. Question Formulation
- Be specific and clear about what you want to know
- Ask about visible elements in the image
- Use context to provide background information

### 2. Image Quality
- Ensure text is legible if asking about written content
- Use high-resolution images for detailed questions
- Ensure good lighting and clarity

### 3. Context Usage
- Provide relevant background information
- Explain the type of document or image
- Include any domain-specific context

### 4. Question Types
- **Factual**: "What is the date on this document?"
- **Counting**: "How many items are shown?"
- **Descriptive**: "What color is the car in the image?"
- **Analytical**: "What trend does this chart show?"

## Common Use Cases

### 1. Document Processing
```
Scenario: Extracting specific information from business documents
Action: Ask targeted questions about document content
Result: Precise data extraction from visual documents
```

### 2. Quality Control
```
Scenario: Inspecting products or processes from images
Action: Ask specific questions about quality indicators
Result: Automated quality assessment responses
```

### 3. Data Entry Automation
```
Scenario: Converting visual information to structured data
Action: Ask specific questions about form fields or data points
Result: Automated data extraction and entry
```

### 4. Content Analysis
```
Scenario: Analyzing visual content for specific information
Action: Ask targeted questions about image elements
Result: Focused analysis and information extraction
```

## Related Catalog Requests

- [Summarize Image](pages/SummarizeImage.md) - Get comprehensive image summaries
- [Answer Question from Base64 Image](pages/AnswerQuestionFromBase64EncodedImage.md) - Process base64-encoded images
- [Answer Question from Multiple Base64 Images](pages/AnswerQuestionFromBase64EncodedImages.md) - Analyze multiple images

## Technical Implementation

### Helper Class
- **Class**: MultiModalArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: File and parameter validation
- **Service**: QueryImpl.queryModel() with vision processing

### Processing Flow
1. **Input Validation**: Verify file, question, and configuration
2. **File Processing**: Download and convert image to base64
3. **Prompt Construction**: Combine question, context, and image
4. **API Call**: Send to OpenAI vision model
5. **Response Processing**: Extract targeted answer
6. **Cleanup**: Remove temporary files

### Telemetry Metrics
- **QUESTION_IMAGE_INITIATED**: Request started
- **QUESTION_IMAGE_COMPLETED**: Successful response
- **QUESTION_IMAGE_FAILED**: Error occurred
- **PROCESSING_TIME**: Total processing duration

## Troubleshooting

### Poor Answer Quality
**Cause**: Unclear question or poor image quality
**Solution**:
1. Make questions more specific
2. Improve image quality and resolution
3. Add relevant context information

### Cannot Find Information
**Cause**: Information not visible in image or unclear question
**Solution**:
1. Verify information is actually visible in the image
2. Rephrase question more clearly
3. Check image quality and readability

### File Processing Issues
**Cause**: File format or accessibility problems
**Solution**:
1. Use supported file formats
2. Ensure file is accessible
3. Check file size limits

### Vision Model Access
**Cause**: Model or billing restrictions
**Solution**:
1. Verify vision model access in OpenAI account
2. Check billing and usage limits
3. Ensure proper API permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Summarize Image](pages/SummarizeImage.md) - General image analysis
