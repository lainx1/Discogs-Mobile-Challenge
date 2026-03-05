package com.lain.soft.claramobilechallenge.ui.state

import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.ArtistRelease
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.ui.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ArtistReleasesState(
    val screenState: ArtistReleasesScreenState = ArtistReleasesScreenState.Loading,
    val releases: Flow<PagingData<ArtistRelease>> = emptyFlow(),
    val selectedFilters: ArtistReleaseFilters = ArtistReleaseFilters()
)

sealed class ArtistReleasesScreenState {
    data object Loading : ArtistReleasesScreenState()
    data object Success : ArtistReleasesScreenState()
    data class Error(val message: UiText) : ArtistReleasesScreenState()
}

sealed class ArtistReleasesEvent {
    data object OnNavigateBack : ArtistReleasesEvent()
    data class OnYearFilterChange(val year: Int?) : ArtistReleasesEvent()
    data class OnGenreFilterChange(val genre: String?) : ArtistReleasesEvent()
    data class OnLabelFilterChange(val label: String?) : ArtistReleasesEvent()
    data object OnResetFilters : ArtistReleasesEvent()
    data class OnListError(val exception: Throwable) : ArtistReleasesEvent()
    data object OnRetry : ArtistReleasesEvent()
}

sealed class ArtistReleasesScreenEffect {
    data object NavigateBack : ArtistReleasesScreenEffect()
}
