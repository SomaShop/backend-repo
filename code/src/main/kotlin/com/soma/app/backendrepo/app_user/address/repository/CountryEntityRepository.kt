package com.soma.app.backendrepo.app_user.address.repository

import com.soma.app.backendrepo.app_user.address.entity.CountryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CountryEntityRepository: JpaRepository<CountryEntity, UUID> {
    fun findByCountryName(countryName: String): CountryEntity?
    fun findByCountryNumericCode(numericCode: String): CountryEntity?
}