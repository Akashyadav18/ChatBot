package com.SpringAI.ChatBot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatBotService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    @Value("classpath*:knowledge/*.pdf")
    private Resource[] policyFiles;

    public ChatBotService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

//    Document info should be store in vector db first
    @PostConstruct
    public void loadKnowledgeBase(){
//        1. Read all pdf files.
//        2. for each files I will divide them into chunk
//        3. I will store those chunks in vector DB --> pinecone
        List<Document> allChunks = new ArrayList<>();
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(300).build();
//        loop through each PDF file
        for(Resource resource : policyFiles){
            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
            List<Document> pages = reader.read();
            List<Document> chunks = splitter.apply(pages);
            allChunks.addAll(chunks);
        }
        vectorStore.add(allChunks);
    }

    public String answerUserQuery(String question){
//        1. Question --> vector
//        2. similarity search in our vector db
//        3. Top 4 results fetch
        List<Document> relevantChunks = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(4)
                        .build()
        );
//      DB relevant chunks r in list -> String before providing to llm
        StringBuilder context = new StringBuilder();
        for (Document document : relevantChunks){
            context.append(document.getText()).append("\n\n");
        }
        String finalContext = context.toString();
//      System prompt + relevant info
        String SYSTEM_PROMPT = """
                You are an AI customer support assistant for our e-commerce company.
                Answer the customer using ONLY the company information provided below.
                If the answer is not available in the provided information, say:
                "I don't have that information in the company documents."
                COMPANY INFORMATION
                %s
                """.formatted(finalContext);
//      provided info to llm
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(question)
                .call()
                .content();
    }
}
