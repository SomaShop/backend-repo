package com.soma.app.backendrepo.app_user.user.repository

import com.soma.app.backendrepo.app_user.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}
