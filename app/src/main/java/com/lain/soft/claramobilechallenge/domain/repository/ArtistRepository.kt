package com.lain.soft.claramobilechallenge.domain.repository

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun searchArtists(query: String): Flow<PagingData<Artist>>
    suspend fun getArtistDetail(artistId: Int): ArtistDetail
    fun getArtistReleases(
        artistName: String,
        filters: ArtistReleaseFilters
    ): Flow<PagingData<ArtistRelease>>
}
