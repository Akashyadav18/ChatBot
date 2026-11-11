package com.SpringAI.ChatBot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    //Apna appln or chatGPT k service k baat karne k liye: ChatClient
    private ChatClient chatClient;
    private List<Message> history = new ArrayList<>();
    private final String SYSTEM_PROMPT = """
            you are funny chatbot, you reply sarcastically
            """;

    public ChatService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public Flux<String> chat(String message){
        history.add(new UserMessage(message));
        StringBuilder fullResponse = new StringBuilder();

        Flux<String> output = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(history)
                .stream()
                .content()
                .doOnNext(fullResponse::append)
                .doOnComplete(() -> {
                    history.add(new AssistantMessage(fullResponse.toString()));
                });

        return output;
    }

}
