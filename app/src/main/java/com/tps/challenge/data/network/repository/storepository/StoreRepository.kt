package com.tps.challenge.data.network.repository.storepository

import com.tps.challenge.util.Constants
import com.tps.challenge.data.network.model.StoreResponse
import com.tps.challenge.data.network.repository.common.ApiResult
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStoreFeed(
        latitude: Double = Constants.DEFAULT_LATITUDE,
        longitude: Double = Constants.DEFAULT_LONGITUDE
    ): Flow<ApiResult<List<StoreResponse>>>
}