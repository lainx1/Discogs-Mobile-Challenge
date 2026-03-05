package com.lain.soft.claramobilechallenge.data.remote.dto.search

import com.google.gson.annotations.SerializedName

data class ReleaseSearchResultDto(
    val id: Int?,
    val title: String?,
    val year: String?,
    val type: String?,
    val format: List<String>?,
    val label: List<String>?,
    val genre: List<String>?,
    val thumb: String?,
    @SerializedName("cover_image")
    val coverImage: String?
)
