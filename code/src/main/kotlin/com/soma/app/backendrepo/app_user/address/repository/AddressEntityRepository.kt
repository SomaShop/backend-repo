package com.soma.app.backendrepo.app_user.address.repository

import com.soma.app.backendrepo.app_user.address.entity.AddressEntity
import com.soma.app.backendrepo.app_user.address.entity.CountryEntity
import com.soma.app.backendrepo.app_user.address.pojo.AddressData
import com.soma.app.backendrepo.app_user.profile.customer.CustomerProfileEntity
import com.soma.app.backendrepo.app_user.profile.merchant.MerchantProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AddressEntityRepository: JpaRepository<AddressEntity, UUID> {
    fun findByCountryUuid(countryUuid: UUID): List<AddressEntity>
}
