package com.belajar.catastreamandroidapp.feature.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belajar.catastreamandroidapp.feature.movie.data.dto.GenreDTO


@Composable
fun GenreFilter(
    genres: List<GenreDTO>,
    selectedGenre: GenreDTO?,
    onGenreSelected: (GenreDTO) -> Unit
) {
    LazyRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        items(genres) { genre ->
            FilterChip(
                selected = genre == selectedGenre,
                onClick = { onGenreSelected(genre) },
                label = { Text(genre.name) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
