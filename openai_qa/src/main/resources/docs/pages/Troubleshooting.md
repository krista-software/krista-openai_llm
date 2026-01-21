# Troubleshooting Guide

This comprehensive guide helps you resolve common issues with the OpenAI Extension. Each section provides clear symptoms, causes, and step-by-step solutions.

## Quick Diagnostic Checklist

Before diving into specific issues, run through this quick checklist:

1. ✅ **API Key**: Is your OpenAI API key configured and valid?
2. ✅ **Model Selection**: Have you selected a ChatGPT model?
3. ✅ **Internet Connection**: Can you access external websites?
4. ✅ **OpenAI Status**: Check [OpenAI Status Page](https://status.openai.com/)
5. ✅ **Account Billing**: Is your OpenAI account in good standing?

## Configuration Issues

### API Key Problems

#### "OpenAI API key is required to use this extension"
**Symptoms**: Extension fails to work, configuration validation errors

**Causes**:
- No API key configured
- Empty API key field
- API key not saved properly

**Solutions**:
1. **Configure API Key**:
   - Go to Extension Configuration
   - Enter your OpenAI API key in the "API Key" field
   - Click "Save" to apply changes

2. **Get a New API Key**:
   - Visit [OpenAI API Keys](https://platform.openai.com/api-keys)
   - Sign in to your OpenAI account
   - Click "Create new secret key"
   - Copy the key and paste it into the extension

3. **Verify API Key Format**:
   - API keys start with "sk-"
   - Should be 51 characters long
   - Contains letters, numbers, and special characters

#### "Unable to connect to OpenAI services"
**Symptoms**: Connection test fails, API calls don't work

**Causes**:
- Invalid or expired API key
- Network connectivity issues
- OpenAI service outage
- Firewall blocking requests

**Solutions**:
1. **Verify API Key**:
   - Test your API key at [OpenAI Playground](https://platform.openai.com/playground)
   - If it doesn't work there, regenerate your API key

2. **Check Network Connection**:
   - Ensure internet connectivity
   - Test accessing https://api.openai.com/ in your browser
   - Check if your firewall allows HTTPS requests

3. **OpenAI Service Status**:
   - Visit [OpenAI Status](https://status.openai.com/)
   - Check for ongoing incidents or maintenance

### Model Selection Issues

#### "Please select an OpenAI model to continue"
**Symptoms**: Requests fail, model validation errors

**Solutions**:
1. **Select a Model**:
   - Go to Extension Configuration
   - Choose from available models:
     - ChatGPT 3.5 (fastest, most cost-effective)
     - ChatGPT 4 (higher quality responses)
     - ChatGPT 4 Latest (newest features)
     - ChatGPT 4.1 Mini (balanced performance)

2. **Model Recommendations**:
   - **For simple queries**: ChatGPT 3.5 or ChatGPT 4.1 Mini
   - **For complex analysis**: ChatGPT 4 or ChatGPT 4 Latest
   - **For image processing**: ChatGPT 4 or ChatGPT 4 Latest

## Request Processing Issues

### "OpenAI services are currently experiencing high demand"
**Symptoms**: Requests timeout, slow responses, intermittent failures

**Causes**:
- High traffic on OpenAI servers
- Rate limiting
- Temporary service overload

**Solutions**:
1. **Wait and Retry**:
   - Wait 30-60 seconds before retrying
   - Try during off-peak hours
   - Break large requests into smaller ones

2. **Check Rate Limits**:
   - Review your OpenAI usage dashboard
   - Upgrade your OpenAI plan if needed
   - Implement request spacing in automated workflows

### "We encountered an issue processing your request"
**Symptoms**: Unexpected errors, null responses, processing failures

**Causes**:
- Malformed requests
- Content policy violations
- Internal processing errors

**Solutions**:
1. **Review Request Content**:
   - Ensure prompts don't violate OpenAI's usage policies
   - Remove potentially harmful or inappropriate content
   - Simplify complex requests

2. **Check Request Format**:
   - For Ask LLM: Ensure JSON is properly formatted
   - For images: Verify image format and size
   - For text: Check for special characters

## Image Processing Issues

### Image Generation Problems

#### "Image count must be between 1 and 10"
**Solution**: Adjust the number of images requested to be between 1 and 10.

#### "Invalid image generation model specified"
**Solution**: Use either 'dall-e-2' or 'dall-e-3' as the model parameter.

#### "Image quality must be either 'standard' or 'hd'"
**Solution**: Set quality parameter to either 'standard' or 'hd'.

#### "Image size must be one of: 256x256, 512x512, or 1024x1024"
**Solution**: Use one of the supported image dimensions.

### Image Analysis Problems

#### Base64 Encoding Issues
**Symptoms**: "Invalid base64 encoding" errors

**Solutions**:
1. **Verify Encoding**:
   - Ensure images are properly base64 encoded
   - Use standard base64 encoding (not URL-safe variants)
   - Remove data URL prefixes (data:image/jpeg;base64,)

2. **Check Image Format**:
   - Supported formats: JPEG, PNG, GIF, WebP
   - Maximum file size: 20MB
   - Recommended resolution: Under 2048x2048

## Authentication & Billing Issues

### Account-Related Problems

#### "Invalid API key" or "Expired API key"
**Solutions**:
1. **Regenerate API Key**:
   - Go to [OpenAI API Keys](https://platform.openai.com/api-keys)
   - Delete the old key
   - Create a new secret key
   - Update the extension configuration

#### "Insufficient permissions" or "Billing account issues"
**Solutions**:
1. **Check Account Status**:
   - Visit [OpenAI Account](https://platform.openai.com/account)
   - Verify billing information is current
   - Ensure you have available credits or a valid payment method

2. **Review Usage Limits**:
   - Check your usage dashboard
   - Upgrade your plan if you've hit limits
   - Set up usage alerts to prevent future issues

## Network & Connectivity Issues

### Firewall and Proxy Issues

**Symptoms**: Connection timeouts, network errors

**Solutions**:
1. **Whitelist OpenAI Domains**:
   - api.openai.com
   - platform.openai.com
   - *.openai.com

2. **Configure Proxy Settings**:
   - If using a corporate proxy, ensure it allows HTTPS traffic
   - Configure proxy authentication if required
   - Test direct connection if possible

### SSL/TLS Issues

**Symptoms**: Certificate errors, SSL handshake failures

**Solutions**:
1. **Update System Certificates**:
   - Ensure your system has current SSL certificates
   - Update Java if using an older version
   - Check system date/time accuracy

## Performance Optimization

### Improving Response Times

1. **Choose Appropriate Models**:
   - Use ChatGPT 3.5 for faster responses
   - Reserve ChatGPT 4 for complex tasks

2. **Optimize Prompts**:
   - Keep prompts concise and specific
   - Avoid unnecessary context
   - Use structured formats when possible

3. **Batch Processing**:
   - Group similar requests together
   - Avoid rapid sequential requests
   - Implement appropriate delays between calls

## Getting Additional Help

### When to Contact Support

Contact your system administrator or support team if:
- Issues persist after following troubleshooting steps
- You encounter new error messages not covered here
- Performance problems affect business operations
- You need help with advanced configuration

### Information to Provide

When reporting issues, include:
- Exact error messages
- Steps to reproduce the problem
- Extension version and configuration
- Timestamp of when the issue occurred
- Screenshots if applicable

### Useful Resources

- [OpenAI Documentation](https://platform.openai.com/docs)
- [OpenAI Status Page](https://status.openai.com/)
- [OpenAI Community Forum](https://community.openai.com/)
- [Extension Configuration Guide](pages/ExtensionConfiguration.md)
- [Authentication Setup](pages/Authentication.md)

## Error Code Reference

| Error Type | Common Causes | Quick Fix |
|------------|---------------|-----------|
| INPUT_ERROR | Missing parameters, invalid format | Check configuration and input format |
| LOGIC_ERROR | Business rule violations | Review request content and structure |
| AUTHENTICATION_ERROR | Invalid credentials | Verify API key and account status |
| SYSTEM_ERROR | Service unavailable | Check OpenAI status, retry later |
| UNAVAILABILITY_ERROR | Service outage | Wait for service restoration |

---

**Need immediate help?** Start with the [Quick Diagnostic Checklist](#quick-diagnostic-checklist) at the top of this page.
