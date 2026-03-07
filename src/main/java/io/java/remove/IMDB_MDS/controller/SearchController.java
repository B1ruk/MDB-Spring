package io.java.remove.IMDB_MDS.controller;

import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.model.SearchQuery;
import io.java.remove.IMDB_MDS.search.MovieSearch;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/movies")
public class SearchController {

    private MovieSearch movieSearch;

    public SearchController(MovieSearch movieSearch) {
        this.movieSearch = movieSearch;
    }

    @GetMapping("/searchByTitle")
    public Optional<Movie> searchMovieByTitle(@RequestParam("title") String title) {
        return movieSearch.findMovieByTitle(title);
    }
}
