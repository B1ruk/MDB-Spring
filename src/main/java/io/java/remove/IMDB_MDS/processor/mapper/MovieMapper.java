package io.java.remove.IMDB_MDS.processor.mapper;


import io.java.remove.IMDB_MDS.model.Movie;

public interface MovieMapper {
    Movie map(Object rawData);
}
