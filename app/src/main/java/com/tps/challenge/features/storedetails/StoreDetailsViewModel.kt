package com.tps.challenge.features.storedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tps.challenge.core.ui.Intent
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.repository.common.ApiResult
import com.tps.challenge.network.repository.storepository.StoreRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class StoreDetailsViewModel @Inject constructor(private val repository: StoreRepository) :
    ViewModel() {

    sealed class StoreDetailsIntent : Intent {
        data class LoadStoreDetails(val storeId: String) : StoreDetailsIntent()
    }

    private val _uiState = MutableStateFlow<UiState<StoreDetailsResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<StoreDetailsResponse>> = _uiState

    private var currentStoreId: String? = null

    fun processIntent(intent: Intent) {
        when (intent) {
            is StoreDetailsIntent.LoadStoreDetails -> {
                currentStoreId = intent.storeId
                loadStoreDetails(intent.storeId)
            }

            else -> {}
        }
    }

    private fun loadStoreDetails(storeId: String) {
        repository.getStoreDetails(storeId)
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}