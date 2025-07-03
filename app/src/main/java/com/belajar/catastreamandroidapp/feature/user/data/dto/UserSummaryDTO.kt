package com.belajar.catastreamandroidapp.feature.user.data.dto

import com.google.gson.annotations.SerializedName

data class UserSummaryDTO(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String?,

    @SerializedName("avatarUrl")
    val avatarUrl: String?
)