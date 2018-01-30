package com.salis.salismovie.api;

import com.salis.salismovie.model.Movie;
import com.salis.salismovie.model.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Salis on 25/01/2018.
 */

public interface Service {
    @GET("search/movie")
    Call<MovieResponse> searchMovie(@Query("api_key") String apiKey, @Query("query") String movieName);

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);
}
