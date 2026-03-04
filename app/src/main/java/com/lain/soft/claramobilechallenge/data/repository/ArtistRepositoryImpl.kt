package com.lain.soft.claramobilechallenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.paging.SearchArtistPagingSource
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import com.lain.soft.claramobilechallenge.util.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val discogsApi: DiscogsApi
) : ArtistRepository {
    override fun searchArtists(query: String): Flow<PagingData<Artist>> =
        Pager(
            PagingConfig(
                pageSize = Constants.SEARCH_ENDPOINT_ITEMS_PER_PAGE,
                initialLoadSize = Constants.SEARCH_ENDPOINT_FIRST_LOAD_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchArtistPagingSource(query = query, discogsApi = discogsApi)
            }
        ).flow
}
