@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.lain.soft.claramobilechallenge.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.LazyPagingItems
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.ui.component.ArtistListItem
import com.lain.soft.claramobilechallenge.ui.component.AlertDialog
import com.lain.soft.claramobilechallenge.ui.component.SearchBar
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenEvent
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistState
import com.lain.soft.claramobilechallenge.ui.state.SearchArtistScreenState
import com.lain.soft.claramobilechallenge.ui.viewmodel.SearchArtistViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SearchArtistScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onArtistClick: (Int) -> Unit,
    viewModel: SearchArtistViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val artists = state.artists.collectAsLazyPagingItems()

    HandlePagingErrorEffect(
        loadState = artists.loadState,
        onListError = { viewModel.onEvent(SearchArtistScreenEvent.OnListError(it)) }
    )

    HandleNavigationEffect(
        viewModel = viewModel,
        onArtistClick = onArtistClick
    )

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        SearchArtistBody(
            state = state,
            artists = artists,
            animatedVisibilityScope = animatedVisibilityScope,
            paddingValues = paddingValues,
            onQueryChange = { viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange(it)) },
            onClearQuery = { viewModel.onEvent(SearchArtistScreenEvent.OnQueryChange("")) },
            onNavigateToDetail = { id ->
                viewModel.onEvent(SearchArtistScreenEvent.OnNavigateToDetail(id))
            }
        )
    }
}

@Composable
private fun SharedTransitionScope.SearchArtistBody(
    state: SearchArtistState,
    artists: LazyPagingItems<Artist>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .imePadding()
    ) {
        val (searchBar, resultContainer) = createRefs()

        SearchBar(
            modifier = Modifier
                .constrainAs(searchBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(horizontal = 8.dp),
            query = state.query,
            enabled = state.searchBarEnabled,
            onQueryChange = onQueryChange
        )

        SearchArtistResultContainer(
            state = state,
            artists = artists,
            animatedVisibilityScope = animatedVisibilityScope,
            onClearQuery = onClearQuery,
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.constrainAs(resultContainer) {
                top.linkTo(searchBar.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
private fun SharedTransitionScope.SearchArtistResultContainer(
    state: SearchArtistState,
    artists: LazyPagingItems<Artist>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClearQuery: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = state.resultsContainerAlignment,
        modifier = modifier
    ) {
        when (state.state) {
            SearchArtistScreenState.Idle -> SearchArtistIdleContent()
            SearchArtistScreenState.Loading -> CircularProgressIndicator()
            SearchArtistScreenState.Success ->
                SearchArtistSuccessContent(
                    state = state,
                    artists = artists,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onNavigateToDetail = onNavigateToDetail
                )
            is SearchArtistScreenState.Error -> {
                SearchArtistErrorText(message = state.state.message.asString())
                AlertDialog(
                    onDismissRequest = onClearQuery,
                    text = state.state.message.asString(),
                    confirmationText = stringResource(R.string.alert_dialog_confirmation_text),
                    icon = Icons.Filled.Error
                )
            }
        }
    }
}

@Composable
private fun SearchArtistIdleContent() {
    Text(
        text = stringResource(R.string.main_screen_idle_state_text),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun SearchArtistErrorText(
    message: String
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun SharedTransitionScope.SearchArtistSuccessContent(
    state: SearchArtistState,
    artists: LazyPagingItems<Artist>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToDetail: (Int) -> Unit
) {
    val loadState = artists.loadState
    when (loadState.refresh) {
        is LoadState.Loading if artists.itemCount == 0 -> SearchArtistLoadingOrEmpty(isLoading = true)
        is LoadState.NotLoading if artists.itemCount == 0 -> SearchArtistLoadingOrEmpty(isLoading = false)
        else -> SearchArtistGridContent(
            state = state,
            artists = artists,
            animatedVisibilityScope = animatedVisibilityScope,
            onNavigateToDetail = onNavigateToDetail
        )
    }
}

@Composable
private fun SearchArtistLoadingOrEmpty(
    isLoading: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = stringResource(R.string.main_screen_empty_state_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.SearchArtistGridContent(
    state: SearchArtistState,
    artists: LazyPagingItems<Artist>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToDetail: (Int) -> Unit
) {
    val loadState = artists.loadState
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            count = artists.itemCount,
            key = { index ->
                val artist = artists.peek(index)
                artist?.let { "artist_${it.id}" } ?: "placeholder_$index"
            },
            contentType = artists.itemContentType { "Artist" }
        ) { index ->
            artists[index]?.let { artist ->
                ArtistListItem(
                    id = artist.id,
                    name = artist.name,
                    thumbnail = artist.thumbnail,
                    imageModifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = "artist_image_${artist.id}"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                    nameModifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = "artist_name_${artist.id}"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                    onClick = onNavigateToDetail
                )
            }
        }

        if (loadState.append is LoadState.Loading) {
            items(
                items = state.loadingArtists,
                key = { "shimmer_${it.id}" }
            ) { shimmer ->
                ArtistListItem(
                    id = shimmer.id,
                    name = shimmer.name,
                    thumbnail = shimmer.thumbnail,
                    isLoading = true,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun HandlePagingErrorEffect(
    loadState: CombinedLoadStates,
    onListError: (Throwable) -> Unit
) {
    LaunchedEffect(loadState) {
        val errorState = loadState.refresh as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error

        errorState?.let {
            onListError(it.error)
        }
    }
}

@Composable
private fun HandleNavigationEffect(
    viewModel: SearchArtistViewModel,
    onArtistClick: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is SearchArtistScreenEffect.NavigateToDetail -> {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onArtistClick(it.id)
                }
            }
        }
    }
}
