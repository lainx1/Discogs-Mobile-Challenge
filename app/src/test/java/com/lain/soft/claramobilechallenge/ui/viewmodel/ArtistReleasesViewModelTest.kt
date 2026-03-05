package com.lain.soft.claramobilechallenge.ui.viewmodel

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistReleasesUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.navigation.RouteArgDecoder
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenState
import com.lain.soft.claramobilechallenge.ui.util.UiText
import com.lain.soft.claramobilechallenge.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistReleasesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getArtistReleasesUseCase: GetArtistReleasesUseCase
    private lateinit var errorMessageMapper: ErrorMessageMapper
    private lateinit var routeArgDecoder: RouteArgDecoder

    @Before
    fun setup() {
        getArtistReleasesUseCase = mockk()
        errorMessageMapper = mockk()
        routeArgDecoder = RouteArgDecoder { value ->
            java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8)
        }
    }

    @Test
    fun init_success_setsSuccessState() = runTest {
        coEvery { getArtistReleasesUseCase.invoke(any()) } returns Result.success(sampleReleasesFlow())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.screenState is ArtistReleasesScreenState.Success)
    }

    @Test
    fun init_failure_setsErrorStateWithMappedMessage() = runTest {
        val exception = RuntimeException("network")
        coEvery { getArtistReleasesUseCase.invoke(any()) } returns Result.failure(exception)
        coEvery { errorMessageMapper.map(exception) } returns UiText.StringResource(R.string.error_timeout)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val errorState = viewModel.state.value.screenState as ArtistReleasesScreenState.Error
        val message = errorState.message as UiText.StringResource
        assertEquals(R.string.error_timeout, message.id)
    }

    @Test
    fun onGenreFilterChange_updatesFiltersAndTriggersReload() = runTest {
        coEvery { getArtistReleasesUseCase.invoke(any()) } returns Result.success(sampleReleasesFlow())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(ArtistReleasesEvent.OnGenreFilterChange("  Rock  "))
        advanceUntilIdle()

        assertEquals("Rock", viewModel.state.value.selectedFilters.genre)
        val inputs = mutableListOf<GetArtistReleasesUseCase.Input>()
        coVerify(atLeast = 2) {
            getArtistReleasesUseCase.invoke(capture(inputs))
        }
        assertTrue(
            inputs.any {
                it.artistName == "ABBA" && it.filters == ArtistReleaseFilters(genre = "Rock")
            }
        )
    }

    @Test
    fun onNavigateBack_emitsNavigateBackEffect() = runTest {
        coEvery { getArtistReleasesUseCase.invoke(any()) } returns Result.success(sampleReleasesFlow())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ArtistReleasesEvent.OnNavigateBack)
            assertEquals(ArtistReleasesScreenEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sampleReleasesFlow() = flowOf(
        PagingData.from(
            listOf(
                ArtistRelease(
                    key = "r1",
                    id = 1,
                    title = "Arrival",
                    year = 1976,
                    type = "release",
                    format = "LP",
                    thumb = null,
                    genres = "Pop",
                    labels = "Polar"
                )
            )
        )
    )

    private fun createViewModel(): ArtistReleasesViewModel {
        return ArtistReleasesViewModel(
            getArtistReleasesUseCase = getArtistReleasesUseCase,
            errorMessageMapper = errorMessageMapper,
            routeArgDecoder = routeArgDecoder,
            savedStateHandle = SavedStateHandle(
                mapOf(Routes.ARTIST_NAME to "ABBA")
            )
        )
    }
}
