package com.soma.app.backendrepo.security.auth.password.controller

import com.soma.app.backendrepo.app_user.dtos.JwtResetPasswordTokenResponse
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.security.auth.password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.security.auth.password.pojos.UpdatePasswordRequest
import com.soma.app.backendrepo.security.auth.password.service.EmailService
import com.soma.app.backendrepo.security.auth.password.service.PasswordService
import com.soma.app.backendrepo.utils.Logger
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/v1/reset")
class PasswordController(
    private val passwordService: PasswordService,
    private val emailService: EmailService
) {

    val logger = Logger<PasswordController>().getLogger()

    @PostMapping("/password")
    fun resetPassword(@RequestBody resetRequest: ResetPasswordRequest): ApiResponse {
        val resetRequestResponse = passwordService.validateResetRequest(resetRequest)
        val user = when (resetRequestResponse.error) {
            null -> (resetRequestResponse.data) as User
            else -> {
                val error = GlobalRequestErrorHandler.handleBadRequestException(
                    Exception(
                        resetRequestResponse.error.errorMessage,
                    )
                )
                return ApiResponse(
                    status = resetRequestResponse.status,
                    error = error.responseData,
                    data = resetRequestResponse.data
                )
            }
        }
        val tokenResponse = passwordService.generatePasswordResetToken(user)
        val token = when (tokenResponse.error) {
            null -> (tokenResponse.data) as JwtResetPasswordTokenResponse
            else -> {
                val error = GlobalRequestErrorHandler.handleInvalidTokenException(
                    Exception(
                        tokenResponse.error.errorMessage,
                    )
                )
                return ApiResponse(
                    status = tokenResponse.status,
                    error = error.responseData,
                    data = tokenResponse.data
                )
            }
        }
        emailService.sendPasswordResetEmail(user, token.token)
        val metaData = mutableMapOf(
            "response" to token,
            "message" to "Password reset link sent to: ${resetRequest.email}"
        )
        return ApiResponse(
            status = "200 OK",
            error = null,
            data = metaData
        )
    }

    @PostMapping("/update/{token}")
    fun updatePassword(
        @PathVariable token: String,
        @RequestBody updateRequest: UpdatePasswordRequest
    ): ApiResponse {
        passwordService.findUser(token)?.let {
            logger.info("TAG: PasswordController: updatePassword: user: $it")
            passwordService.updatePassword(it, updateRequest.password)
            return ApiResponse(
                status = "200 OK",
                error = null,
                data = mutableMapOf(
                    "message" to "Password updated successfully"
                )
            )
        } ?: run {
            val error = GlobalRequestErrorHandler.handleBadRequestException(
                Exception(
                    "Could not update password with token"
                )
            )
            return ApiResponse(
                status = error.statusCode.name,
                error = error.responseData,
                data = null
            )
        }
    }
}
