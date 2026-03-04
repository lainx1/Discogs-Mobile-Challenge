package com.lain.soft.claramobilechallenge.domain.repository

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun searchArtists(query: String): Flow<PagingData<Artist>>
    suspend fun getArtistDetail(artistId: Int): ArtistDetail
}
