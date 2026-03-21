package io.java.remove.IMDB_MDS.controller;


import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.model.SearchQuery;
import io.java.remove.IMDB_MDS.search.MovieSearch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("api/v1/movies")

public class GeneraController {

    private MovieSearch movieSearch;

    public GeneraController(MovieSearch movieSearch) {
        this.movieSearch = movieSearch;
    }

    @GetMapping("/searchByGenera")
    public Optional<Movie> searchMovieByHighRating(@RequestParam("genera") String genera) {
        // Build a SearchQuery with the provided genre and delegate to MovieSearch
        SearchQuery query = new SearchQuery(null, null, List.of(genera));
        return movieSearch.searchMovieByHighestRating(query);
    }
}
