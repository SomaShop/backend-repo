package com.soma.app.backendrepo.address.repository

import com.soma.app.backendrepo.address.entity.AddressEntity
import com.soma.app.backendrepo.address.entity.CountryEntity
import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.profile.customer.CustomerProfileEntity
import com.soma.app.backendrepo.profile.merchant.MerchantProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AddressEntityRepository: JpaRepository<AddressEntity, UUID> {
    fun findByCountryId(countryId: UUID?): AddressEntity?
}
