package com.tps.challenge.features.storefeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tps.challenge.database.repository.StoreFeedRepository
import com.tps.challenge.network.model.StoreResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreFeedViewModel @Inject constructor (val repository: StoreFeedRepository ): ViewModel() {

    sealed class UiState{
        object Loading: UiState()
        data class Success(val stores: List<StoreResponse>): UiState()
        data class Error(val message: String): UiState()
    }

    sealed class Event {
        object RefreshRequested: Event()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun onEvent(event: Event){
        when(event){
            is Event.RefreshRequested -> loadStores()
        }
    }

    init {
        loadStores()
    }

    private fun loadStores() {
        viewModelScope.launch {
            _uiState.update {
                UiState.Loading
            }

            repository.getStoreFeed()
                .onSuccess { stores ->
                    _uiState.update {
                        UiState.Success(stores)
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        UiState.Error(exception.message ?: "Failed to load stores")
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}