package com.lain.soft.claramobilechallenge.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lain.soft.claramobilechallenge.domain.model.ArtistReleaseFilters
import com.lain.soft.claramobilechallenge.domain.usecase.GetArtistReleasesUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.navigation.Routes
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesEvent
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenEffect
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesScreenState
import com.lain.soft.claramobilechallenge.ui.state.ArtistReleasesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistReleasesViewModel @Inject constructor(
    private val getArtistReleasesUseCase: GetArtistReleasesUseCase,
    private val errorMessageMapper: ErrorMessageMapper,
    savedStateHandle: SavedStateHandle
) : StateMachineViewModel<ArtistReleasesState>(
    initialState = ArtistReleasesState()
) {

    private val _effect = MutableSharedFlow<ArtistReleasesScreenEffect>()
    val effect: SharedFlow<ArtistReleasesScreenEffect> = _effect

    private val artistName: String = Uri.decode(
        checkNotNull(savedStateHandle.get<String>(Routes.ARTIST_NAME))
    ).trim()
    private var loadReleasesJob: Job? = null

    init {
        loadReleases(filters = state.value.selectedFilters)
    }

    fun onEvent(event: ArtistReleasesEvent) {
        when (event) {
            is ArtistReleasesEvent.OnYearFilterChange ->
                updateFilters(current = state.value.selectedFilters.copy(year = event.year))
            is ArtistReleasesEvent.OnGenreFilterChange ->
                updateFilters(
                    current = state.value.selectedFilters.copy(
                        genre = sanitizeFilterValue(event.genre)
                    )
                )
            is ArtistReleasesEvent.OnLabelFilterChange ->
                updateFilters(
                    current = state.value.selectedFilters.copy(
                        label = sanitizeFilterValue(event.label)
                    )
                )
            ArtistReleasesEvent.OnResetFilters -> updateFilters(current = ArtistReleaseFilters())
            is ArtistReleasesEvent.OnListError -> setError(event.exception)
            ArtistReleasesEvent.OnRetry -> loadReleases(filters = state.value.selectedFilters)
            ArtistReleasesEvent.OnNavigateBack -> onNavigateBack()
        }
    }

    private fun loadReleases(filters: ArtistReleaseFilters) {
        loadReleasesJob?.cancel()
        loadReleasesJob = viewModelScope.launch {
            setState { current ->
                current.copy(screenState = ArtistReleasesScreenState.Loading)
            }
            getArtistReleasesUseCase(
                GetArtistReleasesUseCase.Input(
                    artistName = artistName,
                    filters = filters
                )
            )
                .onSuccess { releases ->
                    setState { current ->
                        current.copy(
                            releases = releases.cachedIn(viewModelScope),
                            screenState = ArtistReleasesScreenState.Success
                        )
                    }
                }
                .onFailure { setError(it) }
        }
    }

    private fun updateFilters(current: ArtistReleaseFilters) {
        setState { state -> state.copy(selectedFilters = current) }
        loadReleases(filters = current)
    }

    private fun sanitizeFilterValue(value: String?): String? =
        value?.trim()?.takeIf { it.isNotBlank() }

    private fun setError(throwable: Throwable) {
        setState { current ->
            current.copy(
                screenState = ArtistReleasesScreenState.Error(
                    errorMessageMapper.map(throwable)
                )
            )
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            _effect.emit(ArtistReleasesScreenEffect.NavigateBack)
        }
    }
}
