package io.java.remove.IMDB_MDS.controller;

import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.model.SearchQuery;
import io.java.remove.IMDB_MDS.search.MovieSearch;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/search")
public class MovieController {

    private MovieSearch movieSearch;

    public MovieController(MovieSearch movieSearch) {
        this.movieSearch = movieSearch;
    }

    @PostMapping("/")
    public List<Movie> movie(@RequestBody SearchQuery searchQuery) {
        return movieSearch.searchMovie(searchQuery);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/searchByTitle")
    public Optional<Movie> searchMovieByTitle(@RequestParam("title") String title) {
        return movieSearch.findMovieByTitle(title);
    }

    @GetMapping("/{id}")
    public Optional<Movie> search( @PathVariable Long id){
        return movieSearch.searchByIdAndReleaseYear(id,null);
    }
}
