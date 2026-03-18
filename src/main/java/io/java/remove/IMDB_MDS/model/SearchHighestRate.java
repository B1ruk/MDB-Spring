package io.java.remove.IMDB_MDS.model;

import java.util.List;

public record SearchHighestRate(String releaseYear, Double rating, List<String> generes) {
}
