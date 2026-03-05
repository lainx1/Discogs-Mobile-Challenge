package com.lain.soft.claramobilechallenge.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lain.soft.claramobilechallenge.ui.screen.ArtistDetailScreen
import com.lain.soft.claramobilechallenge.ui.screen.ArtistReleasesScreen
import com.lain.soft.claramobilechallenge.ui.screen.SearchArtistScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.SEARCH_ARTIST_ROUTE
        ) {
            composable(Routes.SEARCH_ARTIST_ROUTE) {
                SearchArtistScreen(
                    animatedVisibilityScope = this@composable,
                    onArtistClick = {
                        navController.navigate(Routes.artistDetail(it))
                    }
                )
            }

            composable(
                route = Routes.ARTIST_DETAIL_ROUTE,
                arguments = listOf(
                    navArgument(Routes.ARTIST_ID) { type = NavType.IntType }
                )
            ) {
                ArtistDetailScreen(
                    animatedVisibilityScope = this@composable,
                    onBack = navController::navigateUp,
                    onOpenReleases = { artistId, artistName ->
                        navController.navigate(Routes.artistReleases(artistId, artistName))
                    }
                )
            }

            composable(
                route = Routes.ARTIST_RELEASES_ROUTE,
                arguments = listOf(
                    navArgument(Routes.ARTIST_ID) { type = NavType.IntType },
                    navArgument(Routes.ARTIST_NAME) { type = NavType.StringType }
                )
            ) {
                ArtistReleasesScreen(
                    onBack = navController::navigateUp
                )
            }
        }
    }
}
