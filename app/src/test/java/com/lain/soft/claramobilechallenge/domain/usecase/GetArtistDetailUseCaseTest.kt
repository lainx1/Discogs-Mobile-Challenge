package com.lain.soft.claramobilechallenge.domain.usecase

import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetArtistDetailUseCaseTest {

    private val artistRepository = mockk<ArtistRepository>()
    private val useCase = GetArtistDetailUseCase(artistRepository)

    @Test
    fun invoke_delegatesToRepositoryWithExactArtistId() = runTest {
        val artistId = 12
        val artist = ArtistDetail(
            id = artistId,
            name = "Pink Floyd",
            biographySummary = "bio",
            realName = null,
            urls = emptyList(),
            imageUrl = null,
            nameVariations = emptyList(),
            aliases = emptyList(),
            groups = emptyList(),
            members = emptyList()
        )
        coEvery { artistRepository.getArtistDetail(artistId) } returns artist

        val result = useCase(artistId)

        assertTrue(result.isSuccess)
        assertEquals(artist, result.getOrNull())
        coVerify(exactly = 1) { artistRepository.getArtistDetail(artistId) }
    }

    @Test
    fun invoke_whenRepositoryThrows_returnsFailure() = runTest {
        val artistId = 10
        val failure = RuntimeException()
        coEvery { artistRepository.getArtistDetail(artistId) } throws failure

        val result = useCase(artistId)

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
        coVerify(exactly = 1) { artistRepository.getArtistDetail(artistId) }
    }
}
