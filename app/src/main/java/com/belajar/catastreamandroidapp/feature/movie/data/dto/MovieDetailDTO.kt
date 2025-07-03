package com.belajar.catastreamandroidapp.feature.movie.data.dto

import com.google.gson.annotations.SerializedName


data class MovieDetailDTO(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    val runtime: Int?,
    val genres: List<GenreDTO>?,
)