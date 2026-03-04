package com.lain.soft.claramobilechallenge.ui.viewmodel

import androidx.compose.ui.Alignment
import androidx.lifecycle.viewModelScope
import com.lain.soft.claramobilechallenge.domain.usecase.PopulateLoadingArtistsUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.SearchArtistUseCase
import com.lain.soft.claramobilechallenge.domain.usecase.ValidateSearchQueryUseCase
import com.lain.soft.claramobilechallenge.ui.mapper.ErrorMessageMapper
import com.lain.soft.claramobilechallenge.ui.state.Event
import com.lain.soft.claramobilechallenge.ui.state.MainScreenState
import com.lain.soft.claramobilechallenge.ui.state.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchArtistViewModel @Inject constructor(
    private val populateLoadingArtistsUseCase: PopulateLoadingArtistsUseCase,
    private val validateSearchQueryUseCase: ValidateSearchQueryUseCase,
    private val searchArtistUseCase: SearchArtistUseCase,
    private val errorMessageMapper: ErrorMessageMapper
) : StateMachineViewModel<MainScreenState>(
    initialState = MainScreenState(
        query = "",
        screenState = ScreenState.Idle,
        artists = emptyFlow(),
        searchBarEnabled = true,
        resultsContainerAlignment = Alignment.Center
    )
) {

    init {
        viewModelScope.launch {
            populateLoadingArtistsUseCase(8)
                .onSuccess {
                    setState { current -> current.copy(loadingArtists = it) }
                }
        }
    }

    private var searchJob: Job? = null

    fun onEvent(event: Event){
        when (event){
            is Event.OnQueryChange -> onQueryChange(event.query)
            is Event.OnListError -> onListError(event.exception)
        }
    }

    private fun onListError(exception: Throwable){
        viewModelScope.launch {
            getErrorMessage(exception)
        }
    }

    private fun clearText() {
        cancelSearchJob()
        setState { current ->
            current.copy(
                query = "",
                screenState = ScreenState.Idle,
                resultsContainerAlignment = Alignment.Center
            )
        }
    }

    private fun onQueryChange(query: String){
        setState { current -> current.copy(query = query) }
        viewModelScope.launch {
            validateSearchQueryUseCase(query)
                .onSuccess { performSearchJob(it) }
                .onFailure { clearText() }
        }
    }

    private fun performSearchJob(query: String) {
        cancelSearchJob()
        searchJob =
            viewModelScope.launch {
                delay(500L)
                performSearch(query)
            }
    }

    private fun performSearch(query: String){
        viewModelScope.launch {
            setState { current ->
                current.copy(
                    screenState = ScreenState.Loading,
                    searchBarEnabled = false,
                    resultsContainerAlignment = Alignment.Center
                )
            }
            searchArtistUseCase(query).onSuccess {
                setState { current ->
                    current.copy(
                        artists = it,
                        screenState = ScreenState.Success,
                        searchBarEnabled = true,
                        resultsContainerAlignment = Alignment.TopCenter
                    )
                }
            }.onFailure { getErrorMessage(it) }
        }
    }

    private fun cancelSearchJob() {
        searchJob?.cancel()
    }

    private fun getErrorMessage(exception: Throwable){
        val errorMessage = errorMessageMapper.map(exception)
        setState { current ->
            current.copy(
                screenState = ScreenState.Error(errorMessage),
                searchBarEnabled = true,
                resultsContainerAlignment = Alignment.Center
            )
        }
    }
}
