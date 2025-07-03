
package com.belajar.catastreamandroidapp.feature.movie.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO
import com.belajar.catastreamandroidapp.feature.ui.component.GenreFilter
import com.belajar.catastreamandroidapp.feature.ui.component.ShimmerLoadingGrid
import com.belajar.catastreamandroidapp.feature.ui.component.WelcomeSection

@Composable
fun HomeScreen(
    userName: String?,
    userPictureUrl: String?,
    onLogout: () -> Unit,
    onMovieClick: (MovieSummaryDTO) -> Unit,
    movieSearchViewModel: MovieSearchViewModel = viewModel()
) {
    val uiState by movieSearchViewModel.uiState.collectAsState()

    val gridState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleItemIndex >= totalItems - 5 && !uiState.isLoading && !uiState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore.value) {
            movieSearchViewModel.loadMoreMovies()
        }
    }

    Scaffold(
        topBar = {
            WelcomeSection(
                userName = userName,
                userPictureUrl = userPictureUrl,
                onLogout = onLogout
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            com.belajar.catastreamandroidapp.feature.ui.component.SearchBar(
                value = uiState.searchQuery,
                onValueChange = movieSearchViewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            GenreFilter(
                genres = uiState.genres,
                selectedGenre = uiState.selectedGenre,
                onGenreSelected = movieSearchViewModel::onGenreSelected
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.movies.isEmpty() -> {
                        ShimmerLoadingGrid()
                    }
                    uiState.errorMessage != null && uiState.movies.isEmpty() -> {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        MovieList(
                            movies = uiState.movies.map { movieResponseDTO ->
                                MovieSummaryDTO(
                                    id = 0L,
                                    tmdbId = movieResponseDTO.id,
                                    title = movieResponseDTO.title,
                                    posterPath = movieResponseDTO.posterPath ?: "",
                                    releaseDate = movieResponseDTO.releaseDate ?: "-",
                                    overview = movieResponseDTO.overview ?: "-",
                                    genreIds = movieResponseDTO.genreIds ?: emptyList()
                                )
                            },
                            onMovieClick = onMovieClick,
                            gridState = gridState,
                            isLoadingMore = uiState.isLoadingMore
                        )
                    }
                }
            }
        }
    }
}