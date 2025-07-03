// File: com.belajar.catastreamandroidapp/feature/movie/ui/MovieSearchViewModel.kt

package com.belajar.catastreamandroidapp.feature.movie.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.catastreamandroidapp.core.RetrofitClient
import com.belajar.catastreamandroidapp.feature.movie.data.dto.GenreDTO
import com.belajar.catastreamandroidapp.feature.movie.data.dto.MovieResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class MovieSearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MovieSearchUiState())
    val uiState = _uiState.asStateFlow()

    // Kunci API untuk TMDB
    private val tmdbApiKey = "1fcbebcfe0bedc39903831c5a661389c"

    private var fetchJob: Job? = null

    // Flow ini hanya bertugas sebagai pemicu (trigger) untuk pencarian setelah jeda
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        // Ambil data yang dibutuhkan saat ViewModel pertama kali dibuat
        fetchGenres()
        fetchMoviesBasedOnCurrentState()

        // Siapkan pendengar untuk query pencarian
        _searchQueryFlow
            .debounce(500L) // Tunggu 500md setelah user berhenti mengetik
            .distinctUntilChanged() // Hanya proses jika teksnya benar-benar baru
            .onEach {
                // Setelah jeda, panggil fungsi fetch.
                // Fungsi akan membaca query terbaru dari uiState.
                Log.d("MovieSearchVM", "Debounce selesai, memulai fetch untuk query: '${_uiState.value.searchQuery}'")
                _uiState.update { it.copy(currentPage = 1) } // Selalu reset halaman ke 1 untuk pencarian baru
                fetchMoviesBasedOnCurrentState(isNewSearchOrFilter = true)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Mengambil daftar semua genre film dari API.
     */
    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val genreResponse = RetrofitClient.tmdbApiService.getGenres(apiKey = tmdbApiKey) //
                _uiState.update { it.copy(genres = genreResponse.genres) }
            } catch (e: Exception) {
                Log.e("MovieSearchVM", "Error fetching genres: ${e.message}")
                _uiState.update { it.copy(errorMessage = "Gagal memuat genre.") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val newSource = if (query.isNotBlank()) MovieSource.SEARCH else MovieSource.POPULAR
        _uiState.update { it.copy(searchQuery = query, currentMovieSource = newSource) }

        _searchQueryFlow.value = query
    }
    fun onGenreSelected(genre: GenreDTO?) {
        val currentGenre = _uiState.value.selectedGenre
        val newGenre = if (currentGenre == genre) null else genre // Logika untuk toggle
        val newSource = if (newGenre != null) MovieSource.GENRE else MovieSource.POPULAR
        _uiState.update { it.copy(selectedGenre = newGenre, currentPage = 1, currentMovieSource = newSource) }
        fetchMoviesBasedOnCurrentState(isNewSearchOrFilter = true)
    }
    fun fetchMoviesBasedOnCurrentState(isNewSearchOrFilter: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val pageToFetch = if (isNewSearchOrFilter) 1 else _uiState.value.currentPage

                val response: MovieResponse = when (_uiState.value.currentMovieSource) {
                    MovieSource.SEARCH ->
                        RetrofitClient.tmdbApiService.searchMovies( //
                            apiKey = tmdbApiKey,
                            query = _uiState.value.searchQuery,
                            page = pageToFetch
                        )
                    MovieSource.GENRE ->
                        RetrofitClient.tmdbApiService.discoverMoviesByGenre( //
                            apiKey = tmdbApiKey,
                            genreId = _uiState.value.selectedGenre!!.id.toString(),
                            page = pageToFetch
                        )
                    MovieSource.POPULAR ->
                        RetrofitClient.tmdbApiService.getPopularMovies( //
                            apiKey = tmdbApiKey,
                            page = pageToFetch
                        )
                }

                val filteredResults = response.results.filter { !it.posterPath.isNullOrBlank() } //

                val newMovies = if (isNewSearchOrFilter || pageToFetch == 1) {
                    filteredResults
                } else {
                    _uiState.value.movies + filteredResults
                }

                _uiState.update {
                    it.copy(
                        movies = newMovies.distinctBy { mov -> mov.id }, //
                        isLoading = false,
                        currentPage = response.page, //
                        totalPages = response.totalPages, //
                    )
                }

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("MovieSearchVM", "Error fetching movies: ${e.message}", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat film.") }
                }
            }
        }
    }

    fun loadMoreMovies() {
        if (_uiState.value.isLoadingMore || _uiState.value.isLoading || _uiState.value.currentPage >= _uiState.value.totalPages) {
            return
        }

        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = _uiState.value.currentPage + 1

            try {
                val response: MovieResponse = when (_uiState.value.currentMovieSource) {
                    MovieSource.SEARCH -> RetrofitClient.tmdbApiService.searchMovies(apiKey = tmdbApiKey, query = _uiState.value.searchQuery, page = nextPage) //
                    MovieSource.GENRE -> RetrofitClient.tmdbApiService.discoverMoviesByGenre(apiKey = tmdbApiKey, genreId = _uiState.value.selectedGenre!!.id.toString(), page = nextPage) //
                    MovieSource.POPULAR -> RetrofitClient.tmdbApiService.getPopularMovies(apiKey = tmdbApiKey, page = nextPage) //
                }

                val filteredResults = response.results.filter { !it.posterPath.isNullOrBlank() } //
                val combinedMovies = _uiState.value.movies + filteredResults

                _uiState.update {
                    it.copy(
                        movies = combinedMovies.distinctBy { mov -> mov.id }, //
                        isLoadingMore = false,
                        currentPage = response.page, //
                        totalPages = response.totalPages //
                    )
                }
            } catch (e: Exception) {
                Log.e("MovieSearchVM", "Error loading more movies: ${e.message}", e)
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    /**
     * Menghapus pesan error dari state.
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}