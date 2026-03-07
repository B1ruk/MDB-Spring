package io.java.remove.IMDB_MDS.processor;


import io.java.remove.IMDB_MDS.model.Movie;

import java.util.List;

public interface MovieProcessor {
    public List<Movie> loadMovies();
}
