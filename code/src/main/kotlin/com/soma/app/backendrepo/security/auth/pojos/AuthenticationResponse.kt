package com.soma.app.backendrepo.security.auth.pojos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class AuthenticationResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expired_token") val expiredToken: Date
)
