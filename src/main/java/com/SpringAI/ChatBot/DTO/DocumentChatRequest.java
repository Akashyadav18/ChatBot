package com.SpringAI.ChatBot.DTO;

public record DocumentChatRequest(
        String documentId,
        String question
) { }
