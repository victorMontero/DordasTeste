package com.tps.challenge.network.repository.storepository

import com.tps.challenge.Constants
import com.tps.challenge.network.repository.common.ApiResult
import com.tps.challenge.network.TPSCoroutineService
import com.tps.challenge.network.model.StoreResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StoreRepository @Inject constructor(val service: TPSCoroutineService) {
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
}