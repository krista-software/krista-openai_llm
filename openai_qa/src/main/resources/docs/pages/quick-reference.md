# OpenAI Extension - Quick Reference Guide

## 🚀 Getting Started

### Step 1: Create Discussion Session
```
Request: Create Discussion
Output: Session ID (save this!)
```

### Step 2: Upload Documents
```
Request: Add Documents to Discussion
Input: Files (PDF only, max 512MB each) + Session ID
```

### Step 3: Add Instructions (Optional)
```
Request: Add Instructions to the Discussion
Input: Session ID + Instructions (max 10000 chars)
```

### Step 4: Ask Questions
```
Request: Ask a question from discussion
Input: Session ID + Your Question
Output: AI Response based on documents
```

### Step 5: Clean Up
```
Request: Delete Discussion
Input: Session ID
```

---

## 📋 Request Summary

| **Request Name** | **Purpose** | **Required Inputs** | **Key Outputs** |
|------------------|-------------|---------------------|-----------------|
| **Create Discussion** | Start new session | None | Session ID |
| **Add Documents** | Upload PDFs | Files + Session ID | Success status |
| **Add Instructions** | Guide AI behavior | Session ID + Instructions | Success status |
| **Ask Question** | Get AI answers | Session ID + Question | AI Response |
| **Delete Discussion** | Clean up session | Session ID | Success status |

---

## ⚡ Quick Tips

### File Requirements:
- ✅ **Format**: PDF only
- ✅ **Size**: Max 512MB per file
- ✅ **Content**: Text-searchable preferred

### Session Management:
- 💡 **One session** = One document set
- 💡 **Save Session ID** - you'll need it for everything
- 💡 **Delete when done** - keeps things clean

### Better Questions:
- 🎯 **Be specific**: "What are the key findings?" vs "Tell me about this"
- 🎯 **Reference sections**: "What does chapter 3 say about..."
- 🎯 **Use instructions**: Guide AI style and focus

### Error Prevention:
- ✅ **Check Session ID format**: Should be UUID format
- ✅ **Verify file upload**: Wait for success confirmation
- ✅ **Read error messages**: They provide specific guidance

---

## 🔧 Common Error Solutions

| **Error** | **Solution** |
|-----------|--------------|
| "Session not found" | Check Session ID, create new session if needed |
| "File not PDF" | Convert to PDF format before upload |
| "File too large" | Reduce file size to under 50MB |
| "No documents uploaded" | Upload documents before asking questions |
| "Invalid Session ID format" | Use Session ID from Create Discussion response |

---

## 📞 Support

For detailed documentation, see the full [Catalog Requests](catalog.md) guide.

For setup instructions, see [OpenAI Setup](OpenAI_Setup.md).
