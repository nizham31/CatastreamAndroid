
package com.belajar.catastreamandroidapp.feature.playlist.data.dto
import com.belajar.catastreamandroidapp.feature.playlist.data.dto.MovieSummaryDTO
import com.google.gson.annotations.SerializedName
import com.belajar.catastreamandroidapp.feature.user.data.dto.UserSummaryDTO

data class PlaylistResponseDTO(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("user")
    val user: UserSummaryDTO?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?,

    @SerializedName("itemCount")
    val itemCount: Int,

    @SerializedName("itemsPreview")
    val itemsPreview: List<MovieSummaryDTO>?
)