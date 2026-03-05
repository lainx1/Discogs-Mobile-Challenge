package com.lain.soft.claramobilechallenge.ui.viewmodel

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistDetailUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenState
import com.lain.soft.claramobilechallenge.ui.util.UiText
import com.lain.soft.claramobilechallenge.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getArtistDetailUseCase: GetArtistDetailUseCase
    private lateinit var errorMessageMapper: ErrorMessageMapper

    @Before
    fun setup() {
        getArtistDetailUseCase = mockk()
        errorMessageMapper = mockk()
    }

    @Test
    fun init_success_setsSuccessStateWithArtist() = runTest {
        val artist = ArtistDetail(
            id = 1,
            name = "ABBA",
            biographySummary = "bio",
            realName = null,
            urls = emptyList(),
            imageUrl = null,
            nameVariations = emptyList(),
            aliases = emptyList(),
            groups = emptyList(),
            members = emptyList()
        )
        coEvery { getArtistDetailUseCase.invoke(1) } returns Result.success(artist)

        val viewModel = createViewModel(artistId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.screenState is ArtistDetailScreenState.Success)
        assertEquals(artist, state.artist)
    }

    @Test
    fun init_failure_setsErrorStateWithMappedMessage() = runTest {
        val failure = RuntimeException("404")
        coEvery { getArtistDetailUseCase.invoke(10) } returns Result.failure(failure)
        coEvery { errorMessageMapper.map(failure) } returns UiText.StringResource(R.string.error_not_found)

        val viewModel = createViewModel(artistId = 10)
        advanceUntilIdle()

        val state = viewModel.state.value
        val errorState = state.screenState as ArtistDetailScreenState.Error
        val message = errorState.message as UiText.StringResource
        assertEquals(R.string.error_not_found, message.id)
        assertNull(state.artist)
    }

    @Test
    fun onNavigateBack_emitsNavigateBackEffect() = runTest {
        coEvery { getArtistDetailUseCase.invoke(1) } returns Result.success(
            ArtistDetail(
                id = 1,
                name = "A",
                biographySummary = "",
                realName = null,
                urls = emptyList(),
                imageUrl = null,
                nameVariations = emptyList(),
                aliases = emptyList(),
                groups = emptyList(),
                members = emptyList()
            )
        )
        val viewModel = createViewModel(artistId = 1)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ArtistDetailEvent.OnNavigateBack)
            assertEquals(ArtistDetailScreenEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(artistId: Int): ArtistDetailViewModel {
        return ArtistDetailViewModel(
            getArtistDetailUseCase = getArtistDetailUseCase,
            errorMessageMapper = errorMessageMapper,
            savedStateHandle = SavedStateHandle(
                mapOf(Routes.ARTIST_ID to artistId)
            )
        )
    }
}
