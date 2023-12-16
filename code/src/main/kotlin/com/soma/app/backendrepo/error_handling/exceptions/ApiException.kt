package com.soma.app.backendrepo.error_handling.exceptions
import com.soma.app.backendrepo.utils.ApiError
import kotlin.Exception
data class ApiException(val apiError: ApiError, val status: Int): Exception(apiError.message)

