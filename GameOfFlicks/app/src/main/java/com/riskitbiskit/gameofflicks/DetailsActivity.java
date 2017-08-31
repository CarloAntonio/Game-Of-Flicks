package com.riskitbiskit.gameofflicks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get intent details
        Intent intent = getIntent();
        String movieName = intent.getStringExtra(Movie.MOVIE_NAME);
        String movieOverview = intent.getStringExtra(Movie.MOVIE_OVERVIEW);
        double movieRating = intent.getDoubleExtra(Movie.MOVIE_RATING, 0.0);
        String stringRating = String.valueOf(movieRating);
        String moviePosterPath = intent.getStringExtra(Movie.MOVIE_POSTER_PATH);
        String movieReleaseDate = intent.getStringExtra(Movie.MOVIE_RELEASE_DATE);

        //Find relevant views
        TextView titleTV = (TextView) findViewById(R.id.title_tv);
        TextView overviewTV = (TextView) findViewById(R.id.overview_tv);
        TextView ratingTV = (TextView) findViewById(R.id.rating_tv);
        ImageView posterIV = (ImageView) findViewById(R.id.poster_iv);
        TextView releaseTV = (TextView) findViewById(R.id.release_tv);

        //Set Views
        titleTV.setText(movieName);
        overviewTV.setText(movieOverview);
        ratingTV.setText(stringRating);
        releaseTV.setText(movieReleaseDate);

        //Picasso image
        String baseUrl = "http://image.tmdb.org/t/p/w500/";
        String fullUrl = baseUrl + moviePosterPath;
        Picasso.with(this).load(fullUrl).into(posterIV);

    }
}
