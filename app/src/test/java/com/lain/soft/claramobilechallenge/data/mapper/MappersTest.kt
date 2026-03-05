package com.lain.soft.claramobilechallenge.data.mapper

import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistAliasDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistDetailResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistGroupDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistImageDto
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistMemberDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ReleaseSearchResultDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ResultDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MappersTest {

    @Test
    fun resultDto_toDomain_mapsEmptyThumbToNull() {
        val dto = ResultDto(
            id = 3,
            thumb = "",
            title = "Muse"
        )

        val domain = dto.toDomain()

        assertEquals(3, domain.id)
        assertNull(domain.thumbnail)
        assertEquals("Muse", domain.name)
    }

    @Test
    fun artistDetail_toDomain_filtersBlankValuesAndPicksPrimaryImage() {
        val dto = ArtistDetailResponseDto(
            id = 10,
            name = "Artist",
            profile = "Bio",
            realName = " ",
            urls = listOf("  ", " https://a.com "),
            nameVariations = listOf("  ", " A "),
            aliases = listOf(
                ArtistAliasDto(id = 1, name = "  ", thumbnailUrl = "x"),
                ArtistAliasDto(id = 2, name = " Alias ", thumbnailUrl = " ")
            ),
            groups = listOf(
                ArtistGroupDto(id = 3, name = " Group ", thumbnailUrl = " http://img ")
            ),
            images = listOf(
                ArtistImageDto(type = "secondary", uri = "http://secondary"),
                ArtistImageDto(type = "primary", uri = "http://primary")
            ),
            members = listOf(
                ArtistMemberDto(id = 4, name = " Member ", thumbnailUrl = " ")
            )
        )

        val domain = dto.toDomain()

        assertNull(domain.realName)
        assertEquals(listOf("https://a.com"), domain.urls)
        assertEquals(listOf("A"), domain.nameVariations)
        assertEquals("http://primary", domain.imageUrl)
        assertEquals(1, domain.aliases.size)
        assertEquals("Alias", domain.aliases.first().name)
        assertNull(domain.aliases.first().thumbnailUrl)
        assertEquals(1, domain.groups.size)
        assertEquals("Group", domain.groups.first().name)
        assertEquals("http://img", domain.groups.first().thumbnailUrl)
        assertEquals(1, domain.members.size)
        assertEquals("Member", domain.members.first().name)
        assertNull(domain.members.first().thumbnailUrl)
    }

    @Test
    fun releaseResult_toDomain_appliesDefaultsForMissingData() {
        val dto = ReleaseSearchResultDto(
            id = null,
            title = " ",
            year = "abc",
            type = " ",
            format = listOf(" ", "LP", " "),
            label = listOf("EMI", " EMI ", " "),
            genre = listOf("Rock", "Rock", " "),
            thumb = "https://img/spacer.gif",
            coverImage = null
        )

        val domain = dto.toDomain()

        assertEquals("-1_untitled_-1", domain.key)
        assertNull(domain.title)
        assertNull(domain.year)
        assertNull(domain.type)
        assertEquals("LP", domain.format)
        assertEquals("EMI", domain.labels)
        assertEquals("Rock", domain.genres)
        assertNull(domain.thumb)
    }
}
