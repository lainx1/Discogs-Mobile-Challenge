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
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.domain.usecase.PopulateLoadingArtistsUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.SearchArtistUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.ValidateSearchQueryUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.theme.ClaraMobileChallengeTheme
import com.lain.soft.claramobilechallenge.ui.viewmodel.SearchArtistViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchArtistScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsIdleStateOnLaunch() {
        val viewModel = createSearchViewModel(
            repository = FakeArtistRepository()
        )

        setSharedContent { animatedVisibilityScope ->
            SearchArtistScreen(
                animatedVisibilityScope = animatedVisibilityScope,
                onArtistClick = {},
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.main_screen_idle_state_text)
        ).assertIsDisplayed()
    }

    @Test
    fun searchAndClickArtistTriggersNavigationCallback() {
        var clickedArtistId: Int? = null
        val viewModel = createSearchViewModel(
            repository = FakeArtistRepository(
                searchArtistsProvider = {
                    flowOf(PagingData.from(listOf(Artist(id = 7, thumbnail = null, name = "ABBA"))))
                }
            )
        )

        setSharedContent { animatedVisibilityScope ->
            SearchArtistScreen(
                animatedVisibilityScope = animatedVisibilityScope,
                onArtistClick = { clickedArtistId = it },
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.search_bar_placeholder)
        ).performTextInput("abba")

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("ABBA").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("ABBA").performClick()
        composeRule.waitForIdle()

        assertEquals(7, clickedArtistId)
    }

    private fun createSearchViewModel(repository: FakeArtistRepository): SearchArtistViewModel =
        SearchArtistViewModel(
            populateLoadingArtistsUseCase = PopulateLoadingArtistsUseCase(),
            validateSearchQueryUseCase = ValidateSearchQueryUseCase(),
            searchArtistUseCase = SearchArtistUseCase(repository),
            errorMessageMapper = ErrorMessageMapper()
        )

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
