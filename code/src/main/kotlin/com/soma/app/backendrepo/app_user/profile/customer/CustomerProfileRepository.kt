package com.soma.app.backendrepo.app_user.profile.customer

import com.soma.app.backendrepo.app_user.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import java.util.Optional

/**
 * Repository for CustomerProfile entity.
 * This repository is used to interact with the database.
 */

interface CustomerProfileRepository : JpaRepository<CustomerProfile, UUID> {
    fun findByUser(user: User): Optional<CustomerProfile>
    fun findByCustomerId(customerId: UUID): Optional<CustomerProfile>
}