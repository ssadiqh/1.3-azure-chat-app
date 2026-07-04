package com.example.chat;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        // Load environment variables from .env file
        String projectDir = System.getProperty("user.dir");
        if (!projectDir.endsWith("1.3-azure-chat-app")) {
            projectDir = projectDir + "/1.3-azure-chat-app";
        }
        Dotenv dotenv = Dotenv.configure().directory(projectDir).load();

        SpringApplication.run(ChatApplication.class, args);
    }
}