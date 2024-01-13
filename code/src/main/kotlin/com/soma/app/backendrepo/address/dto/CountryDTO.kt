package com.soma.app.backendrepo.address.dto

import com.soma.app.backendrepo.address.entity.CountryEntity
import com.soma.app.backendrepo.utils.constants.getCountryCode
import com.soma.app.backendrepo.utils.constants.getCurrenciesForCountry
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
                countryId = countryEntity.countryId,
                countryName = countryEntity.countryName,
                code = getCountryCode(countryEntity.countryName),
                currency = getCurrenciesForCountry(countryEntity.countryName),
            )
        }
    }
}
