package com.lain.soft.claramobilechallenge.ui.viewmodel

import app.cash.turbine.test
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.usecase.PopulateLoadingArtistsUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.SearchArtistUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.ValidateSearchQueryUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.ValidateSearchQueryUseCase.ValidateSearchQueryException
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenEvent
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenState
import com.lain.soft.claramobilechallenge.ui.util.UiText
import com.lain.soft.claramobilechallenge.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchArtistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var populateLoadingArtistsUseCase: PopulateLoadingArtistsUseCase
    private lateinit var validateSearchQueryUseCase: ValidateSearchQueryUseCase
    private lateinit var searchArtistUseCase: SearchArtistUseCase
    private lateinit var errorMessageMapper: ErrorMessageMapper

    @Before
    fun setup() {
        populateLoadingArtistsUseCase = mockk()
        validateSearchQueryUseCase = mockk()
        searchArtistUseCase = mockk()
        errorMessageMapper = mockk()

        coEvery { populateLoadingArtistsUseCase.invoke(any()) } returns Result.success(emptyList())
    }

    @Test
    fun onQueryChange_emptyQuery_resetsToIdle() = runTest {
        val artistsFlow = flowOf(PagingData.from(listOf(Artist(id = 1, thumbnail = null, name = "ABBA"))))
        coEvery { validateSearchQueryUseCase.invoke("abba") } returns Result.success("abba")
        coEvery { searchArtistUseCase.invoke("abba") } returns Result.success(artistsFlow)
        coEvery { validateSearchQueryUseCase.invoke("") } returns Result.failure(
            ValidateSearchQueryException.QueryIsEmptyException()
        )

        val viewModel = createViewModel()

        viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange("abba"))
        advanceTimeBy(500)
        advanceUntilIdle()

        viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange(""))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("", state.query)
        assertTrue(state.state is SearchArtistScreenState.Idle)
    }

    @Test
    fun onQueryChange_success_setsSuccessWithArtistsFlow() = runTest {
        val artistsFlow = flowOf(PagingData.from(listOf(Artist(id = 7, thumbnail = null, name = "Daft Punk"))))
        coEvery { validateSearchQueryUseCase.invoke("daft") } returns Result.success("daft")
        coEvery { searchArtistUseCase.invoke("daft") } returns Result.success(artistsFlow)

        val viewModel = createViewModel()

        viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange("daft"))
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.state is SearchArtistScreenState.Success)
        assertEquals(artistsFlow, state.artists)
    }

    @Test
    fun onQueryChange_failure_setsErrorStateWithMappedMessage() = runTest {
        val failure = RuntimeException("network")
        val mappedError = UiText.StringResource(R.string.error_timeout)
        coEvery { validateSearchQueryUseCase.invoke("metallica") } returns Result.success("metallica")
        coEvery { searchArtistUseCase.invoke("metallica") } returns Result.failure(failure)
        coEvery { errorMessageMapper.map(failure) } returns mappedError

        val viewModel = createViewModel()

        viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange("metallica"))
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = viewModel.state.value
        val errorState = state.state as SearchArtistScreenState.Error
        val message = errorState.message as UiText.StringResource
        assertEquals(R.string.error_timeout, message.id)
    }

    @Test
    fun onNavigateToDetail_emitsNavigationEffect() = runTest {
        val viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(SearchArtistScreenEvent.OnNavigateToDetail(99))
            assertEquals(SearchArtistScreenEffect.NavigateToDetail(99), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onListError_mapsMessageAndSetsErrorState() = runTest {
        val exception = IllegalStateException("Something went wrong")
        val mappedError = UiText.StringResource(R.string.error_server)
        coEvery { errorMessageMapper.map(exception) } returns mappedError

        val viewModel = createViewModel()

        viewModel.onEvent(SearchArtistScreenEvent.OnListError(exception))
        advanceUntilIdle()

        coVerify { errorMessageMapper.map(exception) }
        val state = viewModel.state.value
        val errorState = state.state as SearchArtistScreenState.Error
        val message = errorState.message as UiText.StringResource
        assertEquals(R.string.error_server, message.id)
    }

    private fun createViewModel(): SearchArtistViewModel {
        return SearchArtistViewModel(
            populateLoadingArtistsUseCase = populateLoadingArtistsUseCase,
            validateSearchQueryUseCase = validateSearchQueryUseCase,
            searchArtistUseCase = searchArtistUseCase,
            errorMessageMapper = errorMessageMapper
        )
    }
}
