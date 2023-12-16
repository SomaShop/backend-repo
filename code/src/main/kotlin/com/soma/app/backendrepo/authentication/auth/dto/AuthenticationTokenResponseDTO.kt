package com.soma.app.backendrepo.authentication.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.soma.app.backendrepo.model.app_user.UserEntityDTO
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationTokenDTO
import java.util.Date
import java.util.UUID

/**
 * DTO for JWT Registration Token Response for the user registration process
 *
 */

data class JwtRegistrationTokenResponseDTO(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("email_confirmation_token") var emailConfirmationTokenDto: EmailConfirmationTokenDTO? = null,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
    @JsonProperty("profile_id") var associatedUserID: UUID? = null,
)

data class JwtAuthenticateTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("userId") val userId: UUID? = null,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
    @JsonProperty("profile_id") var associatedUserID: UUID? = null,
)

data class JwtResetPasswordTokenResponse(
    @JsonProperty("password_confirmation_token") var token: String,
    @JsonProperty("password_token_expires_at") var expiredToken: Date,
)
