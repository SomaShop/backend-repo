package com.soma.app.backendrepo.error_handling
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiError
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import java.security.SignatureException
import kotlin.Exception
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
@ControllerAdvice
class RestControllerException {

    @ExceptionHandler(value = [ApiException::class])
    fun handleApiException(ex: ApiException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity(ex.apiError, HttpHeaders(), ex.status)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(message = ex.message, errorCode = ErrorCode.INTERNAL_SERVER_ERROR.name)
        return ResponseEntity(apiError, HttpHeaders(), 500)
    }

    @ExceptionHandler(value = [
        SignatureException::class, MalformedJwtException::class,
        ExpiredJwtException::class , UnsupportedJwtException::class,
        IllegalArgumentException::class]
    )
    fun handleJwtException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(message = "Could not validate JWT token request",
            errorCode = ErrorCode.JWT_TOKEN_ERROR.name
        )
        return ResponseEntity(apiError, HttpHeaders(), HttpStatus.BAD_REQUEST.value())
    }
}