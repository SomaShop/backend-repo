package com.soma.app.backendrepo.app_user.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date


data class JwtRegistrationTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("password_confirmation_token") var passwordConfirmationTokenDto: PasswordConfirmationTokenDTO? = null,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
)

data class JwtAuthenticateTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("access_token_expires_at") val expiredToken: Date,
    @JsonProperty("user") val userDTO: UserDTO,
    @JsonProperty("refresh_token_expires_at") var refreshTokenExpiresAt: Date? = null,
)

data class JwtResetPasswordTokenResponse(
    @JsonProperty("password_confirmation_token") var token: String,
    @JsonProperty("password_token_expires_at") var expiredToken: Date,
)
