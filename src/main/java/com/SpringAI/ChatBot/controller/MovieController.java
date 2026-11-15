package com.SpringAI.ChatBot.controller;

import com.SpringAI.ChatBot.model.MovieMatch;
import com.SpringAI.ChatBot.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public List<MovieMatch> search(@RequestParam String query){
        return movieService.search(query);
    }

    @GetMapping("/{title}/similar")
    public List<MovieMatch> similarMovies(@PathVariable String title){
        return movieService.similarMovie(title);
    }
}
