package io.java.remove.IMDB_MDS.processor.fileProcessor;


import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.processor.MovieProcessor;
import io.java.remove.IMDB_MDS.processor.mapper.MovieMapper;

import java.util.List;

public abstract class FileMovieProcessor implements MovieProcessor {

    private MovieMapper movieMapper;

    public FileMovieProcessor(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Override
    public abstract List<Movie> loadMovies();
}
