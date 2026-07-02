package com.example.chat;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.core.credential.AzureKeyCredential;

/**
 * Factory for creating different authentication credentials.
 * Supports 7 authentication methods for Azure.
 */
public class AuthenticationFactory {

    /**
     * Create a credential based on authentication method.
     *
     * @param method The authentication method (key, cli, env, browser, managed, device, default)
     * @param apiKey The API key (required for 'key' method, optional for others)
     * @return TokenCredential or AzureKeyCredential
     */
    public static Object createCredential(String method, String apiKey) {
        System.out.println("Creating credential using method: " + method);

        switch (method.toLowerCase()) {
            case "key":
                return createKeyCredential(apiKey);
            case "cli":
                return createCliCredential();
            case "env":
                return createEnvironmentCredential();
            case "browser":
                return createInteractiveBrowserCredential();
            case "managed":
                return createManagedIdentityCredential();
            case "device":
                return createDeviceCodeCredential();
            case "default":
                return createDefaultCredential();
            default:
                System.out.println("Unknown method: " + method + ". Using default.");
                return createDefaultCredential();
        }
    }

    /**
     * 1. AzureKeyCredential - Direct API Key
     * ✅ WORKS LOCALLY: Yes
     * USE: Development and testing
     * ENTRA ID: No (bypasses Entra ID)
     * HOW: Copy your API key from Azure Portal
     */
    private static AzureKeyCredential createKeyCredential(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException(
                "API key is required for 'key' authentication method. " +
                "Set AZURE_OPENAI_API_KEY in .env file."
            );
        }
        System.out.println("✅ Using AzureKeyCredential");
        return new AzureKeyCredential(apiKey);
    }

    /**
     * 2. AzureCliCredential - Uses Azure CLI credentials
     * ✅ WORKS LOCALLY: Yes (requires `az login`)
     * USE: Local development with Azure CLI
     * ENTRA ID: Yes (uses tokens from `az login`)
     * HOW: Run `az login` first, then this uses those credentials
     */
    private static TokenCredential createCliCredential() {
        System.out.println("✅ Using AzureCliCredential");
        System.out.println("   Requires: az login (run in another terminal)");
        return new AzureCliCredentialBuilder().build();
    }

    /**
     * 3. EnvironmentCredential - Uses environment variables
     * ✅ WORKS LOCALLY: Yes (if you set env vars)
     * USE: CI/CD pipelines, Docker containers
     * ENTRA ID: Yes (reads Entra ID credentials from env)
     * HOW: Set environment variables:
     *      AZURE_TENANT_ID=your-tenant-id
     *      AZURE_CLIENT_ID=your-client-id
     *      AZURE_CLIENT_SECRET=your-client-secret
     */
    private static TokenCredential createEnvironmentCredential() {
        System.out.println("✅ Using EnvironmentCredential");
        System.out.println("   Requires: AZURE_TENANT_ID, AZURE_CLIENT_ID, AZURE_CLIENT_SECRET env vars");
        return new EnvironmentCredentialBuilder().build();
    }

    /**
     * 4. InteractiveBrowserCredential - Browser-based login
     * ✅ WORKS LOCALLY: Yes
     * USE: Interactive applications, user login
     * ENTRA ID: Yes (opens browser to Entra ID login)
     * HOW: Opens your default browser to log in
     */
    private static TokenCredential createInteractiveBrowserCredential() {
        System.out.println("✅ Using InteractiveBrowserCredential");
        System.out.println("   A browser window will open for you to sign in...");
        return new InteractiveBrowserCredentialBuilder().build();
    }

    /**
     * 5. ManagedIdentityCredential - For Azure resources
     * ❌ WORKS LOCALLY: No (only works inside Azure)
     * USE: VMs, App Service, Azure Functions, containers in Azure
     * ENTRA ID: Yes (automatic in Azure)
     * HOW: Automatic when running in Azure. Enable Managed Identity on the resource.
     * WHY NOT LOCAL: Only works when running inside Azure infrastructure
     */
    private static TokenCredential createManagedIdentityCredential() {
        System.out.println("⚠️  Using ManagedIdentityCredential");
        System.out.println("   WARNING: This only works inside Azure (VMs, App Service, etc.)");
        System.out.println("   It will NOT work on your local machine!");
        return new ManagedIdentityCredentialBuilder().build();
    }

    /**
     * 6. DeviceCodeCredential - For devices without browsers
     * ⚠️  WORKS LOCALLY: Technically yes, but awkward
     * USE: IoT devices, phones, limited-input devices
     * ENTRA ID: Yes (device code flow with Entra ID)
     * HOW: Displays a code you enter on another device
     * WHY NOT LOCAL: Not practical for normal development
     */
    private static TokenCredential createDeviceCodeCredential() {
        System.out.println("⚠️  Using DeviceCodeCredential");
        System.out.println("   A device code will be displayed. Enter it in another browser window.");
        return new DeviceCodeCredentialBuilder()
                .challengeConsumer(challenge -> System.out.println(challenge))
                .build();
    }

    /**
     * 7. DefaultAzureCredential - Tries multiple methods automatically
     * ✅ WORKS LOCALLY: Yes (if any method is configured)
     * USE: Production, local dev, everywhere
     * ENTRA ID: Yes (most methods use Entra ID)
     * HOW: Automatically tries (in order):
     *      1. EnvironmentCredential
     *      2. WorkloadIdentityCredential
     *      3. ManagedIdentityCredential
     *      4. SharedTokenCacheCredential
     *      5. IntelliJCredential (if in IntelliJ IDE)
     *      6. AzureCliCredential (uses `az login`)
     *      7. AzurePowerShellCredential
     *      8. AzureDeveloperCliCredential
     *
     * This is the RECOMMENDED method for most scenarios!
     */
    private static TokenCredential createDefaultCredential() {
        System.out.println("✅ Using DefaultAzureCredential");
        System.out.println("   Trying multiple authentication methods automatically...");
        return new DefaultAzureCredentialBuilder().build();
    }
}