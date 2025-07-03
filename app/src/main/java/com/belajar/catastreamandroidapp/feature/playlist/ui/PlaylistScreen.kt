package com.belajar.catastreamandroidapp.feature.playlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.PlaylistResponseDTO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    token: String,
    onPlaylistClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel()
) {
    val uiState by playlistViewModel.uiState.collectAsState() // Ini membutuhkan import getValue dan collectAsState

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            playlistViewModel.fetchPlaylists(token)
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { message: String ->
            scope.launch { snackbarHostState.showSnackbar(message = message) }
            playlistViewModel.clearMessages()
        }
        uiState.successMessage?.let { message: String ->
            scope.launch { snackbarHostState.showSnackbar(message = message) }
            playlistViewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { playlistViewModel.showCreatePlaylistDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Playlist Baru")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "My Playlists",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            when {
                uiState.isLoading && uiState.playlists.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.playlists.isEmpty() && !uiState.isLoading -> {
                    EmptyPlaylistState(onCreateClick = { playlistViewModel.showCreatePlaylistDialog() })
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.playlists,
                            key = { playlist: PlaylistResponseDTO -> playlist.id }
                        ) { playlist: PlaylistResponseDTO ->
                            PlaylistItemCard(
                                playlist = playlist,
                                onPlaylistClick = { onPlaylistClick(playlist.id) },
                                onEditClick = { playlistViewModel.showEditDialog(playlist) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { playlistViewModel.dismissCreatePlaylistDialog() },
            onConfirm = { name: String ->
                playlistViewModel.createPlaylist(token, name)
            }
        )
    }

    uiState.playlistToEdit?.let { playlistBeingEdited: PlaylistResponseDTO ->
        if (uiState.showEditNameDialog) {
            EditPlaylistNameDialog(
                playlist = playlistBeingEdited,
                onDismiss = { playlistViewModel.dismissEditDialog() },
                onConfirm = { newName: String ->
                    playlistViewModel.renamePlaylist(token, playlistBeingEdited.id, newName)
                }
            )
        }
    }
}

@Composable
private fun PlaylistItemCard(
    playlist: PlaylistResponseDTO,
    onPlaylistClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onPlaylistClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                val previews = playlist.itemsPreview?.take(4) ?: emptyList()
                if (previews.isNotEmpty()) {
                    previews.forEach { movie ->
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w300${movie.posterPath}",
                            contentDescription = null,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    val remainingSlots = 4 - previews.size
                    if (remainingSlots > 0) {
                        repeat(remainingSlots) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = "Empty Playlist",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 100f))
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = playlist.name, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "${playlist.itemCount} film", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Nama Playlist", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaylistState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(text = "Playlist Anda Kosong", style = MaterialTheme.typography.titleLarge)
            Text(text = "Ayo buat playlist pertama Anda untuk menyimpan film-film favorit.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onCreateClick) {
                Text("Buat Playlist Baru")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Playlist Baru") },
        text = {
            OutlinedTextField(value = playlistName, onValueChange = { playlistName = it }, label = { Text("Nama Playlist") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(
                onClick = { if (playlistName.isNotBlank()) { onConfirm(playlistName) } },
                enabled = playlistName.isNotBlank()
            ) { Text("Buat") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPlaylistNameDialog(
    playlist: PlaylistResponseDTO,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember(playlist.name) { mutableStateOf(playlist.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nama Playlist") },
        text = {
            OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Nama Playlist Baru") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank() && newName != playlist.name) {
                        onConfirm(newName)
                    } else if (newName == playlist.name) {
                        onDismiss()
                    }
                },
                enabled = newName.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}