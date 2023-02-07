package com.soma.app.backendrepo.app_user.dtos

import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import java.util.Date
import java.util.UUID

/**
 * Data Transfer Object for PasswordConfirmationToken Entity to be used in the API.
 * This DTO is used to transfer data from the API to the service layer.
 */

data class PasswordConfirmationTokenDTO(
    val id: UUID,
    val token: String,
    val token_expires_at: Date? = null,
    val user: UserDTO,
    val created_at: Date? = null,
    val confirmed_at: Date? = null,
) {
    companion object {
        fun fromPasswordConfirmationToken(
            passwordConfirmationToken: PasswordConfirmationToken
        ): PasswordConfirmationTokenDTO {
            return PasswordConfirmationTokenDTO(
                id = passwordConfirmationToken.id!!,
                token = passwordConfirmationToken.token,
                token_expires_at = passwordConfirmationToken.tokenExpiresAt,
                user = UserDTO.fromUserEntity(passwordConfirmationToken.user),
                created_at = passwordConfirmationToken.createdAt,
                confirmed_at = passwordConfirmationToken.confirmedAt,
            )
        }
    }
}