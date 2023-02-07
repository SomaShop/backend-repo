package com.soma.app.backendrepo.app_user.profile.merchant

import com.soma.app.backendrepo.app_user.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import java.util.Optional

/**
 * Repository for MerchantProfile entity.
 * This repository is used to interact with the database.
 */

interface MerchantProfileRepository : JpaRepository<MerchantProfile, UUID> {
    fun findByMerchantId(merchantId: UUID): Optional<MerchantProfile>
    fun findByUser(user: User): Optional<MerchantProfile>
}