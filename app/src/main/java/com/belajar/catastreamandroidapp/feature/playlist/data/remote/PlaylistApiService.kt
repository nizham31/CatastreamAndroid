package com.belajar.catastreamandroidapp.feature.playlist.data.remote

import com.belajar.catastreamandroidapp.feature.playlist.data.dto.AddMovieToPlaylistRequest
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.CreatePlaylistRequest
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.PlaylistResponseDTO
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.UpdatePlaylistNameRequest
import retrofit2.http.*

interface PlaylistApiService {

    @GET("api/playlists/me")
    suspend fun getMyPlaylists(@Header("Authorization") token: String): List<PlaylistResponseDTO>

    @POST("api/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Body req: CreatePlaylistRequest
    ): PlaylistResponseDTO

    @PUT("api/playlists/{playlistId}")
    suspend fun renamePlaylist(
        @Header("Authorization") token: String,
        @Path("playlistId") playlistId: Long,
        @Body req: UpdatePlaylistNameRequest
    ): PlaylistResponseDTO


    @GET("api/playlists/{id}")
    suspend fun getPlaylistDetail(
        @Header("Authorization") token: String,
        @Path("id") playlistId: Long
    ): PlaylistResponseDTO

    @DELETE("api/playlists/{id}")
    suspend fun deletePlaylist(
        @Header("Authorization") token: String,
        @Path("id") playlistId: Long
    )

    @POST("api/playlists/{playlistId}/movies")
    suspend fun addMovieToPlaylist(
        @Header("Authorization") token: String,
        @Path("playlistId") playlistId: Long,
        @Body request: AddMovieToPlaylistRequest
    ): PlaylistResponseDTO
    // PlaylistApiService.kt
    @DELETE("api/playlists/{playlistId}/movies/{localMovieId}")
    suspend fun removeMovieFromPlaylist(
        @Header("Authorization") token: String,
        @Path("playlistId") playlistId: Long,
        @Path("localMovieId") localMovieId: Long
    ): retrofit2.Response<Void>


}
