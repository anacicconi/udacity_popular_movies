package com.cicconi.popularmovies.utils;

import com.cicconi.popularmovies.model.Movie;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieJsonUtils
{
    public static List<Movie> getMoviesFromJson(String moviesString){
        List<Movie> moviesList = new ArrayList<>();

        try {
            if(null != moviesString && !moviesString.isEmpty()) {
                JSONObject moviesJson = new JSONObject(moviesString);
                JSONArray results = moviesJson.getJSONArray("results");
                int page = moviesJson.getInt("page");

                if(results.length() > 0) {
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject movieJson = results.getJSONObject(i);

                        String title = movieJson.optString("original_title", "Unknown");
                        String image = movieJson.optString("poster_path", "");
                        String synopsis = movieJson.optString("overview", "Unknown");
                        String releaseDate = movieJson.optString("release_date", "Unknown");
                        double rating = movieJson.optDouble("vote_average", 0.0);

                        if(!image.isEmpty()) {
                            Movie movie = new Movie(title, image, synopsis, Double.toString(rating), releaseDate, page);
                            moviesList.add(movie);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesList;
    }
}
