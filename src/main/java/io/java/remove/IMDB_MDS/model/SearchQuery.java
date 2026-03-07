package io.java.remove.IMDB_MDS.model;

import java.util.List;

public record SearchQuery(String releaseYear, Double rating, List<String> generes) {

}
