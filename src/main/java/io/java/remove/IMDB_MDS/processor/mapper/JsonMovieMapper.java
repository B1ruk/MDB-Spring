package io.java.remove.IMDB_MDS.processor.mapper;

import com.google.gson.internal.LinkedTreeMap;
import io.java.remove.IMDB_MDS.model.Movie;


public class JsonMovieMapper implements MovieMapper {
    @Override
    public Movie map(Object rawData) {
        LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) rawData;
        return new Movie(
                (String) map.get("Poster_Link"),
                (String) map.get("Series_Title"),
                String.valueOf(map.get("Released_Year")), // Handle potential number-to-string
                (String) map.get("Certificate"),
                (String) map.get("Runtime"),
                (String) map.get("Genre"),
                String.valueOf(map.get("IMDB_Rating")),
                (String) map.get("Overview"),
                String.valueOf(map.get("Meta_score")),
                (String) map.get("Director"),
                (String) map.get("Star1"),
                (String) map.get("Star2"),
                (String) map.get("Star3"),
                (String) map.get("Star4"),
                String.valueOf(map.get("No_of_Votes")),
                (String) map.get("Gross")
        );
    }
}
