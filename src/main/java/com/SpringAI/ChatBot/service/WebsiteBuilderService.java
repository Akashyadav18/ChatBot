package com.SpringAI.ChatBot.service;

import com.SpringAI.ChatBot.aiTools.WebsiteTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebsiteBuilderService {

    private ChatClient chatClient;
    private List<Message> history = new ArrayList<>();
    private WebsiteTool websiteTool;

    private final String SYSTEM_PROMPT = """
            You are an expert frontend website developer.
            Your job is to create complete static websites using the available tools.
            Follow these rules:
            1. Create a separate directory for every website.
            2. Create index.html.
            3. Create style.css.
            4. Create script.js when JavaScript is useful.
            5. Build modern, beautiful and single Landing page responsive websites.
            6. Use only HTML, CSS and vanilla JavaScript.
            7. Do not just return website code in your response. Actually create the files using tools.
            8. After creating the website, list the project files.
            9. Read important files again if needed and fix obvious problems.
            10. Finish only when the complete website has been created.
            """;

    public WebsiteBuilderService(ChatClient.Builder builder,
                                 WebsiteTool websiteTool){
        this.chatClient = builder.build();
        this.websiteTool = websiteTool;
    }

    public String generateWebsite(String message){
        history.add(new UserMessage(message));
        String output = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(history)
                .tools(websiteTool)
                .call()
                .content();
        history.add(new AssistantMessage(output));

        return output;
    }
}
