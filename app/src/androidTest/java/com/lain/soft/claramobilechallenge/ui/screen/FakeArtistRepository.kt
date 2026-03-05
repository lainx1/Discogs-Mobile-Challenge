package com.lain.soft.claramobilechallenge.ui.screen

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeArtistRepository(
    private val searchArtistsProvider: (String) -> Flow<PagingData<Artist>> = {
        flowOf(PagingData.empty())
    },
    private val artistDetailProvider: (Int) -> ArtistDetail = {
        error("Artist detail provider not configured")
    },
    private val artistReleasesProvider: (String, ArtistReleaseFilters) -> Flow<PagingData<ArtistRelease>> = { _, _ ->
        flowOf(PagingData.empty())
    }
) : ArtistRepository {

    override fun searchArtists(query: String): Flow<PagingData<Artist>> = searchArtistsProvider(query)

    override suspend fun getArtistDetail(artistId: Int): ArtistDetail = artistDetailProvider(artistId)

    override fun getArtistReleases(
        artistName: String,
        filters: ArtistReleaseFilters
    ): Flow<PagingData<ArtistRelease>> = artistReleasesProvider(artistName, filters)
}
