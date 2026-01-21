# Summarize Image

## Overview

This request takes an image file as input and generates a comprehensive summary of the image content using OpenAI's vision capabilities.

## Request Details

- **Area**: Multi Modal
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Input Image | File | Yes | Image file to be analyzed and summarized | image.jpg, photo.png |

### Supported Image Formats
- **JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- **WebP** (.webp)
- **BMP** (.bmp)

### Image Requirements
- **Maximum Size**: 20MB per image
- **Minimum Resolution**: 32x32 pixels
- **Maximum Resolution**: 8192x8192 pixels
- **Color Depth**: Supports both color and grayscale images

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Summary | Text | Detailed description and summary of the image content |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| File Not Provided | Input validation error | Provide a valid image file |
| Invalid File Format | File format error | Use supported image formats (JPEG, PNG, GIF, WebP, BMP) |
| File Too Large | File size error | Reduce image size to under 20MB |
| File Download Failed | Media server error | Ensure file is accessible and try again |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- No image file provided
- Invalid file format
- File too large

**Resolution**: 
1. Verify extension configuration
2. Ensure image file is provided
3. Check file format and size
4. Use supported image formats

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Corrupted image file
- Unsupported image characteristics
- File processing issues

**Resolution**:
1. Verify image file integrity
2. Try with a different image
3. Ensure image meets requirements

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or system issues
**Common Scenarios**:
- OpenAI vision service unavailable
- Network connectivity issues
- Media server access problems
- File processing failures

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Retry the request
4. Check media server accessibility

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

### Example 1: Photograph Summary
**Input**:
```
Input Image: family_vacation.jpg (photo of a family at the beach)
```

**Output**:
```
Summary: "This image shows a happy family of four enjoying a day at the beach. The photo captures two adults and two children standing on golden sand with ocean waves in the background. The family appears to be posing for the camera, with everyone smiling. The adults are wearing casual summer clothing, while the children are in colorful swimwear. The sky is clear and blue, suggesting perfect weather for a beach outing. Palm trees are visible in the distance, and there are other beachgoers in the background, indicating this is a popular beach destination."
```

### Example 2: Document Analysis
**Input**:
```
Input Image: chart.png (business chart with sales data)
```

**Output**:
```
Summary: "This image displays a professional bar chart showing quarterly sales performance. The chart has a clean, corporate design with blue bars representing sales figures across four quarters. The vertical axis shows revenue values ranging from $0 to $500K, while the horizontal axis displays Q1 through Q4. The data shows steady growth throughout the year, with Q1 starting at approximately $200K, Q2 at $275K, Q3 at $350K, and Q4 reaching $450K. The chart includes a title 'Quarterly Sales Performance 2023' and appears to be part of a business presentation or report."
```

### Example 3: Artwork Description
**Input**:
```
Input Image: painting.jpg (abstract artwork)
```

**Output**:
```
Summary: "This image features an abstract painting with vibrant colors and dynamic brushstrokes. The composition is dominated by bold strokes of blue, red, and yellow paint that seem to flow and blend across the canvas. The painting style appears to be expressionistic, with energetic, gestural marks that create a sense of movement and emotion. The colors are applied thickly in some areas, creating texture and depth. The overall composition is non-representational, focusing on color relationships and emotional expression rather than depicting recognizable objects or scenes."
```

## Business Rules

1. **Vision Model Usage**: Automatically uses OpenAI's vision-capable models for image analysis
2. **File Processing**: Images are securely downloaded and processed through Krista's media server
3. **Content Analysis**: Provides comprehensive descriptions including objects, scenes, text, and context
4. **Privacy**: Images are processed securely and not stored permanently
5. **Quality**: Summaries aim to be detailed, accurate, and contextually relevant

## Limitations

1. **File Size**: Maximum 20MB per image
2. **Resolution Limits**: Maximum 8192x8192 pixels
3. **Format Support**: Limited to common image formats
4. **Processing Time**: Larger images may take longer to process
5. **Content Restrictions**: Subject to OpenAI's content policy
6. **Model Availability**: Requires access to vision-capable models

## Best Practices

### 1. Image Quality
- Use clear, well-lit images for better analysis
- Ensure important details are visible and not obscured
- Higher resolution images generally provide better results

### 2. File Management
- Optimize image size for faster processing
- Use appropriate file formats (JPEG for photos, PNG for graphics)
- Ensure images are properly oriented

### 3. Use Case Optimization
- For documents: Ensure text is legible
- For photographs: Good lighting and composition help
- For charts/graphs: Clear labels and readable data points

### 4. Error Handling
- Implement retry logic for temporary failures
- Validate file formats before processing
- Handle large files appropriately

## Common Use Cases

### 1. Content Moderation
```
Scenario: Analyzing user-uploaded images for content review
Action: Summarize images to understand content
Result: Detailed descriptions for moderation decisions
```

### 2. Accessibility Support
```
Scenario: Providing image descriptions for visually impaired users
Action: Generate comprehensive image summaries
Result: Accessible content descriptions
```

### 3. Document Processing
```
Scenario: Analyzing business documents and charts
Action: Extract and summarize visual information
Result: Structured data from visual documents
```

### 4. Inventory Management
```
Scenario: Cataloging products from images
Action: Generate detailed product descriptions
Result: Automated inventory descriptions
```

## Related Catalog Requests

- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - Ask specific questions about images
- [Answer Question from Base64 Image](pages/AnswerQuestionFromBase64EncodedImage.md) - Process base64-encoded images
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Verify multimodal support

## Technical Implementation

### Helper Class
- **Class**: MultiModalArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: File validation and API key verification
- **Service**: QueryImpl.queryModel() with image processing

### Processing Flow
1. **File Validation**: Check file format, size, and accessibility
2. **File Download**: Retrieve image from media server
3. **Image Processing**: Convert to base64 for API transmission
4. **API Call**: Send to OpenAI vision model
5. **Response Processing**: Extract and return summary
6. **Cleanup**: Remove temporary files

### Telemetry Metrics
- **IMAGE_SUMMARY_INITIATED**: Request started
- **IMAGE_SUMMARY_COMPLETED**: Successful response
- **IMAGE_SUMMARY_FAILED**: Error occurred
- **IMAGE_PROCESSING_TIME**: Processing duration

## Troubleshooting

### File Access Issues
**Cause**: Cannot download or access image file
**Solution**:
1. Verify file exists and is accessible
2. Check media server connectivity
3. Ensure proper file permissions

### Poor Summary Quality
**Cause**: Image quality or content issues
**Solution**:
1. Use higher quality images
2. Ensure good lighting and clarity
3. Try different image formats

### Processing Timeouts
**Cause**: Large files or service overload
**Solution**:
1. Reduce image file size
2. Retry with smaller images
3. Check service status

### Vision Model Access
**Cause**: Model or billing restrictions
**Solution**:
1. Verify vision model access in OpenAI account
2. Check billing and usage limits
3. Ensure proper API permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - Interactive image analysis
