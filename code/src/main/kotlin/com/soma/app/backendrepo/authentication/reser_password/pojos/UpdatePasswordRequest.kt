package com.soma.app.backendrepo.authentication.reser_password.pojos

data class UpdatePasswordRequest(
    val password: String,
    val confirmPassword: String
)


