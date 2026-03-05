package com.lain.soft.claramobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetArtistReleasesUseCaseTest {

    private val artistRepository = mockk<ArtistRepository>()
    private val useCase = GetArtistReleasesUseCase(artistRepository)

    @Test
    fun invoke_delegatesToRepositoryWithExactParams() = runTest {
        val year = 1997
        val genre = "Rock"
        val label = "EMI"
        val artistName = "Pink Floyd"
        val filters = ArtistReleaseFilters(year = year, genre = genre, label = label)
        val input = GetArtistReleasesUseCase.Input(
            artistName = artistName,
            filters = filters
        )
        val expectedFlow = flowOf(
            PagingData.from(
                listOf(
                    ArtistRelease(
                        key = "r1",
                        id = 1,
                        title = "Animals",
                        year = year,
                        type = "release",
                        format = "LP",
                        thumb = null,
                        genres = genre,
                        labels = label
                    )
                )
            )
        )
        every { artistRepository.getArtistReleases(artistName, filters) } returns expectedFlow

        val result = useCase(input)

        assertTrue(result.isSuccess)
        assertEquals(expectedFlow, result.getOrNull())
        verify(exactly = 1) { artistRepository.getArtistReleases(artistName, filters) }
    }

    @Test
    fun invoke_whenRepositoryThrows_returnsFailure() = runTest {
        val artistName = "ABBA"
        val filters = ArtistReleaseFilters()
        val input = GetArtistReleasesUseCase.Input(artistName, filters)
        val failure = RuntimeException()
        every { artistRepository.getArtistReleases(artistName, filters) } throws failure

        val result = useCase(input)

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
        verify(exactly = 1) { artistRepository.getArtistReleases(artistName, filters) }
    }
}
