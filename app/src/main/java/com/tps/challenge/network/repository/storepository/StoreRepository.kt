package com.tps.challenge.network.repository.storepository

import com.tps.challenge.Constants
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.model.StoreResponse
import com.tps.challenge.network.repository.common.ApiResult
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStoreFeed(
        latitude: Double = Constants.DEFAULT_LATITUDE,
        longitude: Double = Constants.DEFAULT_LONGITUDE
    ): Flow<ApiResult<List<StoreResponse>>>

    fun getStoreDetails(storeId: String) : Flow<ApiResult<StoreDetailsResponse>>
}