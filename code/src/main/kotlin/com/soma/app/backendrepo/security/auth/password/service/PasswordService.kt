package com.soma.app.backendrepo.security.auth.password.service

import com.soma.app.backendrepo.app_user.dtos.JwtResetPasswordTokenResponse
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationService
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.utils.Logger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PasswordService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val passwordConfirmationService: PasswordConfirmationService
) {
    lateinit var token: String
    val logger = Logger<PasswordService>().getLogger()

    fun validateResetRequest(resetRequest: ResetPasswordRequest): ApiResponse {
        val user = userRepository.findByEmail(resetRequest.email)
        return if (!user.isPresent) {
            val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                Exception(
                    "User not found with email: ${resetRequest.email}"
                )
            )
            ApiResponse(
                status = error.statusCode.name,
                error = error.responseData,
            )
        } else {
            ApiResponse(
                status = "200",
                data = user.get(),
                error = null
            )
        }
    }

    fun generatePasswordResetToken(user: User): ApiResponse {
        val userExists = passwordConfirmationService.findTokenByUser(user)
        logger.info("TAG: PasswordService: generatePasswordResetToken: reset password for user: $userExists")
        token = if (userExists.isPresent) {
            val userToken = userExists.get()
            if (userToken.tokenExpiresAt?.before(Date()) == true || userToken.token.isEmpty()) {
                jwtTokenProvider.createConfirmPasswordToken(user)
            } else {
                userToken.token
            }
        } else {
            jwtTokenProvider.createConfirmPasswordToken(user)
        }

        logger.info("TAG: PasswordService: generatePasswordResetToken: token: $token")

        val expiryDate = jwtTokenProvider.getExpirationDateFromToken(token)
        return ApiResponse(
            "200 OK",
            data = JwtResetPasswordTokenResponse(
                token,
                expiryDate
            )
        )
    }

    fun updatePassword(user: User, newPassword: String) {
        logger.info("TAG: PasswordService: updatePassword: user: $user")
        val associatedUserToken = passwordConfirmationService.findTokenByUser(user)
        logger.info("TAG: PasswordService: updatePassword: associatedUserToken: $associatedUserToken")
        val hashedPassword = passwordEncoder.encode(newPassword)
        val saveUserUpdatedPassword =
            user.copy(password = hashedPassword)
        val updatedPasswordToken = when {
            associatedUserToken.isPresent -> {
                val token = passwordConfirmationService.getToken(associatedUserToken.get().token)
                if (token.isPresent && token.get().token.isNotEmpty()) {
                    token.get().copy(token = "", user = saveUserUpdatedPassword)
                } else {
                    PasswordConfirmationToken(
                        token = "",
                        user = saveUserUpdatedPassword,
                    )
                }

            }

            else -> {
                PasswordConfirmationToken(
                    token = "",
                    user = saveUserUpdatedPassword
                )
            }
        }
        passwordConfirmationService.saveToken(
            updatedPasswordToken
        )
        userRepository.save(saveUserUpdatedPassword)
    }

    fun findUser(token: String): User? {
        val userName = jwtTokenProvider.getEmailFromToken(token)
        val user = userRepository.findByEmail(userName)
        logger.info("TAG: PasswordService: findUser: user: $user")
        return when {
            user.isPresent -> {
                user.get()
            }

            else -> null
        }
    }
}