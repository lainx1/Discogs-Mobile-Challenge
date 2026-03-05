package com.lain.soft.claramobilechallenge.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lain.soft.claramobilechallenge.R
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease

@Composable
fun ArtistRelease.displayGenres(): String {
    val text = genres.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.artist_releases_unknown_genres)
    return stringResource(R.string.artist_releases_genres_prefix, text)
}

@Composable
fun ArtistRelease.displayLabels(): String {
    val text = labels.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.artist_releases_unknown_labels)
    return stringResource(R.string.artist_releases_labels_prefix, text)
}
