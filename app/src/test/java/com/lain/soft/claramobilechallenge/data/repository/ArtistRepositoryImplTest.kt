package com.lain.soft.claramobilechallenge.data.repository

import androidx.paging.testing.asSnapshot
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.dto.artist.ArtistDetailResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.PaginationDto
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.UrlsDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ReleaseSearchResultDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ResultDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchArtistsResponseDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchReleasesResponseDto
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.util.Constants
import com.lain.soft.claramobilechallenge.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val discogsApi = mockk<DiscogsApi>()
    private val repository = ArtistRepositoryImpl(discogsApi)

    @Test
    fun getArtistDetail_delegatesToApiAndMapsResponse() = runTest {
        val id = 7
        coEvery { discogsApi.getArtistDetail(id) } returns ArtistDetailResponseDto(
            id = id,
            name = "ABBA",
            profile = "bio",
            realName = " ",
            urls = null,
            nameVariations = null,
            aliases = null,
            groups = null,
            images = null,
            members = null
        )

        val detail = repository.getArtistDetail(7)

        assertEquals(id, detail.id)
        assertEquals("ABBA", detail.name)
        assertEquals(null, detail.realName)
        coVerify(exactly = 1) { discogsApi.getArtistDetail(id) }
    }

    @Test
    fun searchArtists_flowTriggersApiWithPerPage30AndPage1() = runTest {
        val query = "abba"
        coEvery { discogsApi.searchArtists(query, any(), 30, 1) } returns SearchArtistsResponseDto(
            pagination = PaginationDto(
                items = 1,
                page = 1,
                pages = 1,
                perPage = 30,
                urls = UrlsDto(next = null, prev = null)
            ),
            results = listOf(ResultDto(id = 1, thumb = "", title = "ABBA"))
        )

        val snapshot = repository.searchArtists(query).asSnapshot()

        assertEquals(1, snapshot.size)
        assertEquals(1, snapshot.first().id)
        assertEquals(30, Constants.PAGING_ITEMS_PER_PAGE)
        coVerify(exactly = 1) { discogsApi.searchArtists(query, any(), 30, 1) }
    }

    @Test
    fun getArtistReleases_flowTriggersApiWithPerPage30AndPage1() = runTest {
        val filters = ArtistReleaseFilters(year = 2000, genre = "Rock", label = "EMI")
        coEvery {
            discogsApi.searchReleases(
                artist = "ABBA",
                type = any(),
                year = 2000,
                genre = "Rock",
                label = "EMI",
                perPage = 30,
                page = 1
            )
        } returns SearchReleasesResponseDto(
            pagination = PaginationDto(
                items = 1,
                page = 1,
                pages = 1,
                perPage = 30,
                urls = UrlsDto(next = null, prev = null)
            ),
            results = listOf(
                ReleaseSearchResultDto(
                    id = 2,
                    title = "Arrival",
                    year = "1976",
                    type = "release",
                    format = listOf("LP"),
                    label = listOf("EMI"),
                    genre = listOf("Rock"),
                    thumb = null,
                    coverImage = null
                )
            )
        )

        val snapshot = repository.getArtistReleases("ABBA", filters).asSnapshot()

        assertTrue(snapshot.isNotEmpty())
        assertEquals("Arrival", snapshot.first().title)
        assertEquals(30, Constants.PAGING_ITEMS_PER_PAGE)
        coVerify(exactly = 1) {
            discogsApi.searchReleases(
                artist = "ABBA",
                type = any(),
                year = 2000,
                genre = "Rock",
                label = "EMI",
                perPage = 30,
                page = 1
            )
        }
    }

}
