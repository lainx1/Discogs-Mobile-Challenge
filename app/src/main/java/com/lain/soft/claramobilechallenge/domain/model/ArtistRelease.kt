package com.lain.soft.claramobilechallenge.domain.model

data class ArtistRelease(
    val key: String,
    val id: Int?,
    val title: String?,
    val year: Int?,
    val type: String?,
    val format: String?,
    val thumb: String?,
    val genres: String,
    val labels: String
)

data class ArtistReleaseFilters(
    val year: Int? = null,
    val genre: String? = null,
    val label: String? = null
)
