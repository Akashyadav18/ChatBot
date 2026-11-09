package com.SpringAI.ChatBot.controller;

import com.SpringAI.ChatBot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public String Chat(@RequestBody String message){
        return chatService.chat(message);
    }
}

