package com.soma.app.backendrepo.address.service

import com.soma.app.backendrepo.address.entity.AddressEntity
import com.soma.app.backendrepo.address.entity.CountryEntity
import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.address.repository.AddressEntityRepository
import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class AddressEntityService(
    private val addressEntityRepository: AddressEntityRepository,
    private val countryEntityService: CountryEntityService
) {
    val log = Logger.getLogger<AddressEntityService>()
    fun createAddress(addressData: AddressData): AddressEntity {
        log.info("Creating address entity for address data: $addressData")
        val country = countryEntityService.getCountryByCountryName(addressData.country.countryName)
            ?: countryEntityService.createCountry(addressData.country)
        val addressEntity = AddressEntity(
            street = addressData.street,
            city = addressData.city,
            state = addressData.state,
            zipCode = addressData.zipCode,
            countryId = country.countryId
        )
        return saveAddress(addressEntity)
    }

    fun saveAddress(addressEntity: AddressEntity) = addressEntityRepository.saveAndFlush(addressEntity)

    fun isDuplicateAddress(existingAddresses: List<AddressEntity>, addressData: AddressData): Boolean {
        return existingAddresses.isNotEmpty() && existingAddresses.any {
            it.street.lowercase() == addressData.street.lowercase() &&
                it.city.lowercase() == addressData.city.lowercase() &&
                it.state.lowercase() == addressData.state.lowercase() &&
                it.zipCode.lowercase() == addressData.zipCode.lowercase()
        }
    }

    fun findAddressById(addressId: UUID): Optional<AddressEntity> {
        return addressEntityRepository.findById(addressId)
    }

    fun getCountryEntity(countryName: String): CountryEntity? {
        return countryEntityService.getCountryByCountryName(countryName)
    }

    fun updateAddressEntity(addressId: UUID, addressData: AddressData): AddressEntity? {
        val addressEntity = findAddressById(addressId)
        return when {
            !addressEntity.isPresent -> {
                null
            }

            else -> {
                val countryEntity = countryEntityService.findCountryById(addressEntity.get().countryId)
                val updatedAddressEntity = addressEntity.get().copy(
                    street = addressData.street,
                    city = addressData.city,
                    state = addressData.state,
                    zipCode = addressData.zipCode,
                    countryId = countryEntity?.countryId
                )
                return saveAddress(updatedAddressEntity)
            }
        }

    }
}