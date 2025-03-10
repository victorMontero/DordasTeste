package com.tps.challenge.database.repository

import com.tps.challenge.Constants
import com.tps.challenge.network.ApiResult
import com.tps.challenge.network.TPSCoroutineService
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.model.StoreResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository for store feed and details data.
 */
class StoreRepository @Inject constructor(private val service: TPSCoroutineService) {

    /**
     * Get a list of stores based on location.
     */
    fun getStoreFeed(
        latitude: Double = Constants.DEFAULT_LATITUDE,
        longitude: Double = Constants.DEFAULT_LONGITUDE
    ): Flow<ApiResult<List<StoreResponse>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = service.getStoreFeed(
                latitude, longitude
            )
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(e))
        }
    }


    /**
     * Get detailed information about a specific store.
     */
    fun getStoreDetails(storeId: String): Flow<ApiResult<StoreDetailsResponse>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = service.getStoreDetails(storeId)
            emit(ApiResult.Success(response))
        } catch (exception: Exception) {
            emit(ApiResult.Error(exception))
        }
    }
}