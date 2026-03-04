package com.lain.soft.claramobilechallenge.data.remote.dto.artist

import com.google.gson.annotations.SerializedName

data class ArtistImageDto(
    val type: String?,
    @SerializedName("uri")
    val uri: String?    
)
