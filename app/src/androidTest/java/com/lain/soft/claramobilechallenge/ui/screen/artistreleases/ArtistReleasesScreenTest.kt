package com.lain.soft.claramobilechallenge.ui.screen.artistreleases

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistReleasesUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import java.net.URLDecoder
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.screen.FakeArtistRepository
import com.lain.soft.claramobilechallenge.ui.theme.ClaraMobileChallengeTheme
import com.lain.soft.claramobilechallenge.ui.viewmodel.ArtistReleasesViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets

@RunWith(AndroidJUnit4::class)
class ArtistReleasesScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rendersTopBarAndErrorState() {
        val viewModel = createArtistReleasesViewModel()

        setContent {
            ArtistReleasesScreen(
                onBack = {},
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.artist_releases_screen_title)
        ).assertIsDisplayed()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.app_generic_unknown_error)
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun backButtonInvokesOnBackCallback() {
        var backInvocations = 0
        val viewModel = createArtistReleasesViewModel()

        setContent {
            ArtistReleasesScreen(
                onBack = { backInvocations++ },
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.top_app_bar_arrow_back)
        ).performClick()
        composeRule.waitForIdle()

        assertEquals(1, backInvocations)
    }

    private fun createArtistReleasesViewModel(): ArtistReleasesViewModel {
        val repository = FakeArtistRepository(
            artistReleasesProvider = { _, _ ->
                throw RuntimeException("forced error")
            }
        )
        return ArtistReleasesViewModel(
            getArtistReleasesUseCase = GetArtistReleasesUseCase(repository),
            errorMessageMapper = ErrorMessageMapper(),
            routeArgDecoder = { value ->
                URLDecoder.decode(value, StandardCharsets.UTF_8.name())
            },
            savedStateHandle = SavedStateHandle(
                mapOf(Routes.ARTIST_NAME to "AC%2FDC")
            )
        )
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            ClaraMobileChallengeTheme {
                content()
            }
        }
    }
}
