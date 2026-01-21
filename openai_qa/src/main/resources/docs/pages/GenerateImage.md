# Generate Image

## Overview

Generate images using text prompts and return a media ID for the created image. This request uses OpenAI's DALL-E image generation capabilities to create original images based on descriptive text prompts.

## Request Details

- **Area**: Multi Modal
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes (Automatic retry with exponential backoff for rate limiting and temporary failures)

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Prompt | Text | Yes | Descriptive text prompt for image generation | "A serene mountain landscape at sunset with a lake reflection" |

### Prompt Guidelines
- **Length**: 1-4000 characters recommended
- **Detail Level**: More detailed prompts generally produce better results
- **Style Specification**: Include artistic style, mood, or technical specifications
- **Content Policy**: Must comply with OpenAI's content policy guidelines

### Prompt Best Practices
- **Be Descriptive**: Include details about subjects, setting, style, and mood
- **Specify Style**: Mention artistic styles (e.g., "photorealistic", "watercolor", "digital art")
- **Include Composition**: Describe layout, perspective, and framing
- **Add Atmosphere**: Mention lighting, weather, time of day, or mood

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Media Id | Text | Unique identifier for the generated image in Krista's media system |

### Media ID Usage
- **Storage**: Image is stored in Krista's media server
- **Access**: Use Media ID to retrieve or display the image
- **Persistence**: Images are stored according to system retention policies
- **Format**: Generated images are typically in PNG format

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "OpenAI API key is required to use this extension..." | Configure API key in extension settings |
| Model Not Selected | "Please select an OpenAI model to continue..." | Select a model in extension configuration |
| Prompt Not Provided | Input validation error | Provide a descriptive text prompt |
| Prompt Too Long | Input validation error | Reduce prompt length to under 4000 characters |
| Content Policy Violation | Content policy error | Modify prompt to comply with OpenAI policies |
| Image Count Invalid | "Image count must be between 1 and 10..." | Adjust number of images to 1-10 |
| Image Quality Invalid | "Image quality must be either 'standard' or 'hd'..." | Use 'standard' or 'hd' quality |
| Image Size Invalid | "Image size must be one of: 256x256, 512x512, or 1024x1024..." | Use supported image dimensions |

## Error Handling

For comprehensive troubleshooting steps and solutions, see the [Troubleshooting Guide](pages/Troubleshooting.md).

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key configuration
- No model selected
- Empty or missing prompt
- Prompt exceeds length limits
- Invalid characters in prompt

**Resolution**: 
1. Verify extension configuration
2. Provide descriptive prompt text
3. Check prompt length and content
4. Ensure prompt complies with content policies

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic validation failures
**Common Scenarios**:
- Prompt violates content policies
- Ambiguous or unclear prompt
- Unsupported image generation requests

**Resolution**:
1. Review and modify prompt content
2. Make prompt more specific and clear
3. Ensure compliance with OpenAI guidelines

### System Errors (SYSTEM_ERROR)
**Cause**: OpenAI API or system issues
**Common Scenarios**:
- DALL-E service unavailable
- Network connectivity issues
- Image generation failures
- Media server storage issues

**Resolution**:
1. Check OpenAI service status
2. Verify network connectivity
3. Retry the request
4. Check media server availability

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid API key
- Insufficient permissions for DALL-E
- Billing account issues
- Usage quota exceeded

**Resolution**:
1. Verify API key is correct and active
2. Ensure DALL-E access is enabled
3. Check OpenAI account billing status
4. Monitor usage quotas

## Usage Examples

### Example 1: Landscape Generation
**Input**:
```
Prompt: "A serene mountain landscape at sunset with a crystal-clear lake reflecting the orange and pink sky, surrounded by pine trees, photorealistic style"
```

**Output**:
```
Media Id: "img_abc123def456ghi789"
```

**Description**: Generates a photorealistic landscape image stored with the returned media ID

### Example 2: Product Visualization
**Input**:
```
Prompt: "A modern minimalist coffee mug on a white background, clean product photography style, soft lighting, high resolution"
```

**Output**:
```
Media Id: "img_xyz789abc123def456"
```

**Description**: Creates a product-style image suitable for marketing or catalog use

### Example 3: Artistic Creation
**Input**:
```
Prompt: "An abstract digital art piece featuring flowing geometric shapes in blue and gold, modern contemporary style, dynamic composition"
```

**Output**:
```
Media Id: "img_def456ghi789abc123"
```

**Description**: Generates an abstract artistic image with specified colors and style

### Example 4: Character Design
**Input**:
```
Prompt: "A friendly cartoon robot character with blue and silver colors, large expressive eyes, standing pose, children's book illustration style"
```

**Output**:
```
Media Id: "img_ghi789abc123def456"
```

**Description**: Creates a character illustration suitable for children's content

## Business Rules

1. **Content Policy Compliance**: All prompts must comply with OpenAI's usage policies
2. **Image Ownership**: Generated images follow OpenAI's terms regarding usage rights
3. **Media Storage**: Images are stored in Krista's media system with unique IDs
4. **Generation Limits**: Subject to OpenAI's rate limits and usage quotas
5. **Quality Assurance**: DALL-E automatically optimizes image quality and resolution

## Limitations

1. **Content Restrictions**: Must comply with OpenAI's content policy
2. **Prompt Length**: Maximum 4000 characters
3. **Generation Time**: Image creation may take 10-30 seconds
4. **Rate Limits**: Subject to OpenAI's API rate limiting
5. **Style Consistency**: Results may vary between generations
6. **Real People**: Cannot generate images of real, identifiable people

## Best Practices

### 1. Prompt Engineering
- Be specific about desired style and composition
- Include technical details (lighting, perspective, quality)
- Specify artistic medium or style
- Describe mood and atmosphere

### 2. Content Guidelines
- Avoid requesting copyrighted characters or brands
- Don't request images of real people
- Keep content appropriate and policy-compliant
- Focus on original, creative concepts

### 3. Quality Optimization
- Use descriptive adjectives for better results
- Specify image quality terms ("high resolution", "detailed")
- Include composition guidance ("centered", "close-up")
- Mention lighting preferences

### 4. Iteration Strategy
- Start with detailed prompts
- Refine based on initial results
- Experiment with different style keywords
- Build prompt libraries for consistent results

## Common Use Cases

### 1. Marketing Content
```
Scenario: Creating original images for marketing campaigns
Action: Generate branded visuals with specific style requirements
Result: Custom marketing images with unique media IDs
```

### 2. Product Visualization
```
Scenario: Visualizing products before manufacturing
Action: Generate product concepts from descriptions
Result: Visual prototypes for design validation
```

### 3. Creative Content
```
Scenario: Generating artwork for digital projects
Action: Create original art pieces from creative prompts
Result: Unique artistic content for various applications
```

### 4. Educational Materials
```
Scenario: Creating illustrations for educational content
Action: Generate specific visual examples and diagrams
Result: Custom educational imagery
```

## Related Catalog Requests

- [Summarize Image](pages/SummarizeImage.md) - Analyze generated images
- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - Query generated images
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Verify multimodal support

## Technical Implementation

### Helper Class
- **Class**: MultiModalArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog
- **Validation**: Prompt validation and API key verification
- **Service**: DALL-E API integration through OpenAI

### Processing Flow
1. **Input Validation**: Verify prompt and configuration
2. **Content Policy Check**: Validate prompt compliance
3. **API Call**: Send generation request to DALL-E
4. **Image Processing**: Receive generated image
5. **Media Storage**: Store image in Krista media system
6. **Response**: Return media ID for access

### Image Specifications
- **Format**: PNG (typical output format)
- **Resolution**: High resolution (varies by model)
- **Quality**: Optimized by DALL-E
- **Storage**: Krista media server

### Telemetry Metrics
- **IMAGE_GENERATION_INITIATED**: Request started
- **IMAGE_GENERATION_COMPLETED**: Successful generation
- **IMAGE_GENERATION_FAILED**: Error occurred
- **GENERATION_TIME**: Total processing duration

## Troubleshooting

### Content Policy Violations
**Cause**: Prompt violates OpenAI's content policies
**Solution**:
1. Review OpenAI's content policy guidelines
2. Modify prompt to remove problematic content
3. Focus on original, appropriate concepts

### Poor Image Quality
**Cause**: Vague or unclear prompts
**Solution**:
1. Add more descriptive details to prompt
2. Specify desired style and quality
3. Include technical specifications

### Generation Failures
**Cause**: Service issues or invalid requests
**Solution**:
1. Check OpenAI service status
2. Verify prompt format and content
3. Retry with modified prompt

### Quota Exceeded
**Cause**: Usage limits reached
**Solution**:
1. Check OpenAI usage dashboard
2. Monitor generation quotas
3. Consider upgrading account tier

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Get LLM Capabilities](pages/GetLLMCapabilities.md) - Verify image generation support
