package com.soma.app.backendrepo.security

data class JwtAuthenticationResponse(
    val accessToken: String,
    val refreshToken: String? = null,
)
