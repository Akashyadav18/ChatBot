package com.SpringAI.ChatBot.controller;

import com.SpringAI.ChatBot.DTO.DocumentUploadResponse;
import com.SpringAI.ChatBot.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/doc")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<DocumentUploadResponse> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        DocumentUploadResponse response = documentService.uploadPdf(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocument (@RequestBody String question){
        List<Document> results = documentService.searchDocument(question);
        return ResponseEntity.ok(results);
    }
}
