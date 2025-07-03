package com.belajar.catastreamandroidapp.feature.movie.data.remote

import com.belajar.catastreamandroidapp.feature.movie.data.dto.GenreResponse
import com.belajar.catastreamandroidapp.feature.movie.data.dto.MovieDetailDTO
import com.belajar.catastreamandroidapp.feature.movie.data.dto.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse // Berisi List<MovieResponseDTO>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): GenreResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): MovieDetailDTO

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false
    ): MovieResponse

    @GET("discover/movie")
    suspend fun discoverMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse
}