package com.tps.challenge.features.storefeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tps.challenge.core.ui.Intent
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.database.repository.StoreRepository
import com.tps.challenge.network.ApiResult
import com.tps.challenge.network.model.StoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class StoreFeedViewModel @Inject constructor(private val repository: StoreRepository) :
    ViewModel() {

    sealed class StoreFeedIntent : Intent {
        object LoadStores : StoreFeedIntent()
        object RefreshStores : StoreFeedIntent()
    }

    private val _uiState = MutableStateFlow<UiState<List<StoreResponse>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<StoreResponse>>> = _uiState

    fun processIntent(intent: Intent) {
        when (intent) {
            is StoreFeedIntent.LoadStores,
            is StoreFeedIntent.RefreshStores -> loadStores()

            else -> {}
        }
    }

    init {
        processIntent(StoreFeedIntent.LoadStores)
    }

    private fun loadStores() {
        repository.getStoreFeed()
            .onEach { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _uiState.update { UiState.Loading }
                    }
                    is ApiResult.Success -> {
                        _uiState.update { UiState.Success(result.data) }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            UiState.Error(
                                result.message.message ?: "Failed to load stores",
                                result.message.cause
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}