package com.belajar.catastreamandroidapp.feature.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.belajar.catastreamandroidapp.core.ApiClient
import com.belajar.catastreamandroidapp.feature.user.data.dto.UserResponse
import com.belajar.catastreamandroidapp.feature.movie.ui.HomeScreen
import com.belajar.catastreamandroidapp.feature.playlist.ui.PlaylistScreen
import com.belajar.catastreamandroidapp.feature.playlist.ui.PlaylistDetailScreen
import com.belajar.catastreamandroidapp.feature.user.ui.ProfileScreen
import com.belajar.catastreamandroidapp.feature.movie.ui.MovieDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(token: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var userProfile by remember { mutableStateOf<UserResponse?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var profileError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            isLoadingProfile = true
            profileError = null
            try {
                val profileResponse = ApiClient.userApiService.getProfile("Bearer $token")
                if (profileResponse.isSuccessful) {
                    userProfile = profileResponse.body()
                } else {
                    profileError = "Gagal mengambil profil (HTTP ${profileResponse.code()})"
                }
            } catch (e: Exception) {
                Log.e("MainScreen", "Error fetching profile: ${e.message}", e)
                profileError = e.message ?: "Error profil tidak diketahui."
            }
            isLoadingProfile = false
        } else {
            profileError = "Token tidak tersedia untuk mengambil profil."
            isLoadingProfile = false
        }
    }


    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Beranda Film") },
                    label = { Text("Beranda") },
                    selected = currentRoute == "search" || currentRoute?.startsWith("movieDetail") == true,
                    onClick = {
                        navController.navigate("search") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Playlists") },
                    label = { Text("Playlists") },
                    selected = currentRoute == "playlists" || currentRoute?.startsWith("playlistDetail") == true,
                    onClick = {
                        navController.navigate("playlists") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == "profile",
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("search") {
                when {
                    isLoadingProfile -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    profileError != null -> {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: $profileError", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    else -> {
                        HomeScreen(
                            userName = userProfile?.username,
                            userPictureUrl = userProfile?.avatarUrl,
                            onLogout = onLogout,
                            onMovieClick = { movieSummary ->
                                val idToNavigate = movieSummary.tmdbId
                                Log.d("MainScreenNav", "Navigasi ke movieDetail/$idToNavigate dari HomeScreen.")
                                navController.navigate("movieDetail/$idToNavigate")
                            }
                        )
                    }
                }
            }

            composable("playlists") {
                PlaylistScreen(
                    token = token,
                    onPlaylistClick = { playlistId ->
                        Log.d("MainScreenNav", "Navigasi ke playlistDetail/$playlistId dari PlaylistScreen.")
                        navController.navigate("playlistDetail/$playlistId")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    token = token,
                    onLogout = onLogout,
                    onGoToPlaylist = {
                        navController.navigate("playlists") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = "movieDetail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId")
                if (movieId != null) {
                    MovieDetailScreen(
                        movieId = movieId,
                        token = token,
                        onNavigateUp = { navController.popBackStack() }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Movie ID tidak valid atau tidak ditemukan.")
                    }
                }
            }

            composable(
                route = "playlistDetail/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId")
                if (playlistId != null) {
                    PlaylistDetailScreen(
                        token = token,
                        playlistId = playlistId,
                        onBack = { navController.popBackStack() },
                        onMovieItemClick = { tmdbId ->
                            Log.d("MainScreenNav", "Navigasi ke movieDetail/$tmdbId dari PlaylistDetailScreen.")
                            navController.navigate("movieDetail/$tmdbId")
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Playlist ID tidak valid atau tidak ditemukan.")
                    }
                }
            }
        }
    }
}
