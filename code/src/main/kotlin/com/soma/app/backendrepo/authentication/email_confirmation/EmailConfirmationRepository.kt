package com.soma.app.backendrepo.authentication.email_confirmation

import com.soma.app.backendrepo.model.app_user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface EmailConfirmationRepository: JpaRepository<EmailConfirmationTokenEntity, UUID> {
    fun findByToken(token: String): Optional<EmailConfirmationTokenEntity>
    fun findByUserId(userId: UUID?): Optional<EmailConfirmationTokenEntity>
    fun findByVerificationCode(verificationCode: String): Optional<EmailConfirmationTokenEntity>
}