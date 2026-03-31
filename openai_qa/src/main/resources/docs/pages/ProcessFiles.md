# Process Files

## Overview

Analyze images and documents together in a single request. Upload any combination of supported files with a prompt and receive an AI-generated text response. No sessions required — this is a stateless single request/response pattern.

Documents are uploaded to OpenAI's Files API and sent inline via `input_file` blocks in the Responses API. Images are sent inline as base64. The model sees all file content directly in its context window. If files exceed the model's context limit, an actionable error guides the user to switch to a larger model (e.g., gpt-4.1 with 1M tokens).

## Request Details

- **Area**: File Analysis
- **Type**: QUERY_SYSTEM
- **Class**: FileAnalysisArea
- **Package**: app.krista.extensions.krista.llms.openai_qa.catalog

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Files | File (multiple) | Yes | One or more files to analyze. Supports images and documents. Max 20 files, 50 MB total. | report.pdf, screenshot.png |
| Prompt | Text | Yes | The question or instruction for the AI about the uploaded files. | "Summarize the key findings from all documents" |
| Instructions | Paragraph | No | Optional system-level guidance for the AI's behavior. | "Respond in bullet points" |

### Parameter Details

#### Files
- **Purpose**: The files to be analyzed by the AI
- **Multiple Upload**: Yes — upload 1 to 20 files at once
- **Max Total Size**: 50 MB across all files
- **Supported Image Formats**: JPG, JPEG, PNG, GIF, WebP
- **Supported Document Formats**: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX

#### Prompt
- **Purpose**: Tells the AI what to do with the uploaded files
- **Format**: Plain text
- **Examples**: "Summarize this document", "Compare the two reports", "Extract all dates and amounts"

#### Instructions (Optional)
- **Purpose**: System-level guidance that shapes the AI's behavior
- **Format**: Paragraph text
- **Examples**: "Respond in bullet points", "Focus on financial data", "Use formal language"

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Response | Paragraph | AI-generated text response based on the provided files and prompt |

## How It Works

### Image Processing
Images (JPG, PNG, GIF, WebP) are sent inline as base64-encoded data directly to the OpenAI Responses API. The AI can see and analyze the visual content.

### Document Processing
Documents (PDF, DOCX, PPTX, XLSX, etc.) are processed as follows:

1. **Upload**: Each document is uploaded to OpenAI's Files API
2. **Inline Reference**: Documents are included as `input_file` blocks in the Responses API payload
3. **Full Context**: The model sees the entire document content in its context window
4. **Truncation**: Automatic truncation is enabled to handle edge cases gracefully

### Mixed Files (Images + Documents)
When you upload both images and documents:
- Images are sent inline as base64 (the AI can see them directly)
- Documents are sent as input_file references (the AI reads them in full)
- The AI receives all content in a single response

### Model Context Limits
If the combined file content exceeds the selected model's context window, the API returns a clear error message recommending a model with a larger context (e.g., gpt-4.1 supports 1M tokens).

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| API Key Missing | "API Key is missing!" | Configure API key in extension settings |
| Model Not Selected | "Model is not selected!" | Select a model in extension configuration |
| No Files Provided | "At least one file is required" | Upload at least one file |
| Empty Prompt | "Prompt is required" | Provide a non-empty prompt |
| Too Many Files | "Too many files: N. Maximum allowed is 20" | Reduce the number of files to 20 or fewer |
| Total Size Exceeded | "Total file size exceeds 50 MB limit" | Reduce total file size below 50 MB |
| Unsupported File Type | "Unsupported file type: 'file.ext'" | Use a supported file format |

## Error Handling

For comprehensive troubleshooting steps and solutions, see the [Troubleshooting Guide](pages/Troubleshooting.md).

### Input Errors
**Cause**: Invalid or missing parameters
**Common Scenarios**:
- Missing API key or model configuration
- No files uploaded or empty prompt
- Unsupported file format (e.g., .mp3, .zip)
- More than 20 files or total size over 50 MB

**Resolution**:
1. Verify extension configuration is complete
2. Ensure at least one supported file is uploaded
3. Provide a non-empty prompt

### File Upload Errors
**Cause**: OpenAI Files API rejection
**Common Scenarios**:
- File too large for OpenAI's upload limit
- Corrupted or unreadable file content
- OpenAI service temporarily unavailable

**Resolution**:
1. Verify the file opens correctly on your local machine
2. Try a smaller file to isolate the issue
3. Retry after a brief delay

### Context Length Errors
**Cause**: Combined content exceeds model's context window
**Common Scenarios**:
- Very large documents on a model with a smaller context window (e.g., gpt-4o at 128K tokens)
- Many documents uploaded simultaneously

**Resolution**:
1. Use a model with a larger context window (e.g., gpt-4.1 supports 1M tokens)
2. Reduce the number or size of files
3. Split the request into smaller batches

### Authorization Errors
**Cause**: Authentication or permission issues
**Common Scenarios**:
- Invalid or expired API key
- Insufficient account permissions
- Billing account issues

**Resolution**:
1. Verify API key is valid and active
2. Check OpenAI account status and billing
3. Regenerate API key if necessary

## Usage Examples

### Example 1: Single PDF Analysis
**Input**:
```
Files: [quarterly-report.pdf]
Prompt: "Summarize the key financial metrics from this report"
Instructions: (empty)
```

**Output**:
```
Response: "Based on the quarterly report, the key financial metrics are:
- Revenue: $12.3M (up 15% YoY)
- Operating margin: 23.4%
- Net income: $2.8M
- Customer acquisition cost: $45 per customer
The report highlights strong growth in the enterprise segment..."
```

### Example 2: Multiple Document Comparison
**Input**:
```
Files: [proposal-v1.pdf, proposal-v2.pdf]
Prompt: "What changed between version 1 and version 2?"
Instructions: "Present the differences in a structured table format"
```

**Output**:
```
Response: "Here are the key differences between the two proposal versions:

| Section | Version 1 | Version 2 |
|---------|-----------|-----------|
| Budget | $500,000 | $650,000 |
| Timeline | 6 months | 8 months |
| Team Size | 5 engineers | 7 engineers |
| Scope | Core features only | Core + analytics module |
..."
```

### Example 3: Image Analysis
**Input**:
```
Files: [dashboard-screenshot.png]
Prompt: "What metrics are shown on this dashboard?"
Instructions: (empty)
```

**Output**:
```
Response: "The dashboard displays the following metrics:
1. Total Users: 12,453 (up 8% from last month)
2. Active Sessions: 3,201
3. Revenue Chart: showing monthly trend from Jan-Dec
4. Conversion Rate: 4.2%
..."
```

### Example 4: Mixed Image + Document
**Input**:
```
Files: [architecture-diagram.png, technical-spec.pdf]
Prompt: "Does the architecture diagram match what's described in the technical specification?"
Instructions: "Highlight any discrepancies"
```

**Output**:
```
Response: "Comparing the architecture diagram with the technical specification, I found the following:

Matches:
- The three-tier architecture (frontend, API, database) matches the spec
- Load balancer placement is consistent

Discrepancies:
- The diagram shows a Redis cache layer that is not mentioned in the specification
- The spec describes a message queue (RabbitMQ) that does not appear in the diagram
..."
```

## Business Rules

1. **Stateless Processing**: Each request is independent — no sessions required
2. **Auto File Type Detection**: Files are automatically classified as image or document based on extension
3. **Inline Processing**: All files are sent directly to the model — documents as input_file, images as base64
4. **Model Selection**: Uses the model configured in extension settings (gpt-4o default)
5. **Truncation**: Automatic truncation is enabled to handle edge cases gracefully
6. **Context-Aware Errors**: If files exceed the model's context limit, a clear error recommends switching to gpt-4.1

## Limitations

1. **File Count**: Maximum 20 files per request
2. **Total Size**: Maximum 50 MB across all files combined
3. **Supported Formats Only**: Images (JPG, PNG, GIF, WebP) and documents (PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX)
4. **Context Window**: Very large documents may exceed smaller models' context limits — use gpt-4.1 for 1M token support
5. **Text-Based Documents**: Scanned image-only PDFs without OCR may not be readable
6. **Rate Limits**: Subject to OpenAI's API rate limiting
7. **Response Length**: Limited by model's maximum output token setting

## Best Practices

### 1. Choose the Right Model
- Use **gpt-4.1** for large documents (1M token context window)
- Use **gpt-4o** for general-purpose image + document analysis
- Avoid gpt-3.5-turbo for large file processing

### 2. Write Effective Prompts
- Be specific about what you want extracted or analyzed
- For multiple files, reference them by name (e.g., "Compare report.pdf with invoice.pdf")
- Ask focused questions for best results

### 3. Use Instructions Wisely
- Set output format: "Respond in JSON", "Use bullet points", "Create a table"
- Set context: "You are a financial analyst", "Focus on legal terms"
- Set constraints: "Keep response under 500 words", "Only include data from the documents"

### 4. Optimize File Selection
- Upload only relevant files — fewer files means faster processing
- For very large documents, consider splitting into focused sections
- Prefer PDF for documents — it has the best compatibility

### 5. Handle Errors Gracefully
- Check the Response field — errors are returned as FAILURE with a descriptive message
- If context length is exceeded, switch to gpt-4.1
- If a specific file format fails, try converting to PDF

## Technical Implementation

### Architecture
```
FileAnalysisArea (Catalog Entry Point)
  |
  v
FileAnalysisService (Orchestrator)
  |-- FileTypeClassifier (IMAGE / DOCUMENT / UNSUPPORTED)
  |-- FileUploadService (OpenAI Files API upload)
  |-- HttpRequestService (Send to Responses API, parse response)
  |     |-- ResponseParserService (Extract text from response)
```

### Processing Flow
1. **Input Validation**: Verify files, prompt, and file count/size limits
2. **File Classification**: Each file classified as IMAGE or DOCUMENT
3. **Image Processing**: Encode as base64 data URL, create input_image block
4. **Document Processing**: Upload to OpenAI Files API, create input_file block
5. **Payload Construction**: Build Responses API payload with all content blocks inline
6. **API Call**: Send to OpenAI Responses API with truncation: auto
7. **Response Parsing**: Extract text from output items
8. **Error Handling**: context_length_exceeded returns actionable model recommendation

### Key Services

| Service | Responsibility |
|---------|---------------|
| FileAnalysisService | Orchestrates the full pipeline — classifies files, creates content blocks, builds payload |
| FileUploadService | Uploads files to OpenAI's Files API with MIME type detection |
| FileTypeClassifier | Classifies files by extension into IMAGE, DOCUMENT, or UNSUPPORTED |
| HttpRequestService | Sends HTTP requests to OpenAI APIs with auth headers and error handling |
| ResponseParserService | Parses Responses API output, extracts text content |

### Configuration Constants

| Constant | Value | Description |
|----------|-------|-------------|
| MAX_FILES_PER_REQUEST | 20 | Maximum files per single request |
| MAX_TOTAL_FILE_SIZE_BYTES | 50 MB | Total size limit across all files |

## Related Catalog Requests

- [Ask LLM](pages/AskLLM.md) - General-purpose AI queries without files
- [Ask a Question](pages/AskAQuestion.md) - Query with optional context
- [Answer Question from Image](pages/AnswerQuestionFromImage.md) - Single image analysis
- [Answer Question from Base64 Image](pages/AnswerQuestionFromBase64EncodedImage.md) - Base64 image analysis
- [Summarize Image](pages/SummarizeImage.md) - Image summarization

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure API key and model
- [Authentication](pages/Authentication.md) - OpenAI authentication details
- [Troubleshooting Guide](pages/Troubleshooting.md) - Common issues and solutions
- [Release Notes](pages/releaseNotes.md) - Version history
