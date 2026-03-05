package com.lain.soft.claramobilechallenge.ui.screen.artistreleases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenEffect
import com.lain.soft.claramobilechallenge.ui.viewmodel.ArtistReleasesViewModel

@Composable
internal fun ArtistReleasesHandlePagingErrorEffect(
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
internal fun ArtistReleasesHandleNavigationEffect(
    viewModel: ArtistReleasesViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                ArtistReleasesScreenEffect.NavigateBack -> onBack()
            }
        }
    }
}

@Composable
internal fun ReleasesAppendLoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
