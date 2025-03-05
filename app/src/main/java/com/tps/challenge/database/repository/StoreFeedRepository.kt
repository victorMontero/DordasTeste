package com.tps.challenge.database.repository

import com.tps.challenge.Constants
import com.tps.challenge.network.TPSCoroutineService
import com.tps.challenge.network.model.StoreResponse
import javax.inject.Inject

class StoreFeedRepository @Inject constructor(val service: TPSCoroutineService) {
    suspend fun getStoreFeed(
        latitude: Double = Constants.DEFAULT_LATITUDE,
        longitude: Double = Constants.DEFAULT_LONGITUDE
    ): Result<List<StoreResponse>> =
        try {
            val response = service.getStoreFeed(
                latitude = latitude,
                longitude = longitude
            )
            Result.success(response)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
}