package com.soma.app.backendrepo.profile.customer

import com.soma.app.backendrepo.model.app_user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import java.util.Optional

/**
 * Repository for CustomerProfile entity.
 * This repository is used to interact with the database.
 */

interface CustomerProfileRepository : JpaRepository<CustomerProfileEntity, UUID> {
    fun findByUser(user: UserEntity): Optional<CustomerProfileEntity>
    fun findByCustomerId(customerId: UUID): Optional<CustomerProfileEntity>
}