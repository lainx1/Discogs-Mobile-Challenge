package com.lain.soft.claramobilechallenge.ui.state

import com.lain.soft.claramobilechallenge.domain.model.ArtistDetail
import com.lain.soft.claramobilechallenge.ui.util.UiText

data class ArtistDetailState(
    val screenState: ArtistDetailScreenState,
    val artist: ArtistDetail?
)

sealed class ArtistDetailScreenState {
    data object Loading : ArtistDetailScreenState()
    data object Success : ArtistDetailScreenState()
    data class Error(val message: UiText) : ArtistDetailScreenState()
}

sealed class ArtistDetailEvent {
    data object Retry : ArtistDetailEvent()
    data object OnNavigateBack : ArtistDetailEvent()
    data class OnOpenReleases(val id: Int, val name: String) : ArtistDetailEvent()
}

sealed class ArtistDetailScreenEffect {
    data object NavigateBack : ArtistDetailScreenEffect()
    data class OpenReleases(val id: Int, val name: String) : ArtistDetailScreenEffect()
}
