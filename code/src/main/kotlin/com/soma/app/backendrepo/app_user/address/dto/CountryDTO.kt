package com.soma.app.backendrepo.app_user.address.dto

import com.soma.app.backendrepo.app_user.address.entity.CountryEntity
import java.util.UUID

data class CountryDTO(
    val countryId: UUID? = null,
    val countryName: String,
    val code: String,
    val currency: List<String>,
) {
    companion object {
        fun fromCountryEntity(countryEntity: CountryEntity): CountryDTO {
            return CountryDTO(
                countryId = countryEntity.uuid,
                countryName = countryEntity.countryName,
                code = countryEntity.getCountryCode(),
                currency = countryEntity.getCurrenciesForCountry(),
            )
        }
    }
}
