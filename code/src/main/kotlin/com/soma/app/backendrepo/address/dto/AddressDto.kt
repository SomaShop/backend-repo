package com.soma.app.backendrepo.address.dto

import com.soma.app.backendrepo.address.entity.AddressEntity
import java.util.UUID

/**
 * Address DTO for Customer Profile and Merchant Profile
 *  - Customer Profile Address Entity Mapping
 *  - Merchant Profile Address Entity Mapping
 *
 */

data class AddressDto(
    val addressId: UUID?,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: CountryDTO,
    val isDefault: Boolean,
) {
    companion object {
        fun fromAddressEntity(addressEntity: AddressEntity): AddressDto {
            return AddressDto(
                addressId = addressEntity.addressID,
                street = addressEntity.street,
                city = addressEntity.city,
                state = addressEntity.state,
                zipCode = addressEntity.zipCode,
                country = addressEntity.getCountry().let { CountryDTO.fromCountryEntity(it) },
                isDefault = addressEntity.isDefault,
            )
        }
    }
}
