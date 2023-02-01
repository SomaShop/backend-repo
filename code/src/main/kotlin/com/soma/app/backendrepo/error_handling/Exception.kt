package com.soma.app.backendrepo.error_handling

import kotlin.Exception

data class Exception(val errorMessage: String?) : Exception(errorMessage)
