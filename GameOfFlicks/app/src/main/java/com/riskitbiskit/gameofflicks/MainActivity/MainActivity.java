package com.riskitbiskit.gameofflicks.MainActivity;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.riskitbiskit.gameofflicks.DetailsActivity.DetailsActivity;
import com.riskitbiskit.gameofflicks.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener{
    //Testing
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    //General global static constants
    public static final int MOVIE_LOADER = 0;
    private MovieArrayAdapter mMovieArrayAdapter;

    //API global static constants
    public static final String API_KEY = "omitted";
    public static final String ROOT_URL = "https://api.themoviedb.org/3";
    public static final String PATH = "path";
    public static final String NOW_PLAYING_PATH = "/movie/now_playing";
    public static final String TOP_RATED_PATH = "/movie/top_rated";
    public static final String MOST_POPULAR_PATH = "/movie/popular";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize variables
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GridView moviesGridView = (GridView) findViewById(R.id.content_grid_view);

        //Setup adapter
        mMovieArrayAdapter = new MovieArrayAdapter(this, R.layout.movie_poster_item, new ArrayList<Movie>());
        moviesGridView.setAdapter(mMovieArrayAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Get chosen movie
                Movie chosenMovie = mMovieArrayAdapter.getItem(i);

                //Extract traits
                String name = chosenMovie.getOriginalTitle();
                String overview = chosenMovie.getOverview();
                double rating = chosenMovie.getVoteAverage();
                String posterPath = chosenMovie.getPosterPath();
                String releaseDate = chosenMovie.getReleaseDate();
                long id = chosenMovie.getMovieId();

                //Create intent and add traits
                Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
                intent.putExtra(Movie.MOVIE_NAME, name);
                intent.putExtra(Movie.MOVIE_OVERVIEW, overview);
                intent.putExtra(Movie.MOVIE_RATING, rating);
                intent.putExtra(Movie.MOVIE_POSTER_PATH, posterPath);
                intent.putExtra(Movie.MOVIE_RELEASE_DATE, releaseDate);
                intent.putExtra(Movie.MOVIE_ID, id);

                //Start activity
                startActivity(intent);
            }
        });

        if (getActiveNetworkInfo() != null && getActiveNetworkInfo().isConnected()) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        } else {
            TextView textView = (TextView) findViewById(R.id.no_internet_view);
            textView.setText(R.string.no_internet_connectivity);
        }
    }

    public String getRequestUrl(String desiredPath) {

        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PATH, MOST_POPULAR_PATH);
            editor.apply();
            return true;
        } else if (id == R.id.action_top_rated) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PATH, TOP_RATED_PATH);
            editor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        String queryPath = NOW_PLAYING_PATH;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(PATH)) {
            queryPath = sharedPreferences.getString(PATH, NOW_PLAYING_PATH);
        }

        Uri baseUri = Uri.parse(ROOT_URL + queryPath);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("api_key", API_KEY);
        builder.appendQueryParameter("language", "en-US");

        return new MovieLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Movie>> loader, List<Movie> movies) {

        mMovieArrayAdapter.clear();

        if (movies != null && !movies.isEmpty()) {
            mMovieArrayAdapter.addAll(movies);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        mMovieArrayAdapter.clear();
    }


    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
}
