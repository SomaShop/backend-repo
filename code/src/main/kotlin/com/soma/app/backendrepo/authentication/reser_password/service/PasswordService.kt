package com.soma.app.backendrepo.authentication.reser_password.service

import com.soma.app.backendrepo.authentication.auth.dto.JwtResetPasswordTokenResponse
import com.soma.app.backendrepo.authentication.auth.pojos.isNotStrongPassword
import com.soma.app.backendrepo.authentication.auth.pojos.isShortPassword
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
import com.soma.app.backendrepo.config.jwt.JwtTokenProvider
import com.soma.app.backendrepo.authentication.reser_password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.authentication.reser_password.pojos.UpdatePasswordRequest
import com.soma.app.backendrepo.email_service.EmailService
import com.soma.app.backendrepo.error_handling.ErrorCode
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiError
import com.soma.app.backendrepo.utils.ApiResult
import com.soma.app.backendrepo.utils.Logger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus

/**
 * This class is used to handle password reset requests from the user
 * It validates the request and sends a reset password link to the user
 * The link contains a jwt token which is used to verify the user
 */

@Service
class PasswordService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
) {
    val logger = Logger.getLogger<PasswordService>()

    /**
     * Validate the reset password request and sends a reset password link to the user's email
     * @param resetRequest: ResetPasswordRequest object containing the user's email
     * @return ApiResult
     * @throws ApiException
     */
    fun resetPassword(resetRequest: ResetPasswordRequest): ApiResult {
        val userEntity = userRepository.findByEmail(resetRequest.email)
        return when {
            !userEntity.isPresent -> {
                val errorCode = ErrorCode.USER_NOT_FOUND.name
                val errorMessage = "User not found with email: ${resetRequest.email}"
                val apiError = ApiError(errorMessage, errorCode)
                ApiResult.Error(apiError)
                throw ApiException(apiError, status = HttpStatus.BAD_REQUEST.value())
            }
            else -> {
                val user = userEntity.get()
                val passwordToken = jwtTokenProvider.createPasswordToken(user)
                val expiryDate = jwtTokenProvider.getExpirationDateFromToken(passwordToken)
                emailService.sendPasswordResetEmail(user.email, passwordToken)
                val resetPasswordDTO = JwtResetPasswordTokenResponse(
                    passwordToken,
                    expiryDate
                )
                ApiResult.Success(ApiData(resetPasswordDTO))
            }
        }

    }

    /**
     * Update the user's password with the new password provided.
     * @param token: The jwt token sent to the user's email
     * @param passwordRequest: The new password and confirm password
     * @return ApiResult
     * @throws ApiException
     */

    fun updatePassword(token: String, passwordRequest: UpdatePasswordRequest): ApiResult {
        var errorCode = ""
        var errorMessage = ""
        val apiError: ApiError
        if (passwordRequest.password.isShortPassword()) {
            errorCode = ErrorCode.SHORT_PASSWORD.name
            errorMessage = "Password must be at least 8 characters long"
        } else if (passwordRequest.password.isNotStrongPassword()) {
            errorCode = ErrorCode.WEAK_PASSWORD.name
            errorMessage = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
        } else if (passwordRequest.password != passwordRequest.confirmPassword) {
            errorCode = ErrorCode.PASSWORD_MISMATCH.name
            errorMessage = "Passwords do not match"
        }
        return if (errorCode.isNotEmpty() && errorMessage.isNotEmpty()) {
            apiError = ApiError(errorMessage, errorCode)
            ApiResult.Error(apiError)
            throw ApiException(apiError, status = HttpStatus.BAD_REQUEST.value())
        } else {
            updatePasswordRequest(token, passwordRequest)
        }
    }

    private fun updatePasswordRequest(token: String, passwordRequest: UpdatePasswordRequest): ApiResult {
        val apiError: ApiError
        findUser(token)?.let {
            if (passwordEncoder.matches(passwordRequest.password, it.getPassword()) ||
                passwordEncoder.matches(passwordRequest.confirmPassword, it.getPassword()) ) {
                apiError = ApiError(
                    message = "New password cannot be the same as the old password",
                    errorCode = ErrorCode.OLD_PASSWORD_DETECTED.name
                )
                ApiResult.Error(error = apiError)
                throw ApiException(apiError, status = HttpStatus.BAD_REQUEST.value())
            }
            val hashedPassword = passwordEncoder.encode(passwordRequest.password)
            val saveUserUpdatedPassword = it.copy(password = hashedPassword)
            userRepository.save(saveUserUpdatedPassword)
            return ApiResult.Success(ApiData("Password updated successfully"))
        } ?: run {
            apiError = ApiError("User not found", ErrorCode.USER_NOT_FOUND.name)
            ApiResult.Error(apiError)
            throw ApiException(apiError, status = HttpStatus.BAD_REQUEST.value())
        }
    }

    fun findUser(token: String): UserEntity? {
        val userName = jwtTokenProvider.getEmailFromToken(token)
        val user = userRepository.findByEmail(userName)
        logger.info("TAG: PasswordService: User found: ${user.isPresent}")
        return when {
            user.isPresent -> user.get()
            else -> null
        }
    }
}