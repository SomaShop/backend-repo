package com.soma.app.backendrepo.error_handling
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiError
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.io.IOException
import jakarta.servlet.ServletException
import kotlin.Exception
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
@ControllerAdvice
class RestControllerException {

    @ExceptionHandler(value = [ApiException::class])
    fun handleApiException(ex: ApiException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity(ex.apiError, HttpHeaders(), ex.status)
    }

    @ExceptionHandler(value = [ExpiredJwtException::class])
    fun handleExpiredJwtException(ex: ExpiredJwtException, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(message = "JWT access Token has expired", errorCode = ErrorCode.JWT_TOKEN_EXPIRED.name)
        return ResponseEntity(apiError, HttpHeaders(), 401)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(message = ex.message, errorCode = ErrorCode.INTERNAL_SERVER_ERROR.name)
        return ResponseEntity(apiError, HttpHeaders(), 500)
    }

    @ExceptionHandler(value = [ServletException::class, IOException::class])
    fun handleJwtFilterException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(message = ex.message, errorCode = ErrorCode.USER_NOT_FOUND.name)
        return ResponseEntity(apiError, HttpHeaders(), 404)
    }
}