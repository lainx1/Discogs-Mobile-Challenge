package com.lain.soft.claramobilechallenge.data.remote.dto.artist

import com.google.gson.annotations.SerializedName

data class ArtistDetailResponseDto(
    val id: Int,
    val name: String,
    val profile: String,
    @SerializedName("realname")
    val realName: String?,
    val urls: List<String>?,
    @SerializedName("namevariations")
    val nameVariations: List<String>?,
    val aliases: List<ArtistAliasDto>?,
    val groups: List<ArtistGroupDto>?,
    val images: List<ArtistImageDto>?,
    val members: List<ArtistMemberDto>?,
)
