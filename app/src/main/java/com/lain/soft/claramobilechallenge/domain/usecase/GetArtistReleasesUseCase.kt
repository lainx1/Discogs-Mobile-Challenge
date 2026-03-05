package com.lain.soft.claramobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistReleasesUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) : UseCase<GetArtistReleasesUseCase.Input, Flow<PagingData<ArtistRelease>>>() {
    data class Input(
        val artistName: String,
        val filters: ArtistReleaseFilters
    )

    override suspend fun execute(input: Input): Flow<PagingData<ArtistRelease>> =
        artistRepository.getArtistReleases(
            artistName = input.artistName,
            filters = input.filters
        )
}
