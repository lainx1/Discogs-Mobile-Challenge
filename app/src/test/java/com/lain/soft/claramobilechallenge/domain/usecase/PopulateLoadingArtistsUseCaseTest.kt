package com.lain.soft.claramobilechallenge.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PopulateLoadingArtistsUseCaseTest {

    private val useCase = PopulateLoadingArtistsUseCase()

    @Test
    fun invoke_returnsExpectedSizeAndSequentialIds() = runTest {
        val total = 4
        val result = useCase(total)

        assertTrue(result.isSuccess)
        val artists = result.getOrNull().orEmpty()
        assertEquals(total, artists.size)
        assertEquals(listOf(0, 1, 2, 3), artists.map { it.id })
    }
}
