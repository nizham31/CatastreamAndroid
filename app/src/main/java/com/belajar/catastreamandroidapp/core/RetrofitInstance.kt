package com.belajar.catastreamandroidapp.core

import com.belajar.catastreamandroidapp.feature.playlist.data.remote.PlaylistApiService
import com.belajar.catastreamandroidapp.feature.user.data.remote.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object ApiClient {
    private const val BASE_URL = "http://rather-blogger.gl.at.ply.gg:28663/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
    val playlistApiService: PlaylistApiService = retrofit.create(PlaylistApiService::class.java)
}
