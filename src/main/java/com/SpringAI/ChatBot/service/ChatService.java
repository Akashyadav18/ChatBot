package com.SpringAI.ChatBot.service;

import com.SpringAI.ChatBot.aiTools.CalculatorTool;
import com.SpringAI.ChatBot.aiTools.CurrencyExchangeTool;
import com.SpringAI.ChatBot.aiTools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    //Apna appln or chatGPT k service k baat karne k liye: ChatClient
    private ChatClient chatClient;
    private List<Message> history = new ArrayList<>();
    private CalculatorTool calculatorTool;
    private WeatherTool weatherTool;
    private CurrencyExchangeTool currencyExchangeTool;

    private final String SYSTEM_PROMPT = """
            You are the helpful AI assistant with access to external tools.
            Follow these rules:
            1. For arithmetic calculation , always use the calculator tool.
            2. Always use calculator tool for even trivial calculation.
            3. For Current weather of any city, always use the weather tool.
            4. For Currency Exchanges, always use Currency Exchange tool.
            5. You may call multiple tools when solving a multi-step request.
            6. After receiving tool results, explain the answer naturally.
            7. Naver invent current weather or exchange-rate information.
            """;

    public ChatService(ChatClient.Builder builder,
                       CalculatorTool calculatorTool,
                       WeatherTool weatherTool,
                       CurrencyExchangeTool currencyExchangeTool){
        this.chatClient = builder.build();
        this.calculatorTool = calculatorTool;
        this.weatherTool = weatherTool;
        this.currencyExchangeTool = currencyExchangeTool;
    }

    public String chat(String message){
        history.add(new UserMessage(message));
        String output = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(history)
                .tools(calculatorTool, weatherTool, currencyExchangeTool)
                .call()
                .content();
        history.add(new AssistantMessage(output));

        return output;
    }

}
