package com.lain.soft.claramobilechallenge.domain.usecase

import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistDetailUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) : UseCase<Int, ArtistDetail>() {
    override suspend fun execute(input: Int): ArtistDetail =
        artistRepository.getArtistDetail(input)
}
