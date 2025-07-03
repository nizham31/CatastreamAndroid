package com.belajar.catastreamandroidapp.feature.movie.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO

@Composable
fun MovieListItem(movie: MovieSummaryDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium)
                movie.posterPath?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                Text(movie.overview, maxLines = 2, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
