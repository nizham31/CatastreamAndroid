package com.belajar.catastreamandroidapp.feature.user.data.dto

data class UpdateUserProfileRequest(
    val newUsername: String?,
    val newBio: String?,
    val newAvatarUrl: String?
)
