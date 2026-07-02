package com.example.chat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Command-line interface for the chat application.
 * Handles user input and displays responses.
 */
@Component
public class ChatCLI implements CommandLineRunner {
    private final ChatService chatService;
    private String lastResponseId;

    public ChatCLI(ChatService chatService) {
        this.chatService = chatService;
        this.lastResponseId = null;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Claude Chat App ===");
        System.out.println("Type your questions below. Type 'quit' to exit.\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            try {
                System.out.print("Claude: ");
                // TODO: Call chat service (will implement in Step 1)
                String response = chatService.chat(userInput);
                System.out.println(response);
                System.out.println();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}