package com.lain.soft.claramobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArtistUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) : UseCase<String, Flow<PagingData<Artist>>>() {
    override suspend fun execute(input: String): Flow<PagingData<Artist>> =
        artistRepository.searchArtists(input)
}
