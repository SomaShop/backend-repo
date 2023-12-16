package com.soma.app.backendrepo.authentication.auth.pojos

/**
 * This class is used to represent the request body of the login endpoint
 */
data class AuthenticationRequest(
    val email: String,
    val password: String,
)
