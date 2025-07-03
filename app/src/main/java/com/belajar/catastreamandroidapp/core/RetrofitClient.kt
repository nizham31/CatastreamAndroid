package com.belajar.catastreamandroidapp.core

import com.belajar.catastreamandroidapp.feature.movie.data.remote.TmdbApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val tmdbApiService: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}
