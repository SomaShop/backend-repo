package com.soma.app.backendrepo.authentication.auth.pojos
import com.soma.app.backendrepo.model.app_user.UserRole

/**
 * This class is used to represent the request body of the register endpoint
 */

enum class VerificationType {
    VERIFICATION_CODE,
    EMAIL_LINK,
    PHONE
}

data class RegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val userRole: UserRole,
    val verificationType: VerificationType = VerificationType.VERIFICATION_CODE
)
