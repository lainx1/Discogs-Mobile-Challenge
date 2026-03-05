package com.lain.soft.claramobilechallenge.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateSearchQueryUseCaseTest {

    private val useCase = ValidateSearchQueryUseCase()

    @Test
    fun emptyQuery_returnsQueryIsEmptyFailure() = runTest {
        val result = useCase("")

        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ValidateSearchQueryUseCase
                .ValidateSearchQueryException.QueryIsEmptyException
        )
    }

    @Test
    fun nonEmptyQuery_returnsOriginalValue() = runTest {
        val result = useCase("metallica")

        assertTrue(result.isSuccess)
        assertEquals("metallica", result.getOrNull())
    }
}
