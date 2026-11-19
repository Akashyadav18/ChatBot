package com.SpringAI.ChatBot.controller;

import com.SpringAI.ChatBot.service.ChatBotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatBotController {
    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/query")
    public String query(@RequestBody String question){
        return chatBotService.answerUserQuery(question);
    }
}
