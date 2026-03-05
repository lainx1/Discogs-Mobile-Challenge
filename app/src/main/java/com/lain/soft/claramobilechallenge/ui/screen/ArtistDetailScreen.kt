@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.lain.soft.claramobilechallenge.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.ui.component.AlertDialog
import com.lain.soft.claramobilechallenge.ui.component.ArtistReferenceCard
import com.lain.soft.claramobilechallenge.ui.component.AppTopBar
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenState.Error
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenState
import com.lain.soft.claramobilechallenge.ui.viewmodel.ArtistDetailViewModel
import com.lain.soft.claramobilechallenge.ui.component.ArtistInfoCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ArtistDetailScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
    onOpenReleases: (Int, String) -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    HandleNavigationEffect(viewModel, onBack, onOpenReleases)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.artist_detail_screen_title),
                onBack = { viewModel.onEvent(ArtistDetailEvent.OnNavigateBack) },
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = "artist_shared_topbar"
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }
    ) { paddingValues ->
        when (state.screenState) {
            ArtistDetailScreenState.Loading ->
                ArtistDetailLoadingContent(
                    modifier = Modifier.padding(paddingValues)
                )

            is ArtistDetailScreenState.Success -> {
                state.artist?.let {
                    ArtistDetailSuccessContent(
                        artist = it,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.padding(paddingValues),
                        onOpenReleases = { id, name ->
                            viewModel.onEvent(ArtistDetailEvent.OnOpenReleases(id, name))
                        }
                    )
                }
            }
            is ArtistDetailScreenState.Error ->
                ArtistDetailErrorContent(
                    screenState = state.screenState,
                    modifier = Modifier.padding(paddingValues),
                    onRetry = { viewModel.onEvent(ArtistDetailEvent.Retry) }
                )
        }
    }
}

@Composable
private fun ArtistDetailLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ArtistDetailErrorContent(
    screenState: Error,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = screenState.message.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        AlertDialog(
            onDismissRequest = onRetry,
            text = screenState.message.asString(),
            confirmationText = stringResource(R.string.alert_dialog_retry_text),
            icon = Icons.Filled.Error
        )
    }
}

@Composable
private fun SharedTransitionScope.ArtistDetailSuccessContent(
    artist: ArtistDetail,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onOpenReleases: (Int, String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            AsyncImage(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = "artist_image_${artist.id}"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .height(250.dp),
                model = artist.imageUrl,
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.artist_placeholder),
                error = painterResource(R.drawable.artist_placeholder),
                fallback = painterResource(R.drawable.artist_placeholder)
            )
        }

        item {
            Text(
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = "artist_name_${artist.id}"
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
                text = artist.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpenReleases(artist.id, artist.name) }
            ) {
                Text(text = stringResource(R.string.artist_detail_open_releases_button))
            }
        }

        item {
            ArtistInfoCard(
                title = stringResource(R.string.artist_detail_biography_title),
                value = artist.biographySummary.ifBlank {
                    stringResource(R.string.artist_detail_missing_value)
                }
            )
        }

        if (artist.realName.isNullOrBlank().not()) {
            item {
                ArtistInfoCard(
                    title = stringResource(R.string.artist_detail_real_name_title),
                    value = artist.realName
                )
            }
        }

        if (artist.urls.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.artist_detail_urls_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(artist.urls, key = { it }) { url ->
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        uriHandler.openUri(url)
                    }
                )
            }
        }

        if (artist.nameVariations.isNotEmpty()) {
            item {
                ArtistInfoCard(
                    title = stringResource(R.string.artist_detail_name_variations_title),
                    value = artist.nameVariations.joinToString(separator = ", ")
                )
            }
        }

        if (artist.aliases.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.artist_detail_aliases_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(artist.aliases, key = { alias -> "${alias.id}_${alias.name}" }) {
                        ArtistReferenceCard(it.name, it.thumbnailUrl)
                    }
                }
            }
        }

        if (artist.groups.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.artist_detail_groups_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(artist.groups, key = { group -> "${group.id}_${group.name}" }) {
                        ArtistReferenceCard(it.name, it.thumbnailUrl)
                    }
                }
            }
        }

        if (artist.members.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.artist_detail_members_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(artist.members, key = { member -> "${member.id}_${member.name}" }) {
                        ArtistReferenceCard(it.name, it.thumbnailUrl)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HandleNavigationEffect(
    viewModel: ArtistDetailViewModel,
    onBack: () -> Unit,
    onOpenReleases: (Int, String) -> Unit
){
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when(it){
                ArtistDetailScreenEffect.NavigateBack -> onBack()
                is ArtistDetailScreenEffect.OpenReleases -> onOpenReleases(it.id, it.name)
            }
        }
    }
}
