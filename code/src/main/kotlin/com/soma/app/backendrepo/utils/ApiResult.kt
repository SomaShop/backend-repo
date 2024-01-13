package com.soma.app.backendrepo.utils

import com.stripe.model.StripeError

sealed class ApiResult {
    data class Success(val data: ApiData) : ApiResult()
    data class Error(val error: Any) : ApiResult()
}

data class ApiData(val response: Any)
data class ApiError(val message: String?, val errorCode: String)

data class STApiError(val status: Int, val stripeApiError: StripeError)
