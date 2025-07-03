package com.belajar.catastreamandroidapp.feature.playlist.data.dto
data class AddMovieToPlaylistRequest(
    val tmdbMovieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String?
)
