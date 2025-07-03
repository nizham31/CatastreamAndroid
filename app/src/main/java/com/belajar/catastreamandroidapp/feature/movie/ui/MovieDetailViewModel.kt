// File: com.belajar.catastreamandroidapp.feature.movie.ui.detail.MovieDetailViewModel.kt
package com.belajar.catastreamandroidapp.feature.movie.ui.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.catastreamandroidapp.core.ApiClient
import com.belajar.catastreamandroidapp.core.RetrofitClient
import com.belajar.catastreamandroidapp.feature.movie.data.dto.MovieDetailDTO
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.AddMovieToPlaylistRequest
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.PlaylistResponseDTO
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class MovieDetailUiState(

    // State untuk detail film
    val movieDetail: MovieDetailDTO? = null,
    val isLoadingMovieDetail: Boolean = false,
    val movieDetailError: String? = null,

    // State untuk daftar playlist pengguna
    val userPlaylists: List<PlaylistResponseDTO> = emptyList(),
    val isLoadingPlaylists: Boolean = false,
    val fetchPlaylistsError: String? = null,

    // State untuk menambah ke playlist
    val isAddingToPlaylist: Boolean = false,
    val addToPlaylistSuccess: Boolean = false,
    val addToPlaylistError: String? = null
)

class MovieDetailViewModel : ViewModel() {

    var uiState by mutableStateOf(MovieDetailUiState())
        private set

    private val tmdbApiKey = "1fcbebcfe0bedc39903831c5a661389c"

    fun loadData(movieId: Int, token: String) {
        fetchMovieDetail(movieId)
        fetchUserPlaylists(token)
    }

    private fun fetchMovieDetail(movieId: Int) {
        Log.d("MovieDetailVM", "Fetching detail for movieId: $movieId")
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoadingMovieDetail = true,
                movieDetailError = null,
                isAddingToPlaylist = false,
                addToPlaylistSuccess = false,
                addToPlaylistError = null
            )
            try {
                val detail = RetrofitClient.tmdbApiService.getMovieDetail(
                    movieId = movieId,
                    apiKey = tmdbApiKey
                )
                uiState = uiState.copy(movieDetail = detail, isLoadingMovieDetail = false)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("MovieDetailVM", "HttpException fetching movie detail: code=${e.code()}, message=${e.message()}, errorBody=$errorBody")
                val tmdbMsg = parseTmdbErrorStatus(errorBody)
                uiState = uiState.copy(movieDetailError = "HTTP ${e.code()}: ${tmdbMsg ?: "Gagal memuat detail."}", isLoadingMovieDetail = false)
            } catch (e: Exception) {
                Log.e("MovieDetailVM", "Exception fetching movie detail: ${e.message}", e)
                uiState = uiState.copy(movieDetailError = e.message ?: "Terjadi kesalahan.", isLoadingMovieDetail = false)
            }
        }
    }

    private fun fetchUserPlaylists(token: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingPlaylists = true, fetchPlaylistsError = null)
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            try {
                val playlists = ApiClient.playlistApiService.getMyPlaylists(formattedToken)
                uiState = uiState.copy(userPlaylists = playlists, isLoadingPlaylists = false)
                if (playlists.isEmpty()) {
                    Log.i("MovieDetailVM", "Pengguna belum memiliki playlist.")
                }
            } catch (e: HttpException) {
                Log.e("MovieDetailVM", "HttpException fetching playlists: code=${e.code()}, message=${e.message()}")
                uiState = uiState.copy(fetchPlaylistsError = "Gagal memuat playlist (HTTP ${e.code()}).", isLoadingPlaylists = false)
            } catch (e: Exception) {
                Log.e("MovieDetailVM", "Exception fetching playlists: ${e.message}", e)
                uiState = uiState.copy(fetchPlaylistsError = e.message ?: "Kesalahan memuat playlist.", isLoadingPlaylists = false)
            }
        }
    }

    fun addMovieToPlaylist(
        token: String,
        playlistId: Long,
        movie: MovieDetailDTO?
    ) {
        if (movie == null) {
            uiState = uiState.copy(addToPlaylistError = "Data film tidak tersedia.")
            return
        }
        if (playlistId <= 0L) {
            uiState = uiState.copy(addToPlaylistError = "ID Playlist tidak valid.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isAddingToPlaylist = true, addToPlaylistError = null, addToPlaylistSuccess = false)
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            try {
                val requestBody = AddMovieToPlaylistRequest(
                    tmdbMovieId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    overview = movie.overview
                )
                val response = ApiClient.playlistApiService.addMovieToPlaylist(
                    token = formattedToken,
                    playlistId = playlistId,
                    request = requestBody
                )
                Log.d("MovieDetailVM", "Berhasil menambahkan '${movie.title}' ke playlist '${response.name}' (ID: ${response.id})")
                uiState = uiState.copy(isAddingToPlaylist = false, addToPlaylistSuccess = true)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("MovieDetailVM", "HttpException adding to playlist: code=${e.code()}, message=${e.message()}, errorBody=$errorBody")
                val backendMessage = parseBackendError(errorBody)
                val errorMessage = when (e.code()) {
                    401 -> "Akses ditolak. Silakan login ulang."
                    403 -> "Anda tidak memiliki izin untuk playlist ini."
                    404 -> "Playlist tidak ditemukan."
                    409 -> backendMessage ?: "Film sudah ada di playlist ini." // Dari MovieAlreadyExistsInPlaylistException
                    else -> backendMessage ?: "Gagal menambahkan (HTTP ${e.code()})."
                }
                uiState = uiState.copy(addToPlaylistError = errorMessage, isAddingToPlaylist = false)
            } catch (e: Exception) {
                Log.e("MovieDetailVM", "Exception adding to playlist: ${e.message}", e)
                uiState = uiState.copy(addToPlaylistError = e.message ?: "Terjadi kesalahan.", isAddingToPlaylist = false)
            }
        }
    }

    fun resetAddToPlaylistStatus() {
        uiState = uiState.copy(
            isAddingToPlaylist = false,
            addToPlaylistSuccess = false,
            addToPlaylistError = null
        )
    }

    private fun parseTmdbErrorStatus(errorBody: String?): String? {
        return try {
            errorBody?.let {
                if (it.contains("status_message")) {
                    it.split("\"status_message\":\"")[1].split("\"")[0]
                } else { null }
            }
        } catch (ex: Exception) { null }
    }

    private fun parseBackendError(errorBody: String?): String? {
        return try {
            errorBody?.let {
                if (it.contains("\"error\"")) {
                    it.split("\"error\":\"")[1].split("\"")[0]
                } else { null }
            }
        } catch (ex: Exception) { null }
    }
}