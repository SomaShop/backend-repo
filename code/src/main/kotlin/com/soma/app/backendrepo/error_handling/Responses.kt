package com.soma.app.backendrepo.error_handling

data class ErrorResponse(
    val errorCode: String,
    val errorMessage: String?
)
data class ApiResponse(
    val status: String,
    val data: Any? = null,
    val error: ErrorResponse? = null
)
