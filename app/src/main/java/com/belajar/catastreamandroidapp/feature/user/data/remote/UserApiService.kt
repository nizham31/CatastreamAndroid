package com.belajar.catastreamandroidapp.feature.user.data.remote

import com.belajar.catastreamandroidapp.feature.user.data.dto.UserResponse
import com.belajar.catastreamandroidapp.feature.user.data.dto.UpdateUserProfileRequest
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @GET("api/users/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @PUT("api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body updateRequest: UpdateUserProfileRequest
    ): Response<UserResponse>
}
