package com.soma.app.backendrepo.app_user.user.pass_confirmation_token

import com.soma.app.backendrepo.app_user.user.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface PasswordConfirmationRepository: JpaRepository<PasswordConfirmationToken, UUID> {
    fun findByToken(token: String): Optional<PasswordConfirmationToken>
    fun findByUser(user: UserEntity): Optional<PasswordConfirmationToken>
}