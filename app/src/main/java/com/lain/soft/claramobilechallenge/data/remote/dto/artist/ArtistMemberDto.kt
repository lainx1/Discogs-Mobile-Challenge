package com.lain.soft.claramobilechallenge.data.remote.dto.artist

import com.google.gson.annotations.SerializedName

data class ArtistMemberDto(
    val id: Int?,
    val name: String?,    
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?
)
