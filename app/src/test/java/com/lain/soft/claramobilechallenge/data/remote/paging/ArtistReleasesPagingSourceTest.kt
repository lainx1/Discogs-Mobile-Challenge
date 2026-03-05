package com.lain.soft.claramobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.PaginationDto
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.UrlsDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ReleaseSearchResultDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchReleasesResponseDto
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.util.Constants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistReleasesPagingSourceTest {

    private val discogsApi = mockk<DiscogsApi>()

    @Test
    fun load_usesPerPage30AndCalculatePrevNextKeys() = runTest {
        val filters = ArtistReleaseFilters(year = 1999, genre = "Rock", label = "EMI")
        val response = SearchReleasesResponseDto(
            pagination = PaginationDto(
                items = 100,
                page = 2,
                pages = 4,
                perPage = 30,
                urls = UrlsDto(next = "next", prev = "prev")
            ),
            results = listOf(
                ReleaseSearchResultDto(
                    id = 11,
                    title = "A",
                    year = "2001",
                    type = "release",
                    format = listOf("LP"),
                    label = listOf("EMI"),
                    genre = listOf("Rock"),
                    thumb = null,
                    coverImage = null
                ),
                ReleaseSearchResultDto(
                    id = 12,
                    title = "B",
                    year = "2000",
                    type = "release",
                    format = listOf("LP"),
                    label = listOf("EMI"),
                    genre = listOf("Rock"),
                    thumb = null,
                    coverImage = null
                )
            )
        )
        coEvery {
            discogsApi.searchReleases(
                artist = "ABBA",
                year = filters.year,
                genre = filters.genre,
                label = filters.label,
                perPage = Constants.PAGING_ITEMS_PER_PAGE,
                page = 2
            )
        } returns response

        val pagingSource = ArtistReleasesPagingSource(
            artistName = "ABBA",
            filters = filters,
            discogsApi = discogsApi
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
        coVerify(exactly = 1) {
            discogsApi.searchReleases(
                artist = "ABBA",
                year = 1999,
                genre = "Rock",
                label = "EMI",
                perPage = 30,
                page = 2
            )
        }
    }

    @Test
    fun load_whenApiFails_returnsError() = runTest {
        val filters = ArtistReleaseFilters()
        val error = RuntimeException()
        coEvery {
            discogsApi.searchReleases(any(), any(), any(), any(), any(), any(), any())
        } throws error

        val pagingSource = ArtistReleasesPagingSource(
            artistName = "ABBA",
            filters = filters,
            discogsApi = discogsApi
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(error, (result as PagingSource.LoadResult.Error).throwable)
    }
}
