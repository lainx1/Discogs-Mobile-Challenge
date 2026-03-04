package com.lain.soft.claramobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lain.soft.claramobilechallenge.data.mapper.toDomain
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT_ITEMS_PER_PAGE
import com.lain.soft.claramobilechallenge.util.Constants.SEARCH_ENDPOINT_START_PAGE

class SearchArtistPagingSource(
    private val query: String,
    private val discogsApi: DiscogsApi
) : PagingSource<Int, Artist>() {
    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        return try {
            val currentPage = params.key ?: SEARCH_ENDPOINT_START_PAGE
            val response = discogsApi.searchArtists(
                query = query,
                page = currentPage,
                perPage = SEARCH_ENDPOINT_ITEMS_PER_PAGE
            )
            val items = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = items,
                prevKey = calculatePrevKey(currentPage, response.pagination.urls.prev),
                nextKey = calculateNextKey(currentPage, response.pagination.urls.next)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun calculatePrevKey(currentPage: Int, prevUrl: String?): Int? {
        return if (prevUrl == null) null else currentPage - 1
    }

    private fun calculateNextKey(currentPage: Int, nextUrl: String?): Int? {
        return if (nextUrl == null) null else currentPage + 1
    }
}
