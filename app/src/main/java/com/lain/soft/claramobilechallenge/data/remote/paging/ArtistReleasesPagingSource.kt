package com.lain.soft.claramobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lain.soft.claramobilechallenge.data.mapper.toDomain
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.util.Constants.PAGING_ITEMS_PER_PAGE
import com.lain.soft.claramobilechallenge.util.Constants.PAGING_START_PAGE

class ArtistReleasesPagingSource(
    private val artistName: String,
    private val filters: ArtistReleaseFilters,
    private val discogsApi: DiscogsApi
) : PagingSource<Int, ArtistRelease>() {

    override fun getRefreshKey(state: PagingState<Int, ArtistRelease>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArtistRelease> {
        return try {
            val currentPage = params.key ?: PAGING_START_PAGE
            val response = discogsApi.searchReleases(
                artist = artistName,
                year = filters.year,
                genre = filters.genre,
                label = filters.label,
                page = currentPage,
                perPage = PAGING_ITEMS_PER_PAGE
            )
            val releases = response.results
                .map { it.toDomain() }
                .sortedWith(
                    compareByDescending<ArtistRelease> { it.year ?: Int.MIN_VALUE }
                        .thenBy { it.title.orEmpty().lowercase() }
                        .thenBy { it.id ?: Int.MAX_VALUE }
                )

            LoadResult.Page(
                data = releases,
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
