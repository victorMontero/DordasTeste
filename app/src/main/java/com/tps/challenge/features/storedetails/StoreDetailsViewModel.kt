package com.tps.challenge.features.storedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tps.challenge.database.repository.StoreRepository
import com.tps.challenge.network.model.StoreDetailsResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the store details screen, managing state and business logic.
 */
class StoreDetailsViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val storeDetails: StoreDetailsResponse) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Event {
        data class LoadStoreDetails(val storeId: String) : Event()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var currentStoreId: String? = null

    fun onEvent(event: Event) {
        when (event) {
            is Event.LoadStoreDetails -> {
                currentStoreId = event.storeId
                loadStoreDetails(event.storeId)
            }
        }
    }

    private fun loadStoreDetails(storeId: String) {
        viewModelScope.launch {
            _uiState.update {
                UiState.Loading
            }
            repository.getStoreDetails(storeId)
                .onSuccess { details ->
                    _uiState.update {
                        UiState.Success(details)
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        UiState.Error(
                            exception.message ?: "Failed to load store details"
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}