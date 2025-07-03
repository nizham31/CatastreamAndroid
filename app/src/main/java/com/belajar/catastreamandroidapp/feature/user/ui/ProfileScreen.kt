package com.belajar.catastreamandroidapp.feature.user.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.core.ApiClient
import com.belajar.catastreamandroidapp.feature.user.data.dto.UserResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String,
    onLogout: () -> Unit,
    onGoToPlaylist: () -> Unit
) {
    var userProfile by remember { mutableStateOf<UserResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            isLoading = true
            errorMessage = null
            try {
                // Pastikan token diformat dengan benar
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = ApiClient.userApiService.getProfile(formattedToken)
                if (response.isSuccessful) {
                    userProfile = response.body()
                } else {
                    errorMessage = "Gagal mengambil profil (HTTP ${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan saat mengambil profil."
            }
            isLoading = false
        } else {
            errorMessage = "Token tidak valid."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                userProfile != null -> {
                    val user = userProfile!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar User
                        AsyncImage(
                            model = user.avatarUrl ?: "https://via.placeholder.com/150.png?text=No+Avatar",
                            contentDescription = "Avatar ${user.username}",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email
                        ProfileInfoRow(
                            icon = Icons.Filled.Email,
                            text = user.email
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bio
                        ProfileInfoRow(
                            icon = Icons.Filled.Info,
                            text = user.bio ?: "Belum ada bio."
                        )

                        Divider(modifier = Modifier.padding(vertical = 24.dp))

                        // Tombol Aksi
                        Button(
                            onClick = onGoToPlaylist,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Playlist Saya")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Logout")
                        }
                    }
                }
                else -> {
                    Text("Tidak dapat memuat data profil.", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}