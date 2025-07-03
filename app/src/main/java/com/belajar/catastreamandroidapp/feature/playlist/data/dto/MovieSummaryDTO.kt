package com.belajar.catastreamandroidapp.feature.playlist.data.dto
data class MovieSummaryDTO(
    val id: Long,
    val tmdbId: Int?,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String,
    val genreIds: List<Int>? = emptyList()
)
