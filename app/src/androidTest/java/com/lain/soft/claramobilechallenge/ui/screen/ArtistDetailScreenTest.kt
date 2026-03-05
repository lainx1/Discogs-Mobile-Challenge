@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.lain.soft.claramobilechallenge.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistDetailUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.theme.ClaraMobileChallengeTheme
import com.lain.soft.claramobilechallenge.ui.viewmodel.ArtistDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtistDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rendersTopBarAndBiographyInSuccessState() {
        val viewModel = createArtistDetailViewModel()

        setSharedContent { animatedVisibilityScope ->
            ArtistDetailScreen(
                animatedVisibilityScope = animatedVisibilityScope,
                onBack = {},
                onOpenReleases = { _, _ -> },
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.artist_detail_screen_title)
        ).assertIsDisplayed()
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.artist_detail_biography_title)
        ).assertIsDisplayed()
    }

    @Test
    fun viewReleasesButtonInvokesCallback() {
        var receivedId: Int? = null
        var receivedName: String? = null
        val viewModel = createArtistDetailViewModel()

        setSharedContent { animatedVisibilityScope ->
            ArtistDetailScreen(
                animatedVisibilityScope = animatedVisibilityScope,
                onBack = {},
                onOpenReleases = { id, name ->
                    receivedId = id
                    receivedName = name
                },
                viewModel = viewModel
            )
        }

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("ABBA").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.artist_detail_open_releases_button)
        ).performClick()
        composeRule.waitForIdle()

        assertEquals(1, receivedId)
        assertEquals("ABBA", receivedName)
    }

    private fun createArtistDetailViewModel(): ArtistDetailViewModel {
        val repository = FakeArtistRepository(
            artistDetailProvider = {
                ArtistDetail(
                    id = 1,
                    name = "ABBA",
                    biographySummary = "Swedish pop group",
                    realName = null,
                    urls = emptyList(),
                    imageUrl = null,
                    nameVariations = emptyList(),
                    aliases = emptyList(),
                    groups = emptyList(),
                    members = emptyList()
                )
            }
        )
        return ArtistDetailViewModel(
            getArtistDetailUseCase = GetArtistDetailUseCase(repository),
            errorMessageMapper = ErrorMessageMapper(),
            savedStateHandle = SavedStateHandle(mapOf(Routes.ARTIST_ID to 1))
        )
    }

    private fun setSharedContent(
        content: @Composable SharedTransitionScope.(AnimatedVisibilityScope) -> Unit
    ) {
        composeRule.setContent {
            ClaraMobileChallengeTheme {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        content(this@SharedTransitionLayout, this)
                    }
                }
            }
        }
    }
}
