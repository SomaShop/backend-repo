package com.soma.app.backendrepo.utils

// TODO: Remove RequestResponse and its usages and replace with ApiResult
sealed class RequestResponse<T> {
    data class Success<T>(val data: T) : RequestResponse<T>()
    data class Error<T>(val message: String) : RequestResponse<T>()
}

sealed class ApiResult {
    data class Success(val data: ApiData) : ApiResult()
    data class Error(val error: ApiError) : ApiResult()
}

data class ApiData(val response: Any)
data class ApiError(val message: String?, val errorCode: String)
