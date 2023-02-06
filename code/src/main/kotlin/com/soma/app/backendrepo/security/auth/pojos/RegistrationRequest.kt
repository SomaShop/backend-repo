package com.soma.app.backendrepo.security.auth.pojos
import com.soma.app.backendrepo.app_user.user.model.UserRole

/**
 * This class is used to represent the request body of the register endpoint
 */
data class RegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val userRole: UserRole,
)
