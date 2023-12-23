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
) {
    fun isNotValid(): Boolean {
        return password.isBlank() ||
            firstName.isBlank() ||
            lastName.isBlank() ||
            email.isBlank()
    }
}


fun String.isValidEmail(): Boolean {
    // You can implement a more sophisticated email validation logic if needed
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return this.matches(emailRegex.toRegex())
}

fun String.isValidPassword(): Boolean {
    // You can implement a more sophisticated password validation logic if needed
    val strongPasswordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}\$"
    return this.matches(strongPasswordRegex.toRegex())
}

fun String.isShortPassword(): Boolean {
    return this.length < 8
}

fun String.isNotStrongPassword(): Boolean {
    return !isValidPassword() || isShortPassword()
}
