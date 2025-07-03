// File: com.belajar.catastreamandroidapp.feature.playlist.ui.PlaylistDetailViewModel.kt

package com.belajar.catastreamandroidapp.feature.playlist.ui

import android.util.Log
// HAPUS: import androidx.compose.runtime.getValue
// HAPUS: import androidx.compose.runtime.mutableStateOf
// HAPUS: import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.catastreamandroidapp.core.ApiClient
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.*
import kotlinx.coroutines.flow.MutableStateFlow // <-- TAMBAHKAN IMPORT INI
import kotlinx.coroutines.flow.StateFlow // <-- TAMBAHKAN IMPORT INI
import kotlinx.coroutines.flow.asStateFlow // <-- TAMBAHKAN IMPORT INI
import kotlinx.coroutines.flow.update // <-- TAMBAHKAN IMPORT INI
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PlaylistDetailUiState(
    val playlist: PlaylistResponseDTO? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAddMovieDialog: Boolean = false,
    val movieToDelete: MovieSummaryDTO? = null,
)

class PlaylistDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private fun getFormattedToken(token: String) = if (token.startsWith("Bearer ")) token else "Bearer $token"

    fun fetchPlaylistDetails(token: String, playlistId: Long) {
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Token tidak valid.", isLoading = false) } //
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) } //
            try {
                val fetchedPlaylist = ApiClient.playlistApiService.getPlaylistDetail(getFormattedToken(token), playlistId)
                _uiState.update { it.copy(playlist = fetchedPlaylist, isLoading = false) } //
            } catch (e: Exception) {
                Log.e("PlaylistDetailVM", "Error fetching details: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal memuat detail playlist.", isLoading = false) } //
            }
        }
    }

    fun removeMovieFromPlaylist(token: String, playlistId: Long, movie: MovieSummaryDTO) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } //
            try {
                ApiClient.playlistApiService.removeMovieFromPlaylist(
                    token = getFormattedToken(token),
                    playlistId = playlistId,
                    localMovieId = movie.id
                )
                _uiState.update { it.copy(isLoading = false, successMessage = "'${movie.title}' telah dihapus.") } //
                fetchPlaylistDetails(token, playlistId)
            } catch (e: Exception) {
                Log.e("PlaylistDetailVM", "Error removing movie: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal menghapus film.") } //
            } finally {
                dismissDeleteConfirmation()
            }
        }
    }

    fun addMovieManually(token: String, playlistId: Long, request: AddMovieToPlaylistRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } //
            try {
                val updatedPlaylist = ApiClient.playlistApiService.addMovieToPlaylist(getFormattedToken(token), playlistId, request)
                _uiState.update { //
                    it.copy(
                        isLoading = false,
                        playlist = updatedPlaylist,
                        showAddMovieDialog = false,
                        successMessage = "'${request.title}' berhasil ditambahkan."
                    )
                }
            } catch (e: Exception) {
                Log.e("PlaylistDetailVM", "Error adding movie: ${e.message}", e)
                val errorMsg = if (e is HttpException && e.code() == 409) {
                    "Film sudah ada di playlist ini."
                } else {
                    e.message ?: "Gagal menambahkan film."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) } //
            }
        }
    }

    fun showAddMovieDialog() { _uiState.update { it.copy(showAddMovieDialog = true) } }
    fun dismissAddMovieDialog() { _uiState.update { it.copy(showAddMovieDialog = false) } }
    fun showDeleteConfirmation(movie: MovieSummaryDTO) { _uiState.update { it.copy(movieToDelete = movie) } }
    fun dismissDeleteConfirmation() { _uiState.update { it.copy(movieToDelete = null) } }
    fun clearMessages() { _uiState.update { it.copy(errorMessage = null, successMessage = null) } }
}