package com.belajar.catastreamandroidapp.feature.movie.data.dto

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<MovieResponseDTO>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)