package io.java.remove.IMDB_MDS.controller;

import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.search.MovieSearch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/movies")
public class SearchControllerWithReleaseYear {

    private MovieSearch movieSearch;

    public SearchControllerWithReleaseYear(MovieSearch movieSearch) {
        this.movieSearch = movieSearch;
    }

    @GetMapping("/searchByReleaseYear")
    public Optional<Movie> searchMovieByTitle(@RequestParam("releaseYear") String releaseYear) {
        return movieSearch.findMovieByReleaseYear(releaseYear);
    }
}
