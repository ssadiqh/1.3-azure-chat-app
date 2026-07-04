package com.example.chat;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.TokenCredential;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing chat interactions with Azure OpenAI.
 *
 * IMPLEMENTATION STAGES:
 * =====================
 * Step 1 (CURRENT): ChatCompletions API - Basic chat without conversation memory
 * Step 2 (NEXT):    Responses API - Chat with conversation tracking via response IDs
 * Step 3 (LATER):   Streaming - Incremental response display
 * Step 4 (OPTIONAL): Async - Non-blocking operations
 */
@Service
public class ChatService {
    private final OpenAIClient openAIClient;
    private final String modelDeployment;
    private final int maxTokens;

    // ============================================================================
    // STEP 2 TODO: Add conversation tracking
    // private String lastResponseId;  // Track previous response for multi-turn conversations
    // ============================================================================

    public ChatService() {
        String projectDir = System.getProperty("user.dir");
        if (!projectDir.endsWith("1.3-azure-chat-app")) {
            projectDir = projectDir + "/1.3-azure-chat-app";
        }
        Dotenv dotenv = Dotenv.configure().directory(projectDir).load();
        String endpoint = dotenv.get("AZURE_OPENAI_ENDPOINT");
        this.modelDeployment = dotenv.get("MODEL_DEPLOYMENT");
        this.maxTokens = Integer.parseInt(dotenv.get("MAX_TOKENS", "1024"));
        String authMethod = dotenv.get("AZURE_AUTH_METHOD", "default");
        String apiKey = dotenv.get("AZURE_OPENAI_API_KEY", "");

        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalArgumentException("AZURE_OPENAI_ENDPOINT not found in .env file");
        }
        if (modelDeployment == null || modelDeployment.isEmpty()) {
            throw new IllegalArgumentException("MODEL_DEPLOYMENT not found in .env file");
        }

        // Create credential using factory (supports 7 authentication methods)
        Object credential = AuthenticationFactory.createCredential(authMethod, apiKey);

        // Initialize OpenAI client with the appropriate credential
        OpenAIClientBuilder builder = new OpenAIClientBuilder().endpoint(endpoint);

        if (credential instanceof AzureKeyCredential) {
            builder.credential((AzureKeyCredential) credential);
        } else if (credential instanceof TokenCredential) {
            builder.credential((TokenCredential) credential);
        } else {
            throw new IllegalArgumentException("Invalid credential type: " + credential.getClass());
        }

        this.openAIClient = builder.buildClient();

        System.out.println("✓ Azure OpenAI client initialized");
        System.out.println("  Endpoint: " + endpoint);
        System.out.println("  Model: " + modelDeployment);
        System.out.println("  Auth Method: " + authMethod);
    }

    /**
     * STEP 1: Basic chat - send message and get response
     * Uses ChatCompletions API (traditional approach)
     *
     * Current Implementation:
     * - Takes user message
     * - Sends to Azure OpenAI with system prompt
     * - Returns response
     *
     * Limitation: No conversation memory - each call is independent
     */
    public String chat(String userMessage) {
        try {
            // Create message list with system instructions
            List<ChatRequestMessage> messages = new ArrayList<>();

            // Add system message (instructions for the AI)
            messages.add(new ChatRequestSystemMessage(
                "You are a helpful AI assistant that answers questions and provides information."
            ));

            // Add user message
            messages.add(new ChatRequestUserMessage(userMessage));

            // ====================================================================
            // STEP 2 TODO: Responses API would look different here
            //
            // Instead of:
            //   ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
            //
            // STEP 2 would use:
            //   response = openAIClient.responses.create(
            //       model=modelDeployment,
            //       instructions="You are a helpful AI assistant...",
            //       input=userMessage,
            //       previous_response_id=lastResponseId  // <-- KEY ADDITION
            //   )
            //
            // This would:
            // 1. Include previous response ID for conversation continuity
            // 2. Handle response tracking automatically
            // 3. Simplify multi-turn conversations
            // ====================================================================

            // Create options with messages and configuration
            ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                    .setMaxCompletionTokens(maxTokens);

            // Call Azure OpenAI API (ChatCompletions)
            ChatCompletions response = openAIClient.getChatCompletions(modelDeployment, options);

            // Extract and return the response content
            String responseText = response.getChoices().get(0).getMessage().getContent();

            // ====================================================================
            // STEP 2 TODO: Save response ID for conversation tracking
            //
            // After receiving response:
            //   lastResponseId = response.id;
            //
            // Then on next call, pass it:
            //   previous_response_id=lastResponseId
            //
            // This maintains conversation context across multiple turns
            // ====================================================================

            return responseText;

        } catch (Exception e) {
            System.err.println("Error calling Azure OpenAI: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * STEP 2: Chat with conversation context
     * Uses Responses API (when available in Java SDK)
     *
     * Differences from Step 1:
     * - Accepts previous_response_id parameter
     * - Maintains conversation memory automatically
     * - Simpler API (no need to manually manage message history)
     * - Response includes ID for chaining to next call
     *
     * ====================================================================
     * IMPLEMENTATION PLAN:
     *
     * 1. Check if Java SDK supports responses.create() method
     * 2. Update parameters:
     *    - Remove: ChatCompletionsOptions
     *    - Add: previous_response_id parameter
     * 3. Store response.id for next call
     * 4. Simplify message handling (API handles history)
     *
     * Example flow:
     *   Call 1: chat("Hi")
     *           → saves response.id = "resp_123"
     *   Call 2: chat("Tell me more", "resp_123")
     *           → API automatically includes previous context
     *           → saves response.id = "resp_456"
     *   Call 3: chat("Explain that", "resp_456")
     *           → API knows full conversation history
     * ====================================================================
     */
    public String chatWithContext(String userMessage, String previousResponseId) {
        try {
            // ====================================================================
            // STEP 2 IMPLEMENTATION (when Java SDK supports it):
            //
            // Instead of building message list manually:
            //   response = openAIClient.responses.create(
            //       model=modelDeployment,
            //       instructions="You are a helpful AI assistant...",
            //       input=userMessage,
            //       previous_response_id=previousResponseId  // <-- Maintains context
            //   )
            //
            //   return response.output_text
            // ====================================================================

            // For now, fall back to basic chat (no context)
            System.out.println("Note: Conversation context not yet supported (Java SDK limitation)");
            return chat(userMessage);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    /**
     * STEP 3: Stream chat response
     * Displays response incrementally as it's received
     *
     * Differences from Step 1 & 2:
     * - Response comes in chunks (deltas)
     * - Print each chunk immediately (not wait for full response)
     * - Better UX for long responses
     *
     * ====================================================================
     * IMPLEMENTATION PLAN:
     *
     * 1. Switch from getChatCompletions() to getChatCompletionsStream()
     * 2. Listen for streaming events:
     *    - "response.output_text.delta" → print chunk
     *    - "response.completed" → save response.id
     * 3. Update ChatCLI to call this method
     *
     * Example:
     *   stream = openAIClient.getChatCompletionsStream(...)
     *   for event in stream:
     *       if event.type == "response.output_text.delta":
     *           print(event.delta, end="")  // Print as it arrives
     *       elif event.type == "response.completed":
     *           lastResponseId = event.response.id
     *
     * User experience:
     *   Without streaming: Wait 5 seconds... [full response appears]
     *   With streaming:    Text appears word-by-word as it's generated
     * ====================================================================
     */
    public void streamChat(String userMessage, String previousResponseId) {
        try {
            System.out.println("Streaming not yet implemented in Java SDK");

            // ====================================================================
            // STEP 3 IMPLEMENTATION (when Java SDK supports streaming):
            //
            // List<ChatRequestMessage> messages = new ArrayList<>();
            // messages.add(new ChatRequestSystemMessage(...));
            // messages.add(new ChatRequestUserMessage(userMessage));
            //
            // ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
            //     .setMaxCompletionTokens(maxTokens)
            //     .setStream(true);  // <-- Enable streaming
            //
            // IterableStream<ChatCompletions> stream =
            //     openAIClient.getChatCompletionsStream(modelDeployment, options);
            //
            // for (ChatCompletions event : stream) {
            //     if (event has text delta) {
            //         System.out.print(event.getDelta());  // Print immediately
            //     }
            //     if (event indicates completion) {
            //         lastResponseId = event.getId();
            //     }
            // }
            // ====================================================================

            String response = chat(userMessage);
            System.out.println(response);

        } catch (Exception e) {
            System.err.println("Streaming error: " + e.getMessage());
        }
    }

    /**
     * STEP 4 (OPTIONAL): Async operations
     * Non-blocking calls for better responsiveness
     *
     * Java SDK provides async alternatives:
     * - getChatCompletionsAsync() instead of getChatCompletions()
     * - getChatCompletionsStreamAsync() instead of getChatCompletionsStream()
     *
     * Use CompletableFuture or Project Reactor for async handling
     */
}