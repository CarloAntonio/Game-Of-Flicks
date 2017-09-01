package com.riskitbiskit.gameofflicks.MainActivity;

public class Movie {
    //public static variables
    public static final String MOVIE_NAME = "name";
    public static final String MOVIE_OVERVIEW = "overview";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_POSTER_PATH = "poster";
    public static final String MOVIE_RELEASE_DATE = "releaseDate";
    public static final String MOVIE_ID = "movieId";

    //private globle variables
    private String originalTitle;
    private String overview;
    private double voteAverage;
    private String posterPath;
    private String releaseDate;
    private long movieId;

    public Movie(String originalTitle, String overview, double voteAverage, String posterPath, String releaseDate, long movieId) {
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.movieId = movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getMovieId() {
        return movieId;
    }
}
