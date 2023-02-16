package com.soma.app.backendrepo.app_user.user.repository

import com.soma.app.backendrepo.app_user.user.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): Optional<UserEntity>
}
