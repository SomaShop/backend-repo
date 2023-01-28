package com.soma.app.backendrepo.error_handling

import com.soma.app.backendrepo.utils.CustomResponseEntity
import org.springframework.http.ResponseEntity


class GlobalRequestErrorHandler {

   companion object {
       fun handleUserNotFoundException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.USER_NOT_FOUND.name, ex.message)
           return CustomResponseEntity.notFound(data = error)
       }

       fun handleInternalServerException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.name, ex.message)
           return CustomResponseEntity.internalServerError(value = error)
       }


       fun handleBadRequestException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.BAD_REQUEST.name, ex.message)
           return CustomResponseEntity.badRequest(value = error)
       }

       fun handleForbiddenException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.FORBIDDEN.name, ex.message)
           return CustomResponseEntity.forbidden(value = error)
       }

       fun handleUnauthorizedException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.UNAUTHORIZED.name, ex.message)
           return CustomResponseEntity.unauthorized(value = error)
       }

       fun handleTokenNotFoundException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.TOKEN_NOT_FOUND.name, ex.message)
           return CustomResponseEntity.notFound(data = error)
       }

       fun handleTokenExpiredException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.TOKEN_EXPIRED.name, ex.message)
           return CustomResponseEntity.unauthorized(value = error)
       }

       fun handleInvalidTokenException(ex: Exception): CustomResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.INVALID_TOKEN.name, ex.message)
           return CustomResponseEntity.unauthorized(value = error)
       }

       fun handleUserNotVerifiedException(ex: Exception): ResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.USER_NOT_VERIFIED.name, ex.message)
           return CustomResponseEntity.unauthorized(value = error)
       }

       fun handleUserAlreadyExistsException(ex: Exception): ResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.USER_ALREADY_EXISTS.name, ex.message)
           return CustomResponseEntity.conflict(value = error)
       }

       fun handleEmailAlreadyExistsException(ex: Exception): ResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.EMAIL_ALREADY_EXISTS.name, ex.message)
           return CustomResponseEntity.conflict(value = error)
       }

       fun handleEmailNotFoundException(ex: Exception): ResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.EMAIL_NOT_FOUND.name, ex.message)
           return CustomResponseEntity.notFound(data = error)
       }

       fun handleBadCredentialsException(ex: Exception): ResponseEntity<ErrorResponse> {
           val error = ErrorResponse(ErrorCode.BAD_CREDENTIALS.name, ex.message)
           return CustomResponseEntity.unauthorized(value = error)
       }
   }
}
