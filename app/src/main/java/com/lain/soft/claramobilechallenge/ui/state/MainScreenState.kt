package com.lain.soft.claramobilechallenge.ui.state

import androidx.compose.ui.Alignment
import androidx.paging.PagingData
import com.lain.soft.claramobilechallenge.domain.model.Artist
import com.lain.soft.claramobilechallenge.ui.util.UiText
import kotlinx.coroutines.flow.Flow

data class MainScreenState(
    val query: String,
    val screenState: ScreenState,
    val artists: Flow<PagingData<Artist>>,
    val loadingArtists: List<Artist> = emptyList(),
    val searchBarEnabled: Boolean,
    val resultsContainerAlignment: Alignment = Alignment.Center
)

sealed class ScreenState {
    data object Idle : ScreenState()
    data object Loading: ScreenState()
    data object Success : ScreenState()
    data class Error(val message: UiText) : ScreenState()
}

sealed class Event {
    data class OnQueryChange(val query: String) : Event()
    data class OnListError(val exception: Throwable) : Event()
}

sealed class Effect {
    data class NavigateToDetail(val id: Int) : Effect()
}
