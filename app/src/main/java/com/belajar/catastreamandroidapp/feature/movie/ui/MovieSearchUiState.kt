package com.belajar.catastreamandroidapp.feature.movie.ui

import com.belajar.catastreamandroidapp.feature.movie.data.dto.GenreDTO
import com.belajar.catastreamandroidapp.feature.movie.data.dto.MovieResponseDTO

data class MovieSearchUiState(
    val movies: List<MovieResponseDTO> = emptyList(),
    val genres: List<GenreDTO> = emptyList(),
    val selectedGenre: GenreDTO? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val currentMovieSource: MovieSource = MovieSource.POPULAR
)

enum class MovieSource {
    POPULAR,
    SEARCH,
    GENRE
}