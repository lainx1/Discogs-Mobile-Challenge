package com.lain.soft.claramobilechallenge.ui.state

import androidx.compose.ui.Alignment
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.ui.util.UiText
import kotlinx.coroutines.flow.Flow

data class SearchArtistState(
    val query: String,
    val state: SearchArtistScreenState,
    val artists: Flow<PagingData<Artist>>,
    val loadingArtists: List<Artist> = emptyList(),
    val searchBarEnabled: Boolean,
    val resultsContainerAlignment: Alignment = Alignment.Center
)

sealed class SearchArtistScreenState {
    data object Idle : SearchArtistScreenState()
    data object Loading: SearchArtistScreenState()
    data object Success : SearchArtistScreenState()
    data class Error(val message: UiText) : SearchArtistScreenState()
}

sealed class SearchArtistScreenEvent {
    data class OnQueryChange(val query: String) : SearchArtistScreenEvent()
    data class OnListError(val exception: Throwable) : SearchArtistScreenEvent()
    data class OnNavigateToDetail(val id: Int) : SearchArtistScreenEvent()
}

sealed class SearchArtistScreenEffect {
    data class NavigateToDetail(val id: Int) : SearchArtistScreenEffect()
}
