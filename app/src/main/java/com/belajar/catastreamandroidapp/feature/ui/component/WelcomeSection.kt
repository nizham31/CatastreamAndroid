package com.belajar.catastreamandroidapp.feature.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun WelcomeSection(userName: String?, userPictureUrl: String?, onLogout: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (userPictureUrl != null) {
                AsyncImage(
                    model = userPictureUrl,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(text = "Hi, ${userName ?: "User"}", style = MaterialTheme.typography.titleMedium)
        }
        Button(onClick = onLogout) { Text("Logout") }
    }
}
