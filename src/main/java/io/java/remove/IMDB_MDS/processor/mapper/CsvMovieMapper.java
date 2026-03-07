package io.java.remove.IMDB_MDS.processor.mapper;


import io.java.remove.IMDB_MDS.model.Movie;
import org.springframework.stereotype.Service;

@Service
public class CsvMovieMapper implements MovieMapper {
    @Override
    public Movie map(Object rawData) {
        String[] columns = ((String) rawData).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        return new Movie(
                columns[0],                          // Poster_Link
                columns[1],                          // Series_Title
                columns[2],            // Released_Year
                columns[3],                          // Certificate
                columns[4],                          // Runtime
                columns[5],                          // Genre
                columns[6],         // IMDB_Rating
                columns[7],                          // Overview
                columns[8],        // Meta_score
                columns[9],                          // Director
                columns[10],                         // Star1
                columns[11],                         // Star2
                columns[12],                         // Star3
                columns[13],                         // Star4
                columns[14],          // No_of_Votes
                columns[15]                          // Gross
        );
    }

    // Helper to handle empty strings or "N/A" in numeric columns
    private int parseSafeInt(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
    }

    private double parseSafeDouble(String s) {
        return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s.trim());
    }

    private Integer parseSafeInteger(String s) {
        return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
    }

    private long parseSafeLong(String s) {
        return (s == null || s.isBlank()) ? 0L : Long.parseLong(s.trim());
    }

}
