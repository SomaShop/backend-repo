package com.soma.app.backendrepo.utils

sealed class RequestResponse<T> {
    data class Success<T>(val data: T) : RequestResponse<T>()
    data class Error<T>(val message: String) : RequestResponse<T>()
}
