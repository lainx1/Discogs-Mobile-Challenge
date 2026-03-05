package com.lain.soft.claramobilechallenge.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistDetailUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailScreenState
import com.lain.soft.claramobilechallenge.ui.state.ArtistDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistDetailUseCase: GetArtistDetailUseCase,
    private val errorMessageMapper: ErrorMessageMapper,
    savedStateHandle: SavedStateHandle
) : StateMachineViewModel<ArtistDetailState>(
    initialState = ArtistDetailState(
        screenState = ArtistDetailScreenState.Loading,
        artist = null
    ),
) {
    private val artistId: Int =
        checkNotNull(savedStateHandle.get<Int>(Routes.ARTIST_ID))
    private val _effect = MutableSharedFlow<ArtistDetailScreenEffect>()
    val effect: SharedFlow<ArtistDetailScreenEffect> = _effect


    init {
        loadArtistDetail()
    }

    fun onEvent(event: ArtistDetailEvent) {
        when (event) {
            ArtistDetailEvent.Retry -> loadArtistDetail()
            ArtistDetailEvent.OnNavigateBack -> onNavigateBack()
            is ArtistDetailEvent.OnOpenReleases -> onOpenReleases(event.id, event.name)
        }
    }

    private fun loadArtistDetail() {
        viewModelScope.launch {
            setState { current ->
                current.copy(screenState = ArtistDetailScreenState.Loading)
            }
            getArtistDetailUseCase(artistId)
                .onSuccess {
                    setState { current ->
                        current.copy(
                            screenState = ArtistDetailScreenState.Success,
                            artist = it
                        )
                    }
                }
                .onFailure { throwable ->
                    setState { current ->
                        current.copy(
                            screenState = ArtistDetailScreenState.Error(
                                errorMessageMapper.map(throwable)
                            ),
                            artist = null
                        )
                    }
                }
        }
    }
    private fun onNavigateBack() {
        viewModelScope.launch {
            _effect.emit(ArtistDetailScreenEffect.NavigateBack)
        }
    }

    private fun onOpenReleases(id: Int, name: String) {
        viewModelScope.launch {
            _effect.emit(ArtistDetailScreenEffect.OpenReleases(id, name))
        }
    }
}
