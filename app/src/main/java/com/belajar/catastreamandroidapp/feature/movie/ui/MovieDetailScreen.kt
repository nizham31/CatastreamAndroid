package com.belajar.catastreamandroidapp.feature.movie.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.belajar.catastreamandroidapp.feature.movie.ui.detail.MovieDetailViewModel
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.PlaylistResponseDTO
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int, //
    token: String, //
    movieDetailViewModel: MovieDetailViewModel = viewModel(), //
    onNavigateUp: () -> Unit //
) {
    val uiState = movieDetailViewModel.uiState //
    val snackbarHostState = remember { SnackbarHostState() } //
    val scope = rememberCoroutineScope() //

    val sheetState = rememberModalBottomSheetState()
    var showPlaylistSheet by remember { mutableStateOf(false) }

    LaunchedEffect(movieId, token) {
        movieDetailViewModel.loadData(movieId, token) //
    }

    LaunchedEffect(uiState.addToPlaylistSuccess, uiState.addToPlaylistError) {
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, //
        topBar = {
            TopAppBar(
                title = { Text(uiState.movieDetail?.title ?: "Detail Film", maxLines = 1) }, //
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) { //
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali") //
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.movieDetail != null && !uiState.isLoadingMovieDetail) { //
                FloatingActionButton(onClick = { showPlaylistSheet = true }) { // DIUBAH: untuk menampilkan bottom sheet
                    Icon(Icons.Filled.Add, contentDescription = "Simpan ke Playlist") //
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoadingMovieDetail && uiState.movieDetail == null -> { /* ... */ } //
                uiState.movieDetailError != null && uiState.movieDetail == null -> { /* ... */ } //
                uiState.movieDetail != null -> {
                    val movie = uiState.movieDetail!! //
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()) //
                            .padding(bottom = 80.dp) // Beri ruang untuk FAB
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath ?: movie.posterPath}", //
                            contentDescription = movie.title, //
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f/9f), // Gunakan aspect ratio untuk gambar backdrop
                            contentScale = ContentScale.Crop //
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(movie.title, style = MaterialTheme.typography.headlineMedium) //
                            Spacer(modifier = Modifier.height(16.dp))

                            // --- TAMPILAN INFO YANG DIPERBAIKI ---
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InfoChip(
                                    icon = Icons.Default.Star,
                                    text = "${String.format("%.1f", movie.voteAverage ?: 0.0)} / 10" //
                                )
                                InfoChip(
                                    icon = Icons.Default.ThumbUp,
                                    text = "${movie.runtime?.toString() ?: "-"} menit" //
                                )
                                InfoChip(
                                    icon = Icons.Default.Star,
                                    text = movie.releaseDate ?: "-" //
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Genre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) //
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp) //
                            ) {
                                movie.genres?.forEach { genre -> //
                                    AssistChip(onClick = { /* No action */ }, label = { Text(genre.name) }) //
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Sinopsis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) //
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(movie.overview ?: "Tidak ada sinopsis.", style = MaterialTheme.typography.bodyLarge) //
                        }
                    }
                }
            }
        }
    }

    // --- MODAL BOTTOM SHEET UNTUK PEMILIHAN PLAYLIST ---
    if (showPlaylistSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetState,
        ) {
            PlaylistSelectionContent(
                playlists = uiState.userPlaylists,
                onPlaylistSelected = { selectedPlaylistId ->
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showPlaylistSheet = false
                        movieDetailViewModel.addMovieToPlaylist(
                            token = token,
                            playlistId = selectedPlaylistId,
                            movie = uiState.movieDetail
                        )
                    }
                },
                isLoading = uiState.isLoadingPlaylists,
                error = uiState.fetchPlaylistsError,
                onAddPlaylist = { /* TODO: Navigasi ke halaman buat playlist */ }
            )
        }
    }
}

// Composable baru untuk konten bottom sheet
@Composable
fun PlaylistSelectionContent(
    playlists: List<PlaylistResponseDTO>,
    onPlaylistSelected: (Long) -> Unit,
    isLoading: Boolean,
    error: String?,
    onAddPlaylist: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
        Text("Simpan ke Playlist", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() //
                }
            }
            error != null -> {
                Text("Gagal memuat playlist: $error", color = MaterialTheme.colorScheme.error) //
            }
            playlists.isEmpty() -> {
                Text("Anda belum punya playlist. Buat satu untuk menyimpan film.") //
                Spacer(Modifier.height(16.dp))
                Button(onClick = onAddPlaylist, modifier = Modifier.fillMaxWidth()) {
                    Text("Buat Playlist Baru")
                }
            }
            else -> {
                LazyColumn {
                    items(playlists) { playlist -> //
                        TextButton(
                            onClick = { onPlaylistSelected(playlist.id) }, //
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(16.dp))
                                Text(playlist.name, textAlign = TextAlign.Start) //
                            }
                        }
                    }
                }
            }
        }
    }
}


// Composable kecil untuk menampilkan info dengan ikon
@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}