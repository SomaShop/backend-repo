package com.soma.app.backendrepo.authentication.reser_password.service

import com.soma.app.backendrepo.authentication.auth.dto.JwtResetPasswordTokenResponse
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
import com.soma.app.backendrepo.authentication.auth.service.AuthenticationServiceImpl
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
                sendPasswordResetEmail(user.email, passwordToken)
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
        val apiError: ApiError
        return findUser(token)?.let {
            if (passwordEncoder.matches(passwordRequest.password, it.getPassword()) ||
                passwordEncoder.matches(passwordRequest.confirmPassword, it.getPassword())) {
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

    fun sendPasswordResetEmail(email: String, token: String) {
        val subject = "Password reset request"
        //TODO: update the password reset url in production
        val body = "To reset your password, please click the link below:\n" +
            "http://localhost:8080/password/reset_password?token=$token"

        emailService.sendEmail(email, subject, body)

        AuthenticationServiceImpl.logger.info("TAG: EmailService: sendPasswordResetEmail: message: mail sent successfully")
    }
}