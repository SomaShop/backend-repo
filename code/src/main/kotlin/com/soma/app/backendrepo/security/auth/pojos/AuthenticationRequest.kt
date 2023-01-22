package com.soma.app.backendrepo.security.auth.pojos

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
