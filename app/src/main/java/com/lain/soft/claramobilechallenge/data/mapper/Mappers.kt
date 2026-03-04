package com.lain.soft.claramobilechallenge.data.mapper

import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistDetailResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistGroupDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistMemberDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistAliasDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ResultDto
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.model.ArtistMember
import com.lain.soft.claramobilechallenge.domain.model.Alias
import com.lain.soft.claramobilechallenge.domain.model.Group

private val TAG_REGEX = Regex("\\[[^\\]]*]")
private val WHITESPACE_REGEX = Regex("\\s+")

fun ResultDto.toDomain(): Artist =
    Artist(
        id = id,
        thumbnail = thumb.ifEmpty { null },
        name = title
    )

fun ArtistDetailResponseDto.toDomain(): ArtistDetail =
    ArtistDetail(
        id = id,
        name = name,
        biographySummary = profile.toBiographySummary(),
        realName = realName?.takeIf { it.isNotBlank() },
        urls = urls.orEmpty()
            .map(String::trim)
            .filter { it.isNotBlank() },
        imageUrl = images
            .orEmpty()
            .sortedBy { image ->
                when (image.type?.lowercase()) {
                    "primary" -> 0
                    else -> 1
                }
            }
            .firstNotNullOfOrNull { it.uri }
            .orEmpty()
            .ifEmpty { null },
        nameVariations = nameVariations
            .orEmpty()
            .map(String::trim)
            .filter { it.isNotBlank() },
        aliases = aliases
            .orEmpty()
            .mapNotNull(ArtistAliasDto::toDomainOrNull),
        groups = groups
            .orEmpty()
            .mapNotNull(ArtistGroupDto::toDomainOrNull),
        members = members
            .orEmpty()
            .mapNotNull(ArtistMemberDto::toDomainOrNull)
    )

private fun ArtistMemberDto.toDomainOrNull(): ArtistMember? {
    val safeName = name?.trim().orEmpty()
    if (safeName.isBlank()) return null
    return ArtistMember(
        id = id,
        name = safeName,
        thumbnailUrl = thumbnailUrl?.trim()?.takeIf { it.isNotBlank() }
    )
}

private fun ArtistAliasDto.toDomainOrNull(): Alias? {
    val safeName = name?.trim().orEmpty()
    if (safeName.isBlank()) return null
    return Alias(
        id = id,
        name = safeName,
        thumbnailUrl = thumbnailUrl?.trim()?.takeIf { it.isNotBlank() }
    )
}

private fun ArtistGroupDto.toDomainOrNull(): Group? {
    val safeName = name?.trim().orEmpty()
    if (safeName.isBlank()) return null
    return Group(
        id = id,
        name = safeName,
        thumbnailUrl = thumbnailUrl?.trim()?.takeIf { it.isNotBlank() }
    )
}


private fun String?.toBiographySummary(): String {
    if (this.isNullOrBlank()) return ""
    return this
        .replace(TAG_REGEX, "")
        .replace(WHITESPACE_REGEX, " ")
        .trim()
}
