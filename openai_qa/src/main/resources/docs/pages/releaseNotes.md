# Release Notes for Open AI LLM Extension

## Version 2.1.9
* Krista API Version: 1.0.118
* Developer: Ramgopal Patidar

### New Features
* **[KE-3011](https://antbrains.atlassian.net/browse/KE-3011)** - Added GPT-5.4 model family support
  - Added ChatGPT 5.4, ChatGPT 5.4 Mini, and ChatGPT 5.4 Nano to model selection
  - GPT-5.4: Frontier model with 1M context, 128K max output ($2.50/$15.00 per 1M tokens)
  - GPT-5.4 Mini: 400K context, 128K max output ($0.75/$4.50 per 1M tokens)
  - GPT-5.4 Nano: 400K context, 128K max output ($0.20/$1.25 per 1M tokens)

### Resolved Bugs
* **[KE-3011](https://antbrains.atlassian.net/browse/KE-3011)** - Removed `chatgpt-4o-latest` model which no longer exists on OpenAI, causing "The model does not exist or you do not have access to it" errors
* **[KE-3011](https://antbrains.atlassian.net/browse/KE-3011)** - Fixed `max_tokens` parameter error for GPT-4.1 and GPT-5.4 models. Newer models require `max_completion_tokens` instead of `max_tokens`; the extension now dynamically selects the correct parameter based on the model

### Documentation
* Updated extension configuration docs with all 8 supported models and pricing
* Updated troubleshooting guide with current model recommendations
* Updated model specs to match official OpenAI documentation (context windows, max output tokens)

---

## Version 2.1.8
* Krista API Version: 1.0.118
* Developer: Ramgopal Patidar

### New Features
* **[KE-3101](https://antbrains.atlassian.net/browse/KE-3101)** - Process Files: Single Request File Processing for OpenAI
  - New "Process Files" catalog request in the "File Analysis" area
  - Accepts any combination of images (JPG, PNG, GIF, WebP) and documents (PDF, DOCX, PPTX, XLSX) in a single request
  - Auto-detects file type and handles images inline (base64) and documents via OpenAI Files API upload
  - Supports up to 20 files and 50 MB total per request
  - Optional system instructions for guiding AI behavior
  - Uses OpenAI Responses API for unified file processing
  - No sessions required — stateless single request/response pattern

---

## Version 2.1.7
* Krista API Version: 1.0.118
* Developer: Ramgopal Patidar
* Global Catalog Version: GC-2026.02.02

## Bugs resolved
* **[KE-2772](https://antbrains.atlassian.net/browse/KE-2772)** -Open AI extension: Mask log file to hide sensitive info


## Version 2.1.6
* Krista API Version: 1.0.118
* Global Catalog Version: GC-2025.12.2

### New Features & Enhancements
* **[KE-2658](https://antbrains.atlassian.net/browse/KE-2658)** - OpenAI LLM Extension Revamp
  - Added comprehensive Architecture.md documentation with system design, performance analysis, and operational considerations
  - Created complete DOCX documentation file for LLM integration
  - Enhanced error messages with user-friendly, actionable guidance
  - Added detailed documentation for all catalog requests (Ask LLM, Ask a Question, Get AI Model Name, Get LLM Capabilities, Generate Image, Summarize Image, Answer Question from Image)
  - Improved extension configuration documentation with step-by-step guides
  - Added authentication and troubleshooting guides
  - Enhanced Javadoc comments for all public classes and methods
  - Added comprehensive unit test coverage (OpenAIExtensionTest, ChatGPTResponseTest, ExtensionUtilTest, OpenAIConstantsTest)
  - Improved code quality with better error handling and validation
  - Added visual documentation with screenshots for setup and configuration
  - Enhanced multimodal capabilities documentation

### Resolved Bugs
* Fixed minor issues in error message handling
* Improved connection test error reporting

### Known Issues

Not Available

---

## Version 2.1.6
* Krista API Version: 1.0.118
* Global Catalog Version: GC-2025.11.3

### Resolved Bugs
* **[KE-2037](https://antbrains.atlassian.net/browse/KE-2037)** - Fixed incorrect AI model name display when "ChatGPT 4 Latest" is selected
* **[KAE-585](https://antbrains.atlassian.net/browse/KAE-585)** - Add Start Discussion and Upload File to Discussion to ChatGPT and Gemini extensions

### Known Issues

Not Available

