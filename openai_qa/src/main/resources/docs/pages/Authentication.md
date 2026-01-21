# Authentication

## Overview

The OpenAI Extension uses API key-based authentication to securely connect with OpenAI's services. This page explains the authentication mechanism, security considerations, and best practices for managing your OpenAI API credentials.

## Authentication Mode

### API Key Authentication

The OpenAI Extension uses **Bearer Token Authentication** with your OpenAI API key:

- **Authentication Type**: API Key (Bearer Token)
- **Header**: `Authorization: Bearer sk-your-api-key`
- **Security**: HTTPS-only communication
- **Storage**: Encrypted in Krista's secure storage

## API Key Requirements

### Key Format
- **Prefix**: All OpenAI API keys start with `sk-`
- **Structure**: `sk-proj-` followed by a long alphanumeric string
- **Example**: `sk-proj-abc123def456ghi789...`

### Key Types
OpenAI provides different types of API keys:

| Key Type | Description | Use Case |
|----------|-------------|----------|
| Project Keys | Scoped to specific projects | Recommended for production |
| User Keys | Personal API keys | Development and testing |
| Service Account Keys | For automated systems | Enterprise deployments |

## Authentication Flow

### 1. API Key Validation
When you configure the extension:

```
1. Extension receives API key
2. Key format validation (starts with 'sk-')
3. Test API call to OpenAI
4. Authentication success/failure response
```

### 2. Request Authentication
For each catalog request:

```
1. Extension retrieves stored API key
2. Adds Authorization header to request
3. Makes HTTPS call to OpenAI API
4. Processes authenticated response
```

### 3. Error Handling
Authentication errors are handled gracefully:

- **401 Unauthorized**: Invalid or expired API key
- **403 Forbidden**: Insufficient permissions
- **429 Too Many Requests**: Rate limit exceeded

## Security Best Practices

### 1. API Key Management
- **Never expose keys**: Don't include keys in code or logs
- **Rotate regularly**: Change keys periodically for security
- **Monitor usage**: Track API calls in OpenAI dashboard
- **Set limits**: Configure usage limits to prevent overuse

### 2. Access Control
- **Principle of least privilege**: Grant minimum necessary permissions
- **Environment separation**: Use different keys for dev/prod
- **Team access**: Manage team member access appropriately

### 3. Storage Security
- **Encrypted storage**: Krista encrypts API keys at rest
- **Secure transmission**: All communication uses HTTPS
- **No logging**: API keys are never logged in plain text

## Rate Limiting and Quotas

### OpenAI Rate Limits
OpenAI implements rate limiting based on:

| Factor | Description |
|--------|-------------|
| Requests per minute (RPM) | Number of API calls |
| Tokens per minute (TPM) | Total tokens processed |
| Tokens per day (TPD) | Daily token allowance |

### Rate Limit Handling
The extension handles rate limits by:
- **Automatic retries**: Built-in retry mechanism with backoff
- **Error responses**: Clear error messages when limits exceeded
- **Graceful degradation**: Informative responses during rate limiting

### Subscription Tiers
Rate limits vary by OpenAI subscription:

| Tier | RPM | TPM | Features |
|------|-----|-----|----------|
| Free | 3 | 200,000 | Basic access |
| Pay-as-you-go | 3,500 | 90,000 | Standard usage |
| Tier 1 | 5,000 | 300,000 | Higher limits |
| Tier 2+ | Higher | Higher | Enterprise features |

## Token Management

### Token Calculation
OpenAI charges based on token usage:
- **Input tokens**: Tokens in your prompt
- **Output tokens**: Tokens in the response
- **Approximate ratio**: ~4 characters = 1 token

### Token Optimization
The extension optimizes token usage by:
- **Efficient prompts**: Structured message formatting
- **Response limits**: Configurable max token responses
- **Model selection**: Different models have different costs

## Error Scenarios and Handling

### Authentication Errors

#### Invalid API Key
**Error**: `401 Unauthorized`
**Cause**: Incorrect or malformed API key
**Resolution**:
1. Verify API key format (starts with 'sk-')
2. Check for typos or extra characters
3. Generate new key if necessary

#### Expired API Key
**Error**: `401 Unauthorized`
**Cause**: API key has been revoked or expired
**Resolution**:
1. Generate new API key in OpenAI dashboard
2. Update extension configuration
3. Re-validate configuration

#### Insufficient Permissions
**Error**: `403 Forbidden`
**Cause**: API key lacks required permissions
**Resolution**:
1. Check API key permissions in OpenAI dashboard
2. Ensure key has access to required models
3. Verify billing account is active

### Rate Limiting Errors

#### Rate Limit Exceeded
**Error**: `429 Too Many Requests`
**Cause**: Exceeded API rate limits
**Resolution**:
1. Wait for rate limit reset
2. Implement request spacing
3. Consider upgrading subscription tier

#### Quota Exceeded
**Error**: `429 Quota Exceeded`
**Cause**: Monthly usage quota reached
**Resolution**:
1. Check usage in OpenAI dashboard
2. Increase quota limits
3. Monitor usage patterns

## Troubleshooting Authentication

### Common Issues

#### Connection Test Fails
**Symptoms**: Validation fails during setup
**Checks**:
1. Verify internet connectivity
2. Check OpenAI service status
3. Confirm API key is active
4. Ensure billing account is set up

#### Intermittent Authentication Failures
**Symptoms**: Requests sometimes fail
**Checks**:
1. Monitor rate limiting
2. Check for API key rotation
3. Verify network stability
4. Review OpenAI status page

#### Model Access Denied
**Symptoms**: Specific models return 403 errors
**Checks**:
1. Verify model availability in your tier
2. Check if model requires special access
3. Confirm billing account supports model
4. Review OpenAI model documentation

### Diagnostic Steps

1. **Test API Key**: Use OpenAI's API documentation to test key directly
2. **Check Logs**: Review Krista logs for detailed error messages
3. **Verify Configuration**: Ensure all required fields are populated
4. **Monitor Usage**: Check OpenAI dashboard for usage patterns

## Best Practices Summary

### Security
- ✅ Use project-scoped API keys
- ✅ Rotate keys regularly
- ✅ Monitor usage and set alerts
- ✅ Never expose keys in code or logs

### Performance
- ✅ Implement proper error handling
- ✅ Monitor rate limits
- ✅ Optimize token usage
- ✅ Use appropriate models for tasks

### Maintenance
- ✅ Regular security audits
- ✅ Update keys before expiration
- ✅ Monitor OpenAI service updates
- ✅ Review usage patterns monthly

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure your API key
- [Creating OpenAI App](pages/CreatingOpenAIApp.md) - Generate API keys
- [Ask LLM](pages/AskLLM.md) - Test authentication with API calls
