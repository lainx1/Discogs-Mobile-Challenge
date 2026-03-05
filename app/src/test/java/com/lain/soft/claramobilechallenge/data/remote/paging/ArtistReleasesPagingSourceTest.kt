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
    private val artistName = "ABBA"
    private val defaultFilters = ArtistReleaseFilters(year = 1999, genre = "Rock", label = "EMI")

    @Test
    fun load_usesPerPage30AndCalculatePrevNextKeys() = runTest {
        val expectedPage = 2
        val pagingSource = createPagingSource(defaultFilters)
        stubSearchReleasesSuccess(filters = defaultFilters, page = expectedPage)

        val result = pagingSource.load(createAppendParams(key = expectedPage))

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
        verifySearchReleasesCalled(filters = defaultFilters, page = expectedPage)
    }

    @Test
    fun load_whenApiFails_returnsError() = runTest {
        val filters = ArtistReleaseFilters()
        val error = RuntimeException()
        coEvery {
            discogsApi.searchReleases(any(), any(), any(), any(), any(), any(), any())
        } throws error

        val pagingSource = ArtistReleasesPagingSource(
            artistName = artistName,
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

    private fun createPagingSource(filters: ArtistReleaseFilters): ArtistReleasesPagingSource =
        ArtistReleasesPagingSource(
            artistName = artistName,
            filters = filters,
            discogsApi = discogsApi
        )

    private fun createAppendParams(key: Int): PagingSource.LoadParams.Append<Int> =
        PagingSource.LoadParams.Append(
            key = key,
            loadSize = Constants.PAGING_ITEMS_PER_PAGE,
            placeholdersEnabled = false
        )

    private fun stubSearchReleasesSuccess(
        filters: ArtistReleaseFilters,
        page: Int
    ) {
        coEvery {
            discogsApi.searchReleases(
                artist = artistName,
                year = filters.year,
                genre = filters.genre,
                label = filters.label,
                perPage = Constants.PAGING_ITEMS_PER_PAGE,
                page = page
            )
        } returns createSearchReleasesResponse(page = page)
    }

    private fun verifySearchReleasesCalled(
        filters: ArtistReleaseFilters,
        page: Int
    ) {
        coVerify(exactly = 1) {
            discogsApi.searchReleases(
                artist = artistName,
                year = filters.year,
                genre = filters.genre,
                label = filters.label,
                perPage = Constants.PAGING_ITEMS_PER_PAGE,
                page = page
            )
        }
    }

    private fun createSearchReleasesResponse(page: Int): SearchReleasesResponseDto =
        SearchReleasesResponseDto(
            pagination = PaginationDto(
                items = 100,
                page = page,
                pages = 4,
                perPage = Constants.PAGING_ITEMS_PER_PAGE,
                urls = UrlsDto(next = "next", prev = "prev")
            ),
            results = listOf(
                createReleaseSearchResultDto(id = 11, title = "A", year = "2001"),
                createReleaseSearchResultDto(id = 12, title = "B", year = "2000")
            )
        )

    private fun createReleaseSearchResultDto(
        id: Int,
        title: String,
        year: String
    ): ReleaseSearchResultDto =
        ReleaseSearchResultDto(
            id = id,
            title = title,
            year = year,
            type = "release",
            format = listOf("LP"),
            label = listOf("EMI"),
            genre = listOf("Rock"),
            thumb = null,
            coverImage = null
        )
}
