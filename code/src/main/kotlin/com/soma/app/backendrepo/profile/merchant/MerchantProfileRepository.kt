package com.soma.app.backendrepo.profile.merchant

import com.soma.app.backendrepo.model.app_user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import java.util.Optional

/**
 * Repository for MerchantProfile entity.
 * This repository is used to interact with the database.
 */

interface MerchantProfileRepository : JpaRepository<MerchantProfileEntity, UUID> {
    fun findByMerchantId(merchantId: UUID): Optional<MerchantProfileEntity>
    fun findByUser(user: UserEntity): Optional<MerchantProfileEntity>
}