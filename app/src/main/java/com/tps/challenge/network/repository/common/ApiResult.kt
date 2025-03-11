package com.tps.challenge.network.repository.common

sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: Throwable) : ApiResult<Nothing>()
}
