package com.SpringAI.ChatBot.service;

import com.SpringAI.ChatBot.DTO.DocumentUploadResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DocumentService {

    private static VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public DocumentUploadResponse uploadPdf(MultipartFile file) throws IOException {

        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("Please upload a PDF file");
        }
        if(file.getSize() > 10 * 1024 * 1024){
            throw new IllegalArgumentException("PDF size  cannot exceed 10 MB");
        }
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.toLowerCase().endsWith(".pdf")){
            throw new IllegalArgumentException("Only PDF are allowed");
        }
        byte[] fileBytes = file.getBytes();

//        Convert the bytes array into Spring Resource, bcoz PDF reader accepts Resource as input
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
//        Create the Spring AI PDF reader.
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
        List<Document> originalDocument = pdfReader.get();
        if(originalDocument.isEmpty()){
            throw new IllegalArgumentException("No Readable content found in PDF");
        }

//        Split PDF content into smaller chunks
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(500).build();
        List<Document> chunksText = splitter.apply(originalDocument);
        if(chunksText.isEmpty()){
            throw new IllegalArgumentException("Unable to extract data from PDF");
        }
//        Store chunks and generate embedding
        vectorStore.add(chunksText);

        return new DocumentUploadResponse(
                fileName,
                "PDF uploaded and processed Successfully"
        );
    }

    public List<Document> searchDocument(String question){
        SearchRequest similaritySearch = SearchRequest.builder()
                .query(question)
                .topK(4)
                .similarityThreshold(0.75) //Minimum similarity score 75% se jada raha to hi pass hoga.
                .build();
        return vectorStore.similaritySearch(similaritySearch);
    }
}
