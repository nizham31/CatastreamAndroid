// File: com.belajar.catastreamandroidapp.feature.ui.component.MovieGridItem.kt

package com.belajar.catastreamandroidapp.feature.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO

@Composable
fun MovieGridItem(movie: MovieSummaryDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f) // Rasio standar poster film
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}", //
            contentDescription = movie.title, //
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop // Memastikan gambar memenuhi card
        )
    }
}