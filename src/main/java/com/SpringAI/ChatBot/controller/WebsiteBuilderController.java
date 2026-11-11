package com.SpringAI.ChatBot.controller;

import com.SpringAI.ChatBot.service.WebsiteBuilderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebsiteBuilderController {

    private final WebsiteBuilderService websiteBuilderService;

    public WebsiteBuilderController(WebsiteBuilderService websiteBuilderService) {
        this.websiteBuilderService = websiteBuilderService;
    }

    @PostMapping("/website")
    public String generateWebsite(@RequestBody String message){
        return websiteBuilderService.generateWebsite(message);
    }
}
