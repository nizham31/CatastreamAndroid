package com.belajar.catastreamandroidapp.feature.movie.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO
import com.belajar.catastreamandroidapp.feature.ui.component.MovieGridItem

@Composable
fun MovieList(
    movies: List<MovieSummaryDTO>,
    onMovieClick: (MovieSummaryDTO) -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    isLoadingMore: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = modifier,
        state = gridState,
        contentPadding = PaddingValues(16.dp), //
        horizontalArrangement = Arrangement.spacedBy(16.dp), //
        verticalArrangement = Arrangement.spacedBy(16.dp) //
    ) {
        items(
            items = movies,
            key = { movie -> movie.tmdbId ?: movie.hashCode() }
        ) { movie ->
            MovieGridItem(movie = movie, onClick = { onMovieClick(movie) }) //
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth() //
                        .padding(vertical = 16.dp), //
                    contentAlignment = Alignment.Center //
                ) {
                    CircularProgressIndicator() //
                }
            }
        }
    }
}