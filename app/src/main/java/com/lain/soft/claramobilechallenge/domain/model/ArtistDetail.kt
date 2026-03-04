package com.lain.soft.claramobilechallenge.domain.model

data class ArtistDetail(
    val id: Int,
    val name: String,
    val biographySummary: String,
    val realName: String?,
    val urls: List<String>,
    val imageUrl: String?,
    val nameVariations: List<String>,
    val aliases: List<Alias>,
    val groups: List<Group>,
    val members: List<ArtistMember>
)

data class Alias(
    val id: Int?,
    val name: String,
    val thumbnailUrl: String?
)

data class Group(
    val id: Int?,
    val name: String,
    val thumbnailUrl: String?
)

data class ArtistMember(
    val id: Int?,
    val name: String,
    val thumbnailUrl: String?
)
