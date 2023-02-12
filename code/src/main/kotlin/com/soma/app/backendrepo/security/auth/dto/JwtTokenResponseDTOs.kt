package com.soma.app.backendrepo.security.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.soma.app.backendrepo.app_user.user.model.UserEntityDTO
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationTokenDTO
import java.util.Date
import java.util.UUID

/**
 * DTO for JWT Registration Token Response for the user registration process
 *
 */

data class JwtRegistrationTokenResponseDTO(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("password_confirmation_token") var passwordConfirmationTokenDto: PasswordConfirmationTokenDTO? = null,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
    @JsonProperty("associated_user_id") var associatedUserID: UUID? = null,
)

data class JwtAuthenticateTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("user") val userEntityDTO: UserEntityDTO,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
    @JsonProperty("associated_user_id") var associatedUserID: UUID? = null,
)

data class JwtResetPasswordTokenResponse(
    @JsonProperty("password_confirmation_token") var token: String,
    @JsonProperty("password_token_expires_at") var expiredToken: Date,
)
