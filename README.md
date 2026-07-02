# 1.3 Azure Chat App - Java Edition

A Spring Boot chat application that demonstrates building a generative AI chat app using **Azure OpenAI** with **flexible authentication** and a **clear learning roadmap** for implementing the Responses API.

This project implements the Azure Foundry chat exercise in Java, with a focus on understanding authentication methods and the progression from ChatCompletions API → Responses API → Streaming → Async.

---

## Features

✅ **7 Authentication Methods** (Factory Pattern)
- AzureKeyCredential (API Key)
- AzureCliCredential (`az login`)
- EnvironmentCredential (CI/CD)
- InteractiveBrowserCredential (Browser Login)
- DefaultAzureCredential (Tries all methods - RECOMMENDED)
- ManagedIdentityCredential (Azure resources)
- DeviceCodeCredential (Device code flow)

✅ **Detailed Learning Comments**
- Step 1: ChatCompletions API (Current)
- Step 2: Responses API (TODO with detailed implementation plan)
- Step 3: Streaming (TODO)
- Step 4: Async (TODO)

✅ **Production-Ready Setup**
- Azure OpenAI integration
- Entra ID authentication
- Environment configuration
- Security best practices (.env in .gitignore)

---

## Project Structure

```
claude-chat-app/
├── src/main/java/com/example/chat/
│   ├── ChatApplication.java        # Spring Boot entry point
│   ├── ChatService.java            # Azure OpenAI integration
│   │                               # (with detailed TODO comments for Responses API)
│   ├── ChatCLI.java                # Command-line interface
│   └── AuthenticationFactory.java   # 7 authentication methods (Factory Pattern)
├── pom.xml                          # Maven configuration (Azure SDK, Spring Boot)
├── .env                             # Your configuration (GITIGNORED)
├── .env.example                     # Template for .env
├── .gitignore                       # Protects secrets
└── README.md                        # This file
```

---

## Prerequisites

- **Java 20+** — `java -version`
- **Maven 3.9+** — `mvn -version`
- **Azure Resources:**
  - Azure subscription
  - Azure Foundry project
  - Deployed gpt-5.4 (or similar) model
  - Azure OpenAI endpoint

---

## Setup

### 1. Get Azure Credentials

**From Azure Foundry Portal:**
1. Navigate to your project Home
2. Copy **Azure OpenAI Endpoint** (e.g., `https://your-resource.openai.azure.com/`)
3. Note your **Model Deployment Name** (e.g., `gpt-5.4`)

### 2. Configure Environment

Create `.env` file in project root:

```bash
# Copy from template
cp .env.example .env
```

Edit `.env`:

```properties
# Azure Configuration
AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
MODEL_DEPLOYMENT=gpt-5.4
MAX_TOKENS=1024

# Authentication Method (choose one)
AZURE_AUTH_METHOD=default    # Recommended

# For 'key' method only:
AZURE_OPENAI_API_KEY=your-api-key-here

# For 'env' method only (CI/CD):
# AZURE_TENANT_ID=your-tenant-id
# AZURE_CLIENT_ID=your-client-id
# AZURE_CLIENT_SECRET=your-client-secret
```

**⚠️ NEVER commit `.env` to Git!** (It's in `.gitignore`)

### 3. Choose Authentication Method

Edit `AZURE_AUTH_METHOD` in `.env`:

| Method | Use Case | Setup |
|--------|----------|-------|
| `default` | Local dev + production | No extra setup (RECOMMENDED) |
| `cli` | Local dev with Azure CLI | Run `az login` first |
| `key` | Testing | Get API key from Azure Portal |
| `browser` | Interactive login | Opens browser to log in |
| `env` | CI/CD pipelines | Set environment variables |
| `managed` | Azure resources only | Enable Managed Identity |
| `device` | Device code flow | Displays code to enter |

### 4. Build & Run

```bash
cd "C:\Ai Projects\claude-chat-app"

# Build (downloads dependencies)
mvn clean install

# Run the app
mvn spring-boot:run
```

For authentication method `cli` or `default`, first authenticate:
```bash
az login
```

---

## Usage

Once the app starts:

```
✓ Azure OpenAI client initialized
  Endpoint: https://your-resource.openai.azure.com/
  Model: gpt-5.4
  Auth Method: default

=== Claude Chat App ===
Type your questions below. Type 'quit' to exit.

You: Tell me about the ELIZA chatbot.
Claude: The ELIZA chatbot, created by Joseph Weizenbaum in 1964-1966...

You: How does it compare to modern LLMs?
Claude: Modern LLMs like GPT-5 are fundamentally different from ELIZA...

You: quit
Goodbye!
```

---

## Learning Progression

### Step 1 (Current): ChatCompletions API
- Basic chat without conversation memory
- Each call is independent
- Simple request/response pattern
- **Location:** `ChatService.java:chat()` method

### Step 2 (TODO): Responses API
- Conversation tracking via response IDs
- Multi-turn conversations with memory
- Simplified syntax compared to Step 1
- **Location:** `ChatService.java:chatWithContext()` method
- **Details:** See inline comments with `STEP 2 TODO`

### Step 3 (TODO): Streaming
- Display response incrementally (word-by-word)
- Better UX for long responses
- Event-based response handling
- **Location:** `ChatService.java:streamChat()` method
- **Details:** See inline comments with `STEP 3 TODO`

### Step 4 (Optional): Async Operations
- Non-blocking calls
- Better responsiveness
- CompletableFuture or Project Reactor
- **Details:** See comments in `ChatService.java`

---

## Authentication Methods Explained

### 7 Methods Supported (via Factory Pattern)

**1. AzureKeyCredential** (Simplest)
```properties
AZURE_AUTH_METHOD=key
AZURE_OPENAI_API_KEY=your-api-key
```
- ✅ Works locally
- ❌ Not for production (key exposed)
- **Best for:** Development/testing

**2. AzureCliCredential** (Recommended for local dev)
```bash
az login
# Then in .env:
AZURE_AUTH_METHOD=cli
```
- ✅ Works locally
- ✅ Secure (uses system tokens)
- **Best for:** Local development

**3. EnvironmentCredential** (For CI/CD)
```bash
export AZURE_TENANT_ID=...
export AZURE_CLIENT_ID=...
export AZURE_CLIENT_SECRET=...
```
- ✅ Works in pipelines
- ✅ Secure for CI/CD
- **Best for:** GitHub Actions, Azure Pipelines

**4. InteractiveBrowserCredential** (User login)
```properties
AZURE_AUTH_METHOD=browser
```
- ✅ Opens browser to log in
- ✅ User-friendly
- **Best for:** User-facing applications

**5. DefaultAzureCredential** (Tries all)
```properties
AZURE_AUTH_METHOD=default
```
- ✅ Works everywhere
- ✅ Tries multiple methods automatically
- **Best for:** Both local dev and production (RECOMMENDED)

**6. ManagedIdentityCredential** (Azure resources only)
- ❌ Doesn't work locally
- ✅ Works in Azure (VMs, App Service, Functions)
- **Best for:** Deployed Azure applications

**7. DeviceCodeCredential** (Device flow)
- ⚠️ Awkward for normal dev
- ✅ For limited-input devices
- **Best for:** IoT, older phones

---

## Architecture: Factory Pattern

The `AuthenticationFactory.java` implements the **Factory Pattern** for authentication:

```
.env (AZURE_AUTH_METHOD=?)
        ↓
ChatService constructor
        ↓
AuthenticationFactory.createCredential()
        ↓
Returns appropriate credential object
        ↓
OpenAIClient uses credential
```

**Why Factory Pattern?**
- ✅ Switch auth methods by changing `.env`
- ✅ Test different methods without code changes
- ✅ Support production and local dev with same code
- ✅ Easy to add new auth methods

---

## Troubleshooting

### "Azure OpenAI client cannot authenticate"

**Solution:** You didn't authenticate. Try:

```bash
# For 'default' or 'cli' method:
az login

# For 'env' method:
export AZURE_TENANT_ID=your-tenant-id
export AZURE_CLIENT_ID=your-client-id
export AZURE_CLIENT_SECRET=your-client-secret

# For 'key' method:
# Set AZURE_OPENAI_API_KEY in .env
```

### "Unsupported parameter: 'max_tokens' is not supported"

**Solution:** Your model uses `max_completion_tokens` instead. Already fixed in current code.

### "Cannot resolve symbol 'ChatService'"

**Solution:** Run Maven to compile:
```bash
mvn clean compile
```

### "Port 8080 already in use"

**Solution:** Another app is using port 8080. Kill it or change the port in `application.properties`.

---

## Dependency Management

### Azure SDK Versions

The project carefully manages Azure SDK versions to avoid compatibility issues:

| Package | Version | Why |
|---------|---------|-----|
| azure-ai-openai | 1.0.0-beta.16 | Latest beta (no stable yet) |
| azure-identity | 1.13.1 | Latest stable |
| azure-json | 1.2.0 | Compatible with azure-core 1.55.3 |
| Spring Boot | 3.3.0 | Compatible with Azure SDK |

---

## Next Steps

1. **Understand authentication:** Switch `AZURE_AUTH_METHOD` and test each method
2. **Implement Step 2:** Follow `STEP 2 TODO` comments to add Responses API
3. **Implement Step 3:** Add streaming support (see `STEP 3 TODO`)
4. **Add REST API:** Expose chat as HTTP endpoint
5. **Persist history:** Add database for conversation history
6. **Deploy:** Push to Azure App Service or other cloud

---

## Resources

- [Azure OpenAI Documentation](https://learn.microsoft.com/en-us/azure/ai-services/openai/)
- [Azure Identity Library for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/identity-readme?view=azure-java-stable)
- [Azure OpenAI Java SDK](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/openai/azure-ai-openai)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

## License

MIT

---

## Notes for Learning

This project is designed as a **learning tool**. The detailed inline comments in `ChatService.java` show:
- ✅ What works now (Step 1)
- 📍 What needs to be added (Steps 2-4)
- 📚 How to implement it (detailed pseudocode in comments)

Use the TODO markers as a roadmap for learning and implementation.