package com.soma.app.backendrepo.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class CustomResponseEntity<T> (
    val statusCode: HttpStatus,
    val responseData: T,
    val extraMetaData: Map<String, String>? = null
) : ResponseEntity<T>(responseData, statusCode) {

    companion object {
        fun <T> ok(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.OK, value, header)
        }
        fun <T> badRequest(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.BAD_REQUEST, value, header)
        }
        fun <T> notFound(data: T , header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.NOT_FOUND, data, header)
        }
        fun <T> internalServerError(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, value, header)
        }

        fun <T> forbidden(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.FORBIDDEN, value, header)
        }

        fun <T> unauthorized(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.UNAUTHORIZED, value, header)
        }

        fun <T> conflict(value: T, header: Map<String, String>? = null): CustomResponseEntity<T> {
            return CustomResponseEntity(HttpStatus.CONFLICT, value, header)
        }
    }
}

