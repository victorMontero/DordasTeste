package com.tps.challenge.database.repository

import com.tps.challenge.Constants
import com.tps.challenge.network.TPSCoroutineService
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.model.StoreResponse
import javax.inject.Inject

/**
 * Repository for store feed and details data.
 */
class StoreRepository @Inject constructor(val service: TPSCoroutineService) {

    /**
     * Get a list of stores based on location.
     */
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

    /**
     * Get detailed information about a specific store.
     */
    suspend fun getStoreDetails(storeId: String): Result<StoreDetailsResponse> =
        try {
            val response = service.getStoreDetails(storeId)
            Result.success(response)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
}