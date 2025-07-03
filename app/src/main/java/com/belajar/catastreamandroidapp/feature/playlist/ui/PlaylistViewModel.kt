package com.belajar.catastreamandroidapp.feature.playlist.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.catastreamandroidapp.core.ApiClient
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.CreatePlaylistRequest
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.PlaylistResponseDTO
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.UpdatePlaylistNameRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PlaylistScreenUiState(
    val playlists: List<PlaylistResponseDTO> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showEditNameDialog: Boolean = false,
    val playlistToEdit: PlaylistResponseDTO? = null,
    val showCreatePlaylistDialog: Boolean = false
)

class PlaylistViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistScreenUiState())
    val uiState: StateFlow<PlaylistScreenUiState> = _uiState.asStateFlow()

    private fun getFormattedToken(token: String) = if (token.startsWith("Bearer ")) token else "Bearer $token"

    fun fetchPlaylists(token: String) {
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Token tidak valid untuk mengambil playlist.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val fetchedPlaylists = ApiClient.playlistApiService.getMyPlaylists(getFormattedToken(token))
                _uiState.update { it.copy(playlists = fetchedPlaylists, isLoading = false) }
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Error fetching playlists: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal memuat playlists.", isLoading = false) }
            }
        }
    }

    fun createPlaylist(token: String, playlistName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                ApiClient.playlistApiService.createPlaylist( //
                    token = getFormattedToken(token),
                    req = CreatePlaylistRequest(playlistName) //
                )
                _uiState.update { it.copy(isLoading = false, successMessage = "Playlist '$playlistName' berhasil dibuat.") }
                fetchPlaylists(token)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val backendMessage = parseBackendError(errorBody)
                val msg = backendMessage ?: "Gagal membuat playlist (HTTP ${e.code()})"
                Log.e("PlaylistViewModel", "Error creating playlist: HTTP ${e.code()} - $errorBody")
                _uiState.update { it.copy(errorMessage = msg, isLoading = false) }
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Error creating playlist: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal membuat playlist.", isLoading = false) }
            }
        }
    }

    fun renamePlaylist(token: String, playlistId: Long, newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                ApiClient.playlistApiService.renamePlaylist( //
                    token = getFormattedToken(token),
                    playlistId = playlistId,
                    req = UpdatePlaylistNameRequest(newName = newName) //
                )
                _uiState.update { it.copy(isLoading = false, successMessage = "Nama playlist berhasil diubah menjadi '$newName'.") }
                fetchPlaylists(token)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val backendMessage = parseBackendError(errorBody)
                val msg = backendMessage ?: "Gagal mengubah nama playlist (HTTP ${e.code()})"
                Log.e("PlaylistViewModel", "Error renaming playlist: HTTP ${e.code()} - $errorBody")
                _uiState.update { it.copy(errorMessage = msg, isLoading = false) }
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Error renaming playlist: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal mengubah nama playlist.", isLoading = false) }
            }
        }
    }

    fun showEditDialog(playlist: PlaylistResponseDTO) { //
        _uiState.update { it.copy(showEditNameDialog = true, playlistToEdit = playlist, successMessage = null, errorMessage = null) }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(showEditNameDialog = false, playlistToEdit = null) } //
    }

    fun showCreatePlaylistDialog() {
        _uiState.update { it.copy(showCreatePlaylistDialog = true, successMessage = null, errorMessage = null) } //
    }

    fun dismissCreatePlaylistDialog() {
        _uiState.update { it.copy(showCreatePlaylistDialog = false) } //
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) } //
    }

    private fun parseBackendError(errorBody: String?): String? { //
        return try {
            errorBody?.let {
                if (it.contains("\"error\"")) {
                    it.split("\"error\":\"")[1].split("\"")[0]
                } else { null }
            }
        } catch (ex: Exception) { null }
    }
}