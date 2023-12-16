package com.soma.app.backendrepo.authentication.email_confirmation

import java.util.Date
import java.util.UUID

/**
 * Data Transfer Object for EmailConfirmationTokenDTO Entity to be used in the API.
 * This DTO is used to transfer data from the API to the service layer.
 */

data class EmailConfirmationTokenDTO(
    val id: UUID,
    val token: String,
    val tokenExpiresAt: Date? = null,
    val userId: UUID? = null,
    val createdAt: Date? = null,
    val confirmedAt: Date? = null,
) {
    companion object {
        fun fromEmailConfirmationToken(
            emailConfirmationTokenEntity: EmailConfirmationTokenEntity
        ): EmailConfirmationTokenDTO {
            return EmailConfirmationTokenDTO(
                id = emailConfirmationTokenEntity.id!!,
                token = emailConfirmationTokenEntity.token,
                tokenExpiresAt = emailConfirmationTokenEntity.tokenExpiresAt,
                userId = emailConfirmationTokenEntity.userId,
                createdAt = emailConfirmationTokenEntity.createdAt,
                confirmedAt = emailConfirmationTokenEntity.confirmedAt,
            )
        }
    }
}