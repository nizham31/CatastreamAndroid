package com.belajar.catastreamandroidapp.feature.user.data.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val username: String,
    val avatarUrl: String?,
    val bio: String?
)
