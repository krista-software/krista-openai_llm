# OpenAI Extension - Catalog Requests Documentation

## Overview

The OpenAI Extension provides comprehensive document-based Q&A capabilities using OpenAI's advanced language models. This extension enables users to upload PDF documents, create discussion sessions, and ask intelligent questions about the content.

## Key Features

- 🔄 **Session Management**: Create and manage discussion sessions
- 📄 **Document Upload**: Support for PDF files up to 50MB
- 🤖 **AI-Powered Q&A**: Intelligent responses based on document content
- 📝 **Custom Instructions**: Guide AI behavior with specific instructions
- 🔒 **Secure Processing**: Enterprise-grade security and privacy

---

## Discussion Area Requests

### 🆕 **Create Discussion**

**Purpose**: Create a new discussion session for document-based Q&A. This generates a unique session ID for managing documents and conversations.

**Description**: Create a new discussion session for document-based Q&A. This generates a unique session ID that you'll use to upload documents, add instructions, and ask questions. Each session maintains its own context and conversation history.

**Input Parameters**: None

**Output Parameters**:

| **Name**   | **Type** | **Description** |
|------------|----------|-----------------|
| Session Id | Text     | Unique identifier for the new discussion session. Use this ID for all subsequent operations like uploading documents and asking questions. |
| Message    | Text     | Success confirmation message |

**Example Usage**:
```
Request: Create Discussion
Response:
- Session Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
- Message: "Discussion session created successfully. Use this Session ID to upload documents and ask questions."
```

**Error Handling**:
- System errors return detailed error messages
- All failures include guidance for resolution

---

### 📄 **Add Documents to Discussion**

**Purpose**: Upload PDF documents to an existing discussion session for AI analysis and question answering.

**Description**: Upload PDF documents to an existing discussion session. The AI will analyze these documents and use them to answer questions. Supports multiple file uploads with a maximum size of 50MB per file. Only PDF format is supported.

**Input Parameters**:

| **Name**   | **Type** | **Mandatory** | **Description** |
|------------|----------|---------------|-----------------|
| Files      | File     | Yes           | Select one or more PDF files to upload. Supported format: PDF only. Maximum file size: 50MB per file. Files will be analyzed by AI for question answering. |
| Session Id | Text     | Yes           | Enter the unique session ID returned from 'Create Discussion' request. This identifies which discussion to add documents to. |

**Output Parameters**:

| **Name**    | **Type** | **Description** |
|-------------|----------|-----------------|
| Successful  | Boolean  | Returns true if all documents were successfully uploaded and processed, false otherwise |
| Message     | Text     | Success message with upload details |
| Error       | Text     | Error message if upload fails (only present on failure) |

**File Validation**:
- ✅ **Format**: Only PDF files accepted
- ✅ **Size**: Maximum 50MB per file
- ✅ **Content**: Non-empty files only
- ✅ **Processing**: Real-time validation and error reporting

**Example Usage**:
```
Request: Add Documents to Discussion
- Files: [document1.pdf, document2.pdf]
- Session Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

Response:
- Successful: true
- Message: "Successfully uploaded 2 document(s) to the discussion."
```

**Error Examples**:
- "File 'document.txt' is not a PDF. Only PDF files are supported."
- "File 'large.pdf' exceeds maximum size of 50MB."
- "Session not found. Please create a discussion session first."

---

### 📝 **Add Instructions to the Discussion**

**Purpose**: Add custom instructions to guide the AI's behavior when answering questions about uploaded documents.

**Description**: Add custom instructions to guide the AI's behavior when answering questions about the uploaded documents. These instructions will be applied to all future questions in this discussion session. Examples: 'Summarize key points', 'Focus on technical details', 'Provide simple explanations'.

**Input Parameters**:

| **Name**     | **Type**  | **Mandatory** | **Description** |
|--------------|-----------|---------------|-----------------|
| Session Id   | Text      | Yes           | Enter the session ID from 'Create Discussion' request to add instructions to |
| Instructions | Paragraph | Yes           | Enter detailed instructions to guide the AI's responses. Examples: 'Summarize key points', 'Focus on technical details', 'Provide simple explanations'. Maximum 2000 characters. |

**Output Parameters**:

| **Name**    | **Type** | **Description** |
|-------------|----------|-----------------|
| Successful  | Boolean  | Returns true if instructions were successfully added to the discussion session |
| Message     | Text     | Confirmation message |
| Error       | Text     | Error message if operation fails (only present on failure) |

**Instruction Examples**:
- "Summarize key points in bullet format"
- "Focus on technical details and provide code examples"
- "Explain concepts in simple terms for non-technical users"
- "Highlight financial implications and cost analysis"

**Example Usage**:
```
Request: Add Instructions to the Discussion
- Session Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
- Instructions: "Summarize key points and provide actionable recommendations"

Response:
- Successful: true
- Message: "Instructions successfully added to the discussion session."
```

---

### 🗑️ **Delete Discussion**

**Purpose**: Permanently delete a discussion session and all associated data.

**Description**: Permanently delete a discussion session and all its associated data including uploaded documents, conversation history, and instructions. This action cannot be undone. Use this to clean up completed discussions or free up resources.

**Input Parameters**:

| **Name**   | **Type** | **Mandatory** | **Description** |
|------------|----------|---------------|-----------------|
| Session Id | Text     | Yes           | Enter the session ID of the discussion to delete. Warning: This will permanently remove all documents and conversation history. |

**Output Parameters**:

| **Name**    | **Type** | **Description** |
|-------------|----------|-----------------|
| Successful  | Boolean  | Returns true if the discussion session was successfully deleted, false otherwise |
| Message     | Text     | Confirmation message |
| Error       | Text     | Error message if deletion fails (only present on failure) |

**⚠️ Important Notes**:
- This action is **irreversible**
- All uploaded documents will be permanently removed
- Conversation history will be lost
- Session ID will become invalid

**Example Usage**:
```
Request: Delete Discussion
- Session Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

Response:
- Successful: true
- Message: "Discussion session and all associated data have been permanently deleted."
```

---

## Query Area Requests

### 🤖 **Ask LLM**

**Purpose**: Query the AI language model with a simple prompt for general-purpose questions and tasks.

**Description**: Queries a LLM with the given prompt. This is a general-purpose AI query that doesn't require document uploads or sessions. Use this for general knowledge questions, text generation, analysis, or any task that doesn't need specific document context.

**Input Parameters**:

| **Name** | **Type** | **Mandatory** | **Description** |
|----------|----------|---------------|-----------------|
| Prompt   | Text     | Yes           | Enter your question, request, or prompt for the AI. Examples: 'Explain quantum computing', 'Write a summary of...', 'What is the capital of France?' |

**Output Parameters**:

| **Name**     | **Type** | **Description** |
|--------------|----------|-----------------|
| LLM Response | Text     | AI-generated response to your prompt |

**Example Usage**:
```
Request: Ask LLM
- Prompt: "Explain the benefits of cloud computing in simple terms"

Response:
- LLM Response: "Cloud computing offers several key benefits: 1) Cost savings by eliminating hardware purchases, 2) Scalability to handle varying workloads, 3) Accessibility from anywhere with internet..."
```

**Use Cases**:
- General knowledge questions
- Text generation and writing assistance
- Code explanations and programming help
- Analysis and reasoning tasks
- Creative writing and brainstorming

---

### 🔍 **Get AI Model name**

**Purpose**: Returns the name of the AI model currently being used for queries.

**Description**: Returns the name of the AI model that is being queried. This helps you understand which OpenAI model is processing your requests and its capabilities.

**Input Parameters**: None

**Output Parameters**:

| **Name**  | **Type** | **Description** |
|-----------|----------|-----------------|
| AI Model  | Text     | Name of the current AI model (e.g., "gpt-4o", "gpt-4o-mini", "gpt-3.5-turbo") |

**Example Usage**:
```
Request: Get AI Model name
Response:
- AI Model: "gpt-4o"
```

**Model Information**:
- **gpt-4o**: Latest and most capable model
- **gpt-4o-mini**: Faster and more cost-effective
- **gpt-4-turbo**: High-performance variant
- **gpt-3.5-turbo**: Cost-effective option

---

### ❓ **Ask a question**

**Purpose**: Query OpenAI with a simple question and optional context without complex prompt engineering.

**Description**: Catalog Request for querying Open AI with a simple query and an optional context to the query without any prompt engineering. This provides a straightforward way to ask questions with additional context when needed.

**Input Parameters**:

| **Name** | **Type** | **Mandatory** | **Description** |
|----------|----------|---------------|-----------------|
| Query    | Text     | Yes           | Your main question or request |
| Context  | Text     | No            | Additional context or background information to help the AI provide better answers |

**Output Parameters**:

| **Name** | **Type** | **Description** |
|----------|----------|-----------------|
| Response | Text     | AI-generated response based on your query and context |

**Example Usage**:
```
Request: Ask a question
- Query: "How should I structure my presentation?"
- Context: "I'm presenting quarterly sales results to the executive team next week"

Response:
- Response: "For an executive presentation on quarterly sales results, structure it as follows: 1) Executive Summary (key metrics), 2) Performance vs. Targets, 3) Key Wins and Challenges..."
```

**Benefits of Adding Context**:
- More relevant and specific answers
- Better understanding of your situation
- Tailored recommendations
- Improved accuracy

---

### 🛠️ **Get LLM Capabilities**

**Purpose**: Gets the capabilities of the current generative AI provider to understand what tasks it can perform.

**Description**: Gets the capabilities of a given generative AI provider. This helps you understand what types of tasks and complexity levels the current AI model can handle effectively.

**Input Parameters**: None

**Output Parameters**:

| **Name**              | **Type** | **Description** |
|-----------------------|----------|-----------------|
| Multimodal support    | Boolean  | Whether the model can process multiple types of input (text, images, etc.) |
| Hard Task support     | Boolean  | Whether the model can handle complex, challenging tasks |
| Medium Task support   | Boolean  | Whether the model can handle moderately complex tasks |
| Easy Task support     | Boolean  | Whether the model can handle simple, straightforward tasks |

**Example Usage**:
```
Request: Get LLM Capabilities
Response:
- Multimodal support: true
- Hard Task support: true
- Medium Task support: true
- Easy Task support: true
```

**Capability Definitions**:
- **Easy Tasks**: Simple Q&A, basic text generation, straightforward analysis
- **Medium Tasks**: Complex reasoning, detailed explanations, structured analysis
- **Hard Tasks**: Advanced problem-solving, complex research, sophisticated analysis
- **Multimodal**: Processing text, images, documents, and other input types

---

### ❓ **Ask a question from discussion**

**Purpose**: Ask questions about uploaded documents in a discussion session and receive AI-powered answers.

**Description**: Ask questions about uploaded documents in a discussion session. The AI will analyze the document content and provide relevant answers based on the uploaded files and any instructions you've provided. Requires an active session with uploaded documents.

**Input Parameters**:

| **Name**              | **Type** | **Mandatory** | **Description** |
|-----------------------|----------|---------------|-----------------|
| Session Id            | Text     | Yes           | Enter the session ID from 'Create Discussion' request. This session must have documents uploaded to answer questions. |
| Query Or Json Schema  | Text     | Yes           | Enter your question about the uploaded documents. Be specific for better results. Examples: 'What are the key findings?', 'Summarize the main points', 'What does the document say about X?' |

**Output Parameters**:

| **Name** | **Type**  | **Description** |
|----------|-----------|-----------------|
| Response | Paragraph | AI-generated response based on the uploaded documents and your question. The response will reference specific content from your documents. |
| Error    | Text      | Error message if question processing fails (only present on failure) |

| **Name** | **Type**  |
|----------|-----------|
| Response | Paragraph |


### Name: **Answer Question From Base 64 Encoded Images**
Description : This request takes in a base64 encoded string from an images and answers a question on the basis of that

Input Parameters :

| **Name**               | **Type** | **Mandatory** |
|------------------------|----------|---------------|
| Base 64 Encoded Images | List     | true          |
| Question               | Text     | true          |
| Context                | Text     | false         |
| Temperature            | Number   | false         |
| Top K                  | Number   | false         |
| Top P                  | Number   | false         |
| Max Tokens             | Number   | false         |


Output Parameters :

| **Name** | **Type** |
|----------|----------|
| Answer   | Text     |


### Name: **Ask A Question Using Settings**

Description: This request takes in parameters like user prompt, system prompt, both being simple plain text strings along with temperature, top k and top p as well as max tokens. To be used by more technical users who know what they are doing.

Input Parameters:

| **Name**      | **Type** | **Mandatory** |
|---------------|----------|---------------|
| User Prompt   | Text     | true          |
| System Prompt | Text     | false         |
| Temperature   | Number   | false         |
| Top K         | Number   | false         |
| Top P         | Number   | false         |
| Max Tokens    | Number   | false         |

Output Parameters:

| **Name**     | **Type**  |
|--------------|-----------|
| LLM Response | Paragraph |
**Question Examples**:
- "What are the main conclusions of this research?"
- "Summarize the financial performance discussed in the documents"
- "What recommendations are provided for implementation?"
- "List the key risks mentioned in the analysis"
- "What are the technical specifications outlined?"

**Example Usage**:
```
Request: Ask a question from discussion
- Session Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
- Query: "What are the key findings from the research?"

Response:
- Response: "Based on the uploaded research documents, the key findings include: 1) Market growth of 15% year-over-year, 2) Customer satisfaction increased by 23%, 3) Cost reduction of $2.3M through process optimization..."
```

**Advanced Features**:
- 🧠 **Context Awareness**: AI maintains conversation context within the session
- 📊 **Document Analysis**: Deep understanding of document content and structure
- 🎯 **Instruction Following**: Responses follow any custom instructions provided
- 🔍 **Content Referencing**: Answers reference specific parts of uploaded documents

---

## Error Handling

All requests include comprehensive error handling with specific, actionable error messages:

### Common Error Types:

**Input Validation Errors**:
- Missing required fields
- Invalid session ID format
- File type/size violations
- Content length limits

**System Errors**:
- Network connectivity issues
- API rate limits
- Authentication failures
- Service unavailability

**Business Logic Errors**:
- Session not found
- No documents uploaded
- Invalid operations

### Error Response Format:
```json
{
  "Successful": false,
  "Error": "Specific error message with guidance for resolution"
}
```

---

## Best Practices

### 📋 **Session Management**:
1. Create a new session for each distinct document set
2. Upload all related documents before asking questions
3. Add instructions early to guide AI behavior
4. Delete sessions when no longer needed

### 📄 **Document Upload**:
1. Use high-quality PDF files for best results
2. Ensure documents are text-searchable (not scanned images)
3. Keep file sizes under 50MB for optimal performance
4. Upload related documents together in one session

### ❓ **Question Asking**:
1. Be specific and clear in your questions
2. Reference document sections when needed
3. Use follow-up questions to dive deeper
4. Leverage custom instructions for consistent responses

### 🔧 **Troubleshooting**:
1. Check session ID validity if requests fail
2. Verify document upload success before asking questions
3. Review error messages for specific guidance
4. Contact support for persistent issues

---

## Technical Specifications

### **File Handling**:
- **Supported File Format**: PDF only
- **Maximum File Size**: 512MB per file (OpenAI API limit)
- **File Validation**: Extension and size checking only

### **Input Limits**:
- **Maximum Instruction Length**: 10,000 characters (increased)
- **Maximum Question Length**: 10,000 characters (increased)
- **Maximum Prompt Length**: No specific limit (reasonable use)
- **Session ID Format**: UUID (36 characters)

### **Performance**:
- **API Response Time**: Typically 5-25 seconds (optimized for 30s UI limit)
- **Connect Timeout**: 5 seconds
- **Read Timeout**: 20 seconds
- **File Processing Delay**: 1 second

### **Supported Models**:
- **gpt-4o** (default): Latest and most capable
- **gpt-4o-mini**: Faster and cost-effective
- **gpt-4-turbo**: High-performance variant
- **gpt-3.5-turbo**: Cost-effective option
- **16+ model variants**: Automatic validation and fallback

### **Capabilities**:
- **Multimodal Support**: Yes (text, documents)
- **Task Complexity**: Easy, Medium, and Hard tasks supported
- **Context Awareness**: Maintains conversation history
- **Instruction Following**: Custom behavior guidance

---

*For additional support or questions, please refer to the setup documentation or contact your system administrator.*