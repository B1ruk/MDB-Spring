package io.java.remove.IMDB_MDS.processor.fileProcessor;

import io.java.remove.IMDB_MDS.model.Movie;
import io.java.remove.IMDB_MDS.processor.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CsvMovieProcessor extends FileMovieProcessor{

    @Value("${imdb.movies.filename}")
    private String fileName;
    private MovieMapper movieMapper;

    public CsvMovieProcessor(MovieMapper movieMapper) {
        super(movieMapper);
        this.movieMapper=movieMapper;
    }

    @Override
    public List<Movie> loadMovies() {
        Path path = Paths.get(System.getProperty("user.home"),fileName);
        try {
            return Files.readAllLines(path)
                    .stream()
                    .skip(1)
                    .map(line -> movieMapper.map(line))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
