package com.lain.soft.claramobilechallenge.data.remote.dto.pagination

import com.google.gson.annotations.SerializedName

data class PaginationDto(
    val items: Int,
    val page: Int,
    val pages: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val urls: UrlsDto
)
