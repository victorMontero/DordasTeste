package com.tps.challenge.network.repository.storepository

import com.tps.challenge.network.TPSCoroutineService
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.model.StoreResponse
import com.tps.challenge.network.repository.common.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(val service: TPSCoroutineService) : StoreRepository {
    override fun getStoreFeed(
        latitude: Double,
        longitude: Double
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

    override fun getStoreDetails(storeId: String): Flow<ApiResult<StoreDetailsResponse>> = flow {
        try {
            val response = service.getStoreDetails(storeId)
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(e))
        }
    }
}