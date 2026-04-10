# Extension Configuration

## Overview

The OpenAI Extension requires configuration of your API key and model selection to connect with OpenAI's services. This page provides step-by-step instructions for configuring the extension in your Krista workspace.

## Configuration Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| API Key | Text (Secured) | Yes | Your OpenAI API key for authentication | "sk-proj-abc123..." |
| Model Name | Pick One | Yes | The ChatGPT model to use for requests | "ChatGPT 4" |

### Available Models

| Model Option | Description | Use Case |
|--------------|-------------|----------|
| ChatGPT 3.5 | Fast, cost-effective model (Legacy) | General queries, simple tasks |
| ChatGPT 4 | Advanced reasoning capabilities | Complex analysis, detailed responses |
| ChatGPT 4.1 Nano | Fastest GPT-4.1, ultra low-cost | Classification, data extraction, simple tasks |
| ChatGPT 4.1 Mini | Balanced GPT-4.1, low-cost | Quick responses, efficient processing |
| ChatGPT 4.1 | Full GPT-4.1, 1M context | Complex analysis, long documents |
| ChatGPT 5.4 | Frontier model, 1M context | Complex professional work, advanced reasoning |
| ChatGPT 5.4 Mini | Capable small model, 400K context | Coding, computer use, subagents |
| ChatGPT 5.4 Nano | Cheapest GPT-5.4-class model | High-volume tasks, classification, ranking |

## Step-by-Step Setup

### Step 1: Access the Extension Catalog

1. Navigate to **Catalog → Krista → LLMs → Extensions → OpenAI**
2. Click on the OpenAI Extension

![Extension Catalog](../_media/ExtensionConfiguration_catalog_openAI.png)

### Step 2: Add Extension to Workspace

1. Click **Add to Workspace**
2. The extension configuration page will open
3. You'll see two required fields: **API Key** and **Model Name**

![Setup Page](../_media/ExtensionConfiguration_setup_page.png)

### Step 3: Configure API Key

1. Enter your OpenAI API key in the **API Key** field
2. The API key should start with "sk-" and be kept secure
3. Refer to [Creating OpenAI App](pages/CreatingOpenAIApp.md) if you need to generate an API key

> **🔒 Security Note**: The API Key field is secured and encrypted in Krista's storage.

### Step 4: Select Model

1. Choose your preferred model from the **Model Name** dropdown:
   - **ChatGPT 3.5**: Legacy model, general use
   - **ChatGPT 4**: GPT-4o, advanced reasoning
   - **ChatGPT 4.1 Nano**: Ultra low-cost, fastest GPT-4.1
   - **ChatGPT 4.1 Mini**: Balanced performance and cost
   - **ChatGPT 4.1**: Full GPT-4.1 with 1M context
   - **ChatGPT 5.4**: Frontier model for complex professional work
   - **ChatGPT 5.4 Mini**: Best small model for coding and subagents
   - **ChatGPT 5.4 Nano**: Cheapest, fastest for high-volume tasks

### Step 5: Validate Configuration

1. Click the **Validate Attributes** button
2. The system will test your API key and model configuration
3. A confirmation dialog will appear

![Validation Dialog](../_media/ExtensionConfiguration_openai_krista_3.png)

4. Click **Yes** to proceed with validation
5. Upon successful validation, you'll see a success message

![Validation Success](../_media/ExtensionConfiguration_validate.png)

## Authentication Type Selection

The OpenAI Extension uses **API Key Authentication**:

- **Type**: Bearer Token Authentication
- **Method**: API key passed in Authorization header
- **Security**: Keys are encrypted and stored securely
- **Scope**: Full access to your OpenAI account capabilities

## Security Considerations

### Best Practices

1. **API Key Security**:
   - Never share your API key publicly
   - Rotate keys regularly for enhanced security
   - Monitor usage in your OpenAI dashboard

2. **Access Control**:
   - Limit API key permissions in OpenAI dashboard
   - Set usage limits to prevent unexpected charges
   - Monitor API usage patterns

3. **Environment Management**:
   - Use different API keys for development and production
   - Implement proper key rotation policies
   - Regular security audits

### Rate Limiting

OpenAI implements rate limiting on API requests:
- Limits vary by model and subscription tier
- The extension handles rate limiting gracefully
- Monitor your usage in the OpenAI dashboard

## Troubleshooting

### Common Configuration Issues

#### API Key Missing Error
**Error**: "API Key is missing!"
**Cause**: No API key provided or invalid format
**Resolution**: 
1. Verify API key is entered correctly
2. Ensure key starts with "sk-"
3. Check for extra spaces or characters

#### Model Not Selected Error
**Error**: "Model is not selected!"
**Cause**: No model chosen from dropdown
**Resolution**: Select a model from the available options

#### Connection Test Failed
**Error**: "Error occurred while testing connection"
**Cause**: Invalid API key or network issues
**Resolution**:
1. Verify API key is valid and active
2. Check OpenAI service status
3. Ensure network connectivity
4. Verify billing account is set up (for advanced models)

#### Authentication Failed
**Error**: Connection timeout or 401 errors
**Cause**: Invalid credentials or expired key
**Resolution**:
1. Generate a new API key in OpenAI dashboard
2. Update the configuration with new key
3. Re-validate the configuration

### Validation Process

The extension performs the following validations:

1. **API Key Format**: Checks if key follows OpenAI format
2. **Model Selection**: Ensures a valid model is selected
3. **Connection Test**: Makes a test API call to verify connectivity
4. **Authentication**: Validates API key permissions

## Next Steps

After successful configuration:

1. **Test Basic Functionality**: Try the [Ask a Question](pages/AskAQuestion.md) catalog request
2. **Explore Capabilities**: Check [Get LLM Capabilities](pages/GetLLMCapabilities.md)
3. **Review Authentication**: Understand [Authentication](pages/Authentication.md) details
4. **Start Building**: Integrate AI capabilities into your Krista workflows

## See Also

- [Authentication](pages/Authentication.md) - Detailed authentication information
- [Creating OpenAI App](pages/CreatingOpenAIApp.md) - Setting up your OpenAI account
- [Ask a Question](pages/AskAQuestion.md) - Test your configuration with a simple query
- [Troubleshooting Guide](pages/Troubleshooting.md) - Resolve configuration and connection issues
