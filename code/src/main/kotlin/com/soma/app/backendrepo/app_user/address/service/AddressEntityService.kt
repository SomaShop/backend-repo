package com.soma.app.backendrepo.app_user.address.service

import com.soma.app.backendrepo.app_user.address.entity.AddressEntity
import com.soma.app.backendrepo.app_user.address.entity.CountryEntity
import com.soma.app.backendrepo.app_user.address.pojo.AddressData
import com.soma.app.backendrepo.app_user.address.repository.AddressEntityRepository
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.utils.Logger
import com.soma.app.backendrepo.utils.RequestResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class AddressEntityService(
    private val addressEntityRepository: AddressEntityRepository,
    private val countryEntityService: CountryEntityService
) {
    val logger = Logger.getLogger<AddressEntityService>()
    fun createAddress(addressData: AddressData): AddressEntity {
        val countryEntity = countryEntityService.getCountryByCountryName(addressData.country.countryName)
            ?: countryEntityService.createCountry(addressData.country)
        val addressEntity = AddressEntity(
            street = addressData.street,
            city = addressData.city,
            state = addressData.state,
            zipCode = addressData.zipCode,
        )
        addressEntity.assignCountry(countryEntity)
        return saveAddress(addressEntity)
    }

    fun saveAddress(addressEntity: AddressEntity) = addressEntityRepository.save(addressEntity)

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

    fun updateAddressEntity(addressId: UUID, addressData: AddressData): RequestResponse<AddressEntity> {
        val addressEntity = findAddressById(addressId)
        return when {
            !addressEntity.isPresent -> {
                RequestResponse.Error("Address not found")
            }

            else -> {
                val countryEntity = countryEntityService.getCountryByCountryName(
                    addressEntity.get().getCountry().countryName
                )
                val updatedAddressEntity = addressEntity.get().copy(
                    street = addressData.street,
                    city = addressData.city,
                    state = addressData.state,
                    zipCode = addressData.zipCode,
                )
                updatedAddressEntity.assignCountry(countryEntity!!)
                return RequestResponse.Success(saveAddress(updatedAddressEntity))
            }
        }

    }
}