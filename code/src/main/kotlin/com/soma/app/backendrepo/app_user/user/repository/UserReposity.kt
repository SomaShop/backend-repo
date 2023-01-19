package com.soma.app.backendrepo.app_user.user.repository

import com.soma.app.backendrepo.app_user.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}
