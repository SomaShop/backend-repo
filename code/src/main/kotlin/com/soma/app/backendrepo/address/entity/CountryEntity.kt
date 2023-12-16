package com.soma.app.backendrepo.address.entity

import com.soma.app.backendrepo.utils.constants.countryCode
import com.soma.app.backendrepo.utils.constants.countryToCurrencies
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

/**
 * Country Entity to be saved in the database.
 * This entity is used to store country information such as country name, numeric code, currency, etc.
 */

@Entity
@Table(name = "countries")
data class CountryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID? = null,
    val countryName: String,
    val countryNumericCode: String? = null,
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    val currency: List<String>? = null,
) {
    override fun toString(): String {
        return "CountryEntity(id=$uuid, countryName='$countryName', code='$countryNumericCode', currency='$currency')"
    }

    fun getCurrenciesForCountry(): List<String> = countryToCurrencies[countryName] ?: emptyList()
    fun getCountryCode(): String = countryCode[countryName] ?: ""
}
