package com.lain.soft.claramobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
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
class SearchArtistUseCaseTest {

    private val artistRepository = mockk<ArtistRepository>()
    private val useCase = SearchArtistUseCase(artistRepository)

    @Test
    fun invoke_delegatesToRepositoryWithExactQuery() = runTest {
        val artistName = "abba"
        val expectedFlow = flowOf(PagingData.from(listOf(Artist(1, null, "ABBA"))))
        every { artistRepository.searchArtists(artistName) } returns expectedFlow

        val result = useCase(artistName)

        assertTrue(result.isSuccess)
        assertEquals(expectedFlow, result.getOrNull())
        verify(exactly = 1) { artistRepository.searchArtists(artistName) }
    }

    @Test
    fun invoke_whenRepositoryThrows_returnsFailure() = runTest {
        val artistName = "abba"
        val failure = RuntimeException()
        every { artistRepository.searchArtists(artistName) } throws failure

        val result = useCase(artistName)

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
        verify(exactly = 1) { artistRepository.searchArtists(artistName) }
    }
}
