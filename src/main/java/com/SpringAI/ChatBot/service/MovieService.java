package com.SpringAI.ChatBot.service;

import com.SpringAI.ChatBot.model.Movie;
import com.SpringAI.ChatBot.model.MovieData;
import com.SpringAI.ChatBot.model.MovieMatch;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MovieService {

    private final EmbeddingModel embeddingModel;

    private final List<Movie> moviesEmbeddings = new ArrayList<>();
    private final JsonMapper jsonMapper;

    public MovieService(@Qualifier("googleGenAiTextEmbedding") EmbeddingModel embeddingModel, JsonMapper jsonMapper) {
        this.embeddingModel = embeddingModel;
        this.jsonMapper = jsonMapper;
    }

    @PostConstruct
    public void initializeMovies() throws IOException {
//        Spring ko batata hai ki movies.json project ke resources folder mein hai
        ClassPathResource resource =
                new ClassPathResource("movies.json");
//        us file ko bytes ki form mein read karta hai taaki JAR file banne ke baad bhi code crash na ho.
        InputStream inputStream =
                resource.getInputStream();
//      jsonMapper- JSON text ko Java objects mein convert karta hai
//      TypeReference<>() isliye lagaya hai taaki Java ko pata chale ki humein ek single object nahi, balki List<MovieData> chahiye
        List<MovieData> movieDataList = jsonMapper.readValue(
                inputStream, new TypeReference<>() {});
//      MovieData is DTO
        for (MovieData movieData : movieDataList) {
            float[] embedding = embeddingModel.embed(
                    movieData.getDescription());
//            Title, Description, aur naya embedding milakar ek final Movie object banta
            Movie movie = new Movie(
                    movieData.getTitle(),
                    movieData.getDescription(),
                    embedding
            );

            moviesEmbeddings.add(movie);
        }
        inputStream.close();
        log.warn(moviesEmbeddings.size() + " movies loaded with embeddings.");
    }

    public List<MovieMatch> search(String query){
        float[] userQueryEmbedding = embeddingModel.embed(query);
        List<MovieMatch> matches = new ArrayList<>();
        for (Movie movie : moviesEmbeddings){
            double similarity = cosineSimilarity(userQueryEmbedding, movie.getEmbedding());
            MovieMatch match = new MovieMatch(movie.getTitle(), movie.getDescription(), similarity);
            matches.add(match);
        }
        sortBySimilarity(matches);
        return topKMatches(matches, 3);
    }

    public List<MovieMatch> similarMovie(String title){
        Movie selectedMovie = findMovie(title);
        List<MovieMatch> matches = new ArrayList<>();
        for (Movie movie : moviesEmbeddings){
            if (movie.getTitle().equalsIgnoreCase(title)){
                continue;
            }
            double similarity = cosineSimilarity(selectedMovie.getEmbedding(), movie.getEmbedding());
            MovieMatch match = new MovieMatch(
                    movie.getTitle(),
                    movie.getDescription(),
                    similarity
            );
            matches.add(match);
        }
        sortBySimilarity(matches);
        return topKMatches(matches, 3);
    }

    private Movie findMovie(String title){
        for (Movie movie : moviesEmbeddings){
            if (movie.getTitle().equalsIgnoreCase(title)){
                return movie;
            }
        }
        throw new IllegalArgumentException("Movie not found "+title);
    }

    private double cosineSimilarity(float[] a, float[] b){
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i=0; i<a.length; i++){
            dotProduct += (a[i] * b[i]);
            normA += (a[i] * a[i]);
            normB += (b[i] * b[i]);
        }
        if(normA == 0 || normB == 0){
            return 0.0;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

//    Descending order
    private void sortBySimilarity(List<MovieMatch> matches){
        Collections.sort(matches,
                (first, second) -> Double.compare(
                        second.getMatch(),
                        first.getMatch()
                ));
    }

    private List<MovieMatch> topKMatches(List<MovieMatch> matches, int limit){
        List<MovieMatch> topMatches = new ArrayList<>();
        int numberOfMatches = Math.min(limit, matches.size());
        for(int i=0; i<numberOfMatches; i++){
            topMatches.add(matches.get(i));
        }
        return topMatches;
    }
}
