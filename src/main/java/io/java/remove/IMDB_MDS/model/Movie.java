package io.java.remove.IMDB_MDS.model;

public record Movie(
        String posterLink,
        String seriesTitle,
        String releasedYear,
        String certificate,
        String runtime,
        String genre,
        String imdbRating,
        String overview,
        String metaScore,
        String director,
        String star1,
        String star2,
        String star3,
        String star4,
        String noOfVotes,
        String gross
) {}
