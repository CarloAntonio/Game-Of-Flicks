package com.riskitbiskit.gameofflicks.DetailsActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.riskitbiskit.gameofflicks.MainActivity.MainActivity;
import com.riskitbiskit.gameofflicks.MainActivity.Movie;
import com.riskitbiskit.gameofflicks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>,
        View.OnClickListener{

    public static final int PROMO_LOADER = 0;
    public static final int REVIEW_LOADER = 1;

    private long movieId;
    TableLayout trailerTableLayout;
    TableLayout reviewTableLayout;
    LoaderManager.LoaderCallbacks reviewLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.loading_error);

        //Get intent details
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Movie.MOVIE_NAME)
                && intent.hasExtra(Movie.MOVIE_OVERVIEW) && intent.hasExtra(Movie.MOVIE_RATING)
                && intent.hasExtra(Movie.MOVIE_POSTER_PATH) && intent.hasExtra(Movie.MOVIE_RELEASE_DATE)
                && intent.hasExtra(Movie.MOVIE_ID)) {

            relativeLayout.setVisibility(View.INVISIBLE);

            String movieName = intent.getStringExtra(Movie.MOVIE_NAME);
            String movieOverview = intent.getStringExtra(Movie.MOVIE_OVERVIEW);
            double movieRating = intent.getDoubleExtra(Movie.MOVIE_RATING, 0.0);
            String stringRating = String.valueOf(movieRating);
            String moviePosterPath = intent.getStringExtra(Movie.MOVIE_POSTER_PATH);
            String movieReleaseDate = intent.getStringExtra(Movie.MOVIE_RELEASE_DATE);
            movieId = intent.getLongExtra(Movie.MOVIE_ID, 0);

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

        } else {
            relativeLayout.setVisibility(View.VISIBLE);
        }

        //Locate dynamic table
        trailerTableLayout = (TableLayout) findViewById(R.id.trailers_table_layout);
        reviewTableLayout = (TableLayout) findViewById(R.id.review_table_layout);

        reviewLoaderCallback = new LoaderManager.LoaderCallbacks<List<Review>>() {
            @Override
            public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
                Uri baseUri = Uri.parse(MainActivity.ROOT_URL + "/movie/" + movieId + "/reviews");
                Uri.Builder builder = baseUri.buildUpon();

                builder.appendQueryParameter("api_key", MainActivity.API_KEY);
                builder.appendQueryParameter("language", "en-US");
                builder.appendQueryParameter("page", "1");

                return new ReviewLoader(getBaseContext(), builder.toString());
            }

            @Override
            public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
                View tableRow;
                if (!reviews.isEmpty()) {
                    //Create a new row for each video path
                    for (int i = 0; i < reviews.size(); i++) {
                        tableRow = View.inflate(getBaseContext(), R.layout.review_table_row, null);
                        TextView contentTextView = tableRow.findViewById(R.id.review_content_tv);
                        TextView authorTextView = tableRow.findViewById(R.id.review_author_tv);

                        //set text
                        contentTextView.setText(reviews.get(i).getContent());
                        authorTextView.setText("-" + reviews.get(i).getAuthor());

                        reviewTableLayout.addView(tableRow);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Review>> loader) {
                reviewTableLayout.removeAllViews();
            }
        };

        getLoaderManager().initLoader(PROMO_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, reviewLoaderCallback);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(MainActivity.ROOT_URL + "/movie/" + movieId + "/videos");
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("api_key", MainActivity.API_KEY);
        builder.appendQueryParameter("language", "en-US");

        return new VideoPathLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> videoPaths) {
        View tableRow;
        if (!videoPaths.isEmpty()) {
            //Create a new row for each video path
            for (int i = 0; i < videoPaths.size(); i++) {
                tableRow = View.inflate(getBaseContext(), R.layout.trailer_table_row, null);
                tableRow.setTag(videoPaths.get(i));
                tableRow.setOnClickListener(this);
                TextView textView = tableRow.findViewById(R.id.trailer_tv);
                int trailerNumber = i + 1;
                textView.setText("Play Movie Trailer " + trailerNumber);
                trailerTableLayout.addView(tableRow);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        trailerTableLayout.removeAllViews();
    }

    @Override
    public void onClick(View view) {
        String identifier = (String) view.getTag();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + identifier)));
        trailerTableLayout.removeAllViews();
        reviewTableLayout.removeAllViews();
    }
}
