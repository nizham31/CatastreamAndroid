// File: com.belajar.catastreamandroidapp.feature.ui.component.ShimmerLoadingGrid.kt

package com.belajar.catastreamandroidapp.feature.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerLoadingGrid() {

    val shimmerModifier = Modifier.shimmer()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(12) {
            Box(
                modifier = shimmerModifier
                    .aspectRatio(2f / 3f)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
        }
    }
}