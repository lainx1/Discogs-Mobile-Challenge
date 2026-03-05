package com.lain.soft.claramobilechallenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.data.mapper.toDomain
import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.paging.ArtistReleasesPagingSource
import com.lain.soft.claramobilechallenge.data.remote.paging.SearchArtistPagingSource
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
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
                pageSize = Constants.PAGING_ITEMS_PER_PAGE,
                initialLoadSize = Constants.PAGING_ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchArtistPagingSource(query = query, discogsApi = discogsApi)
            }
        ).flow

    override suspend fun getArtistDetail(artistId: Int): ArtistDetail =
        discogsApi.getArtistDetail(artistId).toDomain()

    override fun getArtistReleases(
        artistName: String,
        filters: ArtistReleaseFilters
    ): Flow<PagingData<ArtistRelease>> =
        Pager(
            config = PagingConfig(
                pageSize = Constants.PAGING_ITEMS_PER_PAGE,
                initialLoadSize = Constants.PAGING_ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ArtistReleasesPagingSource(
                    artistName = artistName,
                    filters = filters,
                    discogsApi = discogsApi
                )
            }
        ).flow
}
