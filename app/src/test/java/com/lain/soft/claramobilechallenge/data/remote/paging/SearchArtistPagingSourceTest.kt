package com.lain.soft.claramobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.PaginationDto
import com.lain.soft.claramobilechallenge.data.remote.dto.pagination.UrlsDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.ResultDto
import com.lain.soft.claramobilechallenge.data.remote.dto.search.SearchArtistsResponseDto
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
class SearchArtistPagingSourceTest {

    private val discogsApi = mockk<DiscogsApi>()

    @Test
    fun load_usesPerPage30AndCalculateNextPrevKeys() = runTest {
        val response = SearchArtistsResponseDto(
            pagination = PaginationDto(
                items = 100,
                page = 1,
                pages = 4,
                perPage = 30,
                urls = UrlsDto(next = "next", prev = null)
            ),
            results = listOf(
                ResultDto(id = 1, thumb = "", title = "ABBA")
            )
        )
        coEvery {
            discogsApi.searchArtists(
                query = "abba",
                perPage = Constants.PAGING_ITEMS_PER_PAGE,
                page = 1
            )
        } returns response

        val pagingSource = SearchArtistPagingSource(query = "abba", discogsApi = discogsApi)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
        assertEquals(1, page.data.first().id)
        coVerify(exactly = 1) {
            discogsApi.searchArtists(
                query = "abba",
                perPage = 30,
                page = 1
            )
        }
    }

    @Test
    fun load_whenApiFails_returnsError() = runTest {
        val error = RuntimeException()
        coEvery { discogsApi.searchArtists(any(), any(), any(), any()) } throws error
        val pagingSource = SearchArtistPagingSource(query = "abba", discogsApi = discogsApi)

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
