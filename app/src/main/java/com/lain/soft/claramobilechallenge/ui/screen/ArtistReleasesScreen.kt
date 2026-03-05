package com.lain.soft.claramobilechallenge.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.ui.component.AlertDialog
import com.lain.soft.claramobilechallenge.ui.component.AppTopBar
import com.lain.soft.claramobilechallenge.ui.component.FilterInputDialog
import com.lain.soft.claramobilechallenge.ui.component.ReleaseListItem
import com.lain.soft.claramobilechallenge.ui.mapper.displayGenres
import com.lain.soft.claramobilechallenge.ui.mapper.displayLabels
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenState
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesState
import com.lain.soft.claramobilechallenge.ui.viewmodel.ArtistReleasesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistReleasesScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ArtistReleasesViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val releases = state.releases.collectAsLazyPagingItems()

    HandleNavigationEffect(
        viewModel = viewModel,
        onBack = onBack
    )

    HandlePagingErrorEffect(
        loadState = releases.loadState,
        onListError = { viewModel.onEvent(ArtistReleasesEvent.OnListError(it)) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.artist_releases_screen_title),
                onBack = {
                    viewModel.onEvent(ArtistReleasesEvent.OnNavigateBack)
                }
            )
        }
    ) { paddingValues ->
        when (val screenState = state.screenState) {
            ArtistReleasesScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ArtistReleasesScreenState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = screenState.message.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    AlertDialog(
                        onDismissRequest = { viewModel.onEvent(ArtistReleasesEvent.OnRetry) },
                        text = screenState.message.asString(),
                        confirmationText = stringResource(R.string.alert_dialog_retry_text),
                        icon = Icons.Filled.Error
                    )
                }
            }

            ArtistReleasesScreenState.Success -> {
                ArtistReleasesSuccessContent(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    state = state,
                    releases = releases,
                    onYearFilterChange = {
                        viewModel.onEvent(ArtistReleasesEvent.OnYearFilterChange(it))
                    },
                    onGenreFilterChange = {
                        viewModel.onEvent(ArtistReleasesEvent.OnGenreFilterChange(it))
                    },
                    onLabelFilterChange = {
                        viewModel.onEvent(ArtistReleasesEvent.OnLabelFilterChange(it))
                    },
                    onResetFilters = {
                        viewModel.onEvent(ArtistReleasesEvent.OnResetFilters)
                    }
                )
            }
        }
    }
}

@Composable
private fun ArtistReleasesSuccessContent(
    modifier: Modifier = Modifier,
    state: ArtistReleasesState,
    releases: androidx.paging.compose.LazyPagingItems<ArtistRelease>,
    onYearFilterChange: (Int?) -> Unit,
    onGenreFilterChange: (String?) -> Unit,
    onLabelFilterChange: (String?) -> Unit,
    onResetFilters: () -> Unit
) {
    val hasActiveFilters = state.selectedFilters.year != null ||
        !state.selectedFilters.genre.isNullOrBlank() ||
        !state.selectedFilters.label.isNullOrBlank()

    var showYearDialog by rememberSaveable { mutableStateOf(false) }
    var showGenreDialog by rememberSaveable { mutableStateOf(false) }
    var showLabelDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterBar(
            year = state.selectedFilters.year,
            genre = state.selectedFilters.genre,
            label = state.selectedFilters.label,
            onYearClick = { showYearDialog = true },
            onGenreClick = { showGenreDialog = true },
            onLabelClick = { showLabelDialog = true },
            onReset = onResetFilters
        )

        when {
            releases.loadState.refresh is LoadState.Loading && releases.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            releases.loadState.refresh is LoadState.NotLoading && releases.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasActiveFilters) {
                            stringResource(R.string.artist_releases_filters_empty_text)
                        } else {
                            stringResource(R.string.artist_releases_empty_text)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        count = releases.itemCount,
                        key = { index ->
                            releases.peek(index)?.key ?: "release_$index"
                        },
                        contentType = releases.itemContentType { "release_item" }
                    ) { index ->
                        releases[index]?.let { release ->
                            ReleaseListItem(
                                title = release.title ?: stringResource(R.string.artist_releases_unknown_title),
                                year = release.year?.toString()
                                    ?: stringResource(R.string.artist_releases_unknown_year),
                                formatOrType = release.format
                                    ?: release.type
                                    ?: stringResource(R.string.artist_releases_unknown_format),
                                genres = release.displayGenres(),
                                labels = release.displayLabels(),
                                thumbnail = release.thumb
                            )
                        }
                    }

                    if (releases.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showYearDialog) {
        FilterInputDialog(
            title = stringResource(R.string.artist_releases_filter_year),
            initialText = state.selectedFilters.year?.toString().orEmpty(),
            onDismiss = { showYearDialog = false },
            onApply = {
                onYearFilterChange(it.trim().toIntOrNull())
                showYearDialog = false
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }

    if (showGenreDialog) {
        FilterInputDialog(
            title = stringResource(R.string.artist_releases_filter_genre),
            initialText = state.selectedFilters.genre.orEmpty(),
            onDismiss = { showGenreDialog = false },
            onApply = { text ->
                onGenreFilterChange(text.trim().takeIf { it.isNotBlank() })
                showGenreDialog = false
            }
        )
    }

    if (showLabelDialog) {
        FilterInputDialog(
            title = stringResource(R.string.artist_releases_filter_label),
            initialText = state.selectedFilters.label.orEmpty(),
            onDismiss = { showLabelDialog = false },
            onApply = { text ->
                onLabelFilterChange(text.trim().takeIf { it.isNotBlank() })
                showLabelDialog = false
            }
        )
    }
}

@Composable
private fun FilterBar(
    year: Int?,
    genre: String?,
    label: String?,
    onYearClick: () -> Unit,
    onGenreClick: () -> Unit,
    onLabelClick: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = year != null,
            onClick = onYearClick,
            label = {
                Text(
                    text = year?.toString()
                        ?: stringResource(R.string.artist_releases_filter_year)
                )
            }
        )
        FilterChip(
            selected = !genre.isNullOrBlank(),
            onClick = onGenreClick,
            label = {
                Text(
                    text = genre ?: stringResource(R.string.artist_releases_filter_genre)
                )
            }
        )
        FilterChip(
            selected = !label.isNullOrBlank(),
            onClick = onLabelClick,
            label = {
                Text(
                    text = label ?: stringResource(R.string.artist_releases_filter_label)
                )
            }
        )
        TextButton(onClick = onReset) {
            Text(
                text = stringResource(R.string.artist_releases_filter_reset),
                fontWeight = FontWeight.SemiBold
            )
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
    viewModel: ArtistReleasesViewModel,
    onBack: () -> Unit
){
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when(it){
                ArtistReleasesScreenEffect.NavigateBack -> onBack()
            }
        }
    }
}
