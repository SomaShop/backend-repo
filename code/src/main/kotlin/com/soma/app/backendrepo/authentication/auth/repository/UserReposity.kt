package com.soma.app.backendrepo.authentication.auth.repository

import com.soma.app.backendrepo.model.app_user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): Optional<UserEntity>
}
