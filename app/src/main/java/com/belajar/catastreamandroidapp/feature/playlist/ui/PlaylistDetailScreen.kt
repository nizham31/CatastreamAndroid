package com.belajar.catastreamandroidapp.feature.playlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.R
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.AddMovieToPlaylistRequest
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    token: String,
    playlistId: Long,
    onBack: () -> Unit,
    onMovieItemClick: (tmdbId: Int) -> Unit,
    playlistDetailViewModel: PlaylistDetailViewModel = viewModel()
) {
    val uiState by playlistDetailViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(playlistId, token) {
        playlistDetailViewModel.fetchPlaylistDetails(token, playlistId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { message: String ->
            scope.launch { snackbarHostState.showSnackbar(message = message) }
            playlistDetailViewModel.clearMessages()
        }
        uiState.successMessage?.let { message: String ->
            scope.launch { snackbarHostState.showSnackbar(message = message) }
            playlistDetailViewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PlaylistDetailTopAppBar(
                playlistName = uiState.playlist?.name ?: "Detail Playlist",
                onBack = onBack,
                onAddMovie = { playlistDetailViewModel.showAddMovieDialog() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.playlist == null -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null && uiState.playlist == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error", modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                uiState.playlist != null -> {
                    val currentPlaylist = uiState.playlist!!
                    val moviesToDisplay: List<MovieSummaryDTO> = currentPlaylist.itemsPreview ?: emptyList()

                    if (moviesToDisplay.isEmpty() && !uiState.isLoading) {
                        EmptyMovieState(
                            playlistName = currentPlaylist.name,
                            onAddClick = { playlistDetailViewModel.showAddMovieDialog() }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                PlaylistInfoHeader(
                                    itemCount = currentPlaylist.itemCount,
                                    owner = currentPlaylist.user?.username
                                )
                            }
                            items(
                                items = moviesToDisplay,
                                key = { movie: MovieSummaryDTO -> movie.id }
                            ) { movie: MovieSummaryDTO ->
                                MoviePlaylistItem(
                                    movie = movie,
                                    onMovieClick = {
                                        movie.tmdbId?.let { tmdbIdValue: Int -> onMovieItemClick(tmdbIdValue) }
                                    },
                                    onDeleteClick = {
                                        playlistDetailViewModel.showDeleteConfirmation(movie)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddMovieDialog) {
        AddMovieManuallyDialog(
            onDismiss = { playlistDetailViewModel.dismissAddMovieDialog() },
            onConfirm = { tmdbId: Int, title: String, poster: String, release: String, overview: String ->
                val request = AddMovieToPlaylistRequest(tmdbId, title, poster.ifBlank { null }, release.ifBlank { null }, overview.ifBlank { null })
                playlistDetailViewModel.addMovieManually(token, playlistId, request)
            }
        )
    }

    uiState.movieToDelete?.let { movie: MovieSummaryDTO ->
        DeleteConfirmationDialog(
            movie = movie,
            onDismiss = { playlistDetailViewModel.dismissDeleteConfirmation() },
            onConfirm = { playlistDetailViewModel.removeMovieFromPlaylist(token, playlistId, movie) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailTopAppBar(playlistName: String, onBack: () -> Unit, onAddMovie: () -> Unit) {
    TopAppBar(
        title = { Text(playlistName, maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            IconButton(onClick = onAddMovie) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Film Manual")
            }
        }
    )
}

@Composable
private fun PlaylistInfoHeader(itemCount: Int, owner: String?) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text("Total $itemCount film", style = MaterialTheme.typography.titleMedium)
        owner?.let {
            Text("Milik: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MoviePlaylistItem(
    movie: MovieSummaryDTO,
    onMovieClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMovieClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterPath?.takeIf { it.isNotBlank() }?.let { "https://image.tmdb.org/t/p/w185$it" }
                    ?: painterResource(id = R.drawable.ic_launcher_background), // Ganti dengan placeholder Anda
                contentDescription = movie.title,
                modifier = Modifier.width(60.dp).height(90.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium)
                movie.releaseDate?.takeIf { it.isNotBlank() }?.let {
                    Text("Rilis: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus Film")
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(movie: MovieSummaryDTO, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Film") },
        text = { Text("Yakin ingin menghapus \"${movie.title}\" dari playlist ini?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Hapus") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMovieManuallyDialog(
    onDismiss: () -> Unit,
    onConfirm: (tmdbId: Int, title: String, posterPath: String, releaseDate: String, overview: String) -> Unit
) {
    var tmdbIdState by remember { mutableStateOf("") }
    var titleState by remember { mutableStateOf("") }
    var posterPathState by remember { mutableStateOf("") }
    var releaseDateState by remember { mutableStateOf("") }
    var overviewState by remember { mutableStateOf("") }
    val isInputValid by remember(tmdbIdState, titleState) {
        derivedStateOf { tmdbIdState.toIntOrNull() != null && tmdbIdState.isNotBlank() && titleState.isNotBlank() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Film Manual") },
        text = {
            LazyColumn {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = tmdbIdState, onValueChange = { tmdbIdState = it }, label = { Text("TMDB Movie ID*") }, isError = tmdbIdState.isNotBlank() && tmdbIdState.toIntOrNull() == null, singleLine = true)
                        OutlinedTextField(value = titleState, onValueChange = { titleState = it }, label = { Text("Judul Film*") }, singleLine = true)
                        OutlinedTextField(value = posterPathState, onValueChange = { posterPathState = it }, label = { Text("Poster Path (opsional)") }, singleLine = true)
                        OutlinedTextField(value = releaseDateState, onValueChange = { releaseDateState = it }, label = { Text("Tgl Rilis (YYYY-MM-DD, ops.)") }, singleLine = true)
                        OutlinedTextField(value = overviewState, onValueChange = { overviewState = it }, label = { Text("Overview (opsional)") }, maxLines = 3)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(tmdbIdState.toInt(), titleState, posterPathState, releaseDateState, overviewState) },
                enabled = isInputValid
            ) { Text("Tambah") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
private fun EmptyMovieState(playlistName: String, onAddClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text("Playlist '$playlistName' Kosong", style = MaterialTheme.typography.titleLarge)
            Text("Cari film dan tambahkan ke playlist ini untuk memulai koleksi Anda.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Tambah Film Manual")
            }
        }
    }
}