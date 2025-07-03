package com.belajar.catastreamandroidapp.feature.movie.data.dto

import com.google.gson.annotations.SerializedName

data class MovieResponseDTO(
    @SerializedName("id")
    val id: Int, // TMDB ID

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>?
)