package com.soma.app.backendrepo.profile.customer


import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.address.service.AddressEntityService
import com.soma.app.backendrepo.profile.customer.dto.CustomerProfileDTO
import com.soma.app.backendrepo.profile.customer.pojo.CustomerProfileData
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.utils.Logger
import com.soma.app.backendrepo.utils.RequestResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service for CustomerProfile entity.
 * This service is used to handle business logic.
 * This service is used to interact with the repository.
 *
 */

@Service
class CustomerProfileService(
    private val customerProfileRepository: CustomerProfileRepository,
    private val addressEntityService: AddressEntityService
) {
    val logger = Logger.getLogger<CustomerProfileService>()

    fun getCustomerProfile(userEntity: UserEntity): ApiResponse {
        val customerProfile = customerProfileRepository.findByUser(userEntity)
        return when {
            !customerProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        errorMessage = "Customer profile not found",
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    customerProfile.get()
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }

    @Transactional
    fun updateCustomerProfile(
        userEntity: UserEntity,
        id: UUID,
        updateRequest: CustomerProfileData
    ): ApiResponse {
        val profile = customerProfileRepository.findByCustomerId(id)
        return when {
            !profile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Customer profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val customerProfile = profile.get()
                val updatedCustomerProfile = customerProfile.copy(
                    paymentMethod = updateRequest.paymentMethod,
                )
                updatedCustomerProfile.assignUser(userEntity)
                customerProfileRepository.save(updatedCustomerProfile)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    updatedCustomerProfile
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }

    @Transactional
    fun updateCustomerPaymentMethod(
        userEntity: UserEntity,
        customerId: UUID,
        customerProfileRequest: CustomerProfileData
    ): ApiResponse {
        val profile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !profile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Customer profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val customerProfile = profile.get()
                val updatedCustomerProfile = customerProfile.copy(
                    paymentMethod = customerProfileRequest.paymentMethod,
                )
                updatedCustomerProfile.assignUser(userEntity)
                customerProfileRepository.save(updatedCustomerProfile)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    updatedCustomerProfile
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }

    @Transactional
    fun updateCustomerAddress(
        customerId: UUID,
        addressData: AddressData,
        addressId: UUID
    ): ApiResponse {
        val customerProfile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !customerProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Customer profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                handleUpdateCustomerAddress(
                    customerProfile.get(), addressData, addressId
                )
            }
        }
    }

    private fun handleUpdateCustomerAddress(
        customerProfileEntity: CustomerProfileEntity,
        addressData: AddressData,
        addressId: UUID
    ): ApiResponse {
        val addresses = customerProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        val addressEntity =
            when (val updateResponse = addressEntityService.updateAddressEntity(addressId, addressData)) {
                is RequestResponse.Success -> updateResponse.data
                is RequestResponse.Error -> null
            }
        return when {
            addressExists -> {
                val error = GlobalRequestErrorHandler.handleAddressAlreadyExistsException(
                    Exception(
                        "Address already exists"
                    )
                )
                ApiResponse(
                    status = error.body!!.errorCode,
                    error = error.body
                )
            }

            addressEntity == null -> {
                val error = GlobalRequestErrorHandler.handleBadRequestException(
                    Exception(
                        "Customer profile does not have any address"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                customerProfileEntity.updateMerchantAddresses(addressEntity)
                val savedCustomerProfileEntity = customerProfileRepository.save(customerProfileEntity)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    savedCustomerProfileEntity
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }

    fun createCustomerAddress(
        customerId: UUID,
        addressData: AddressData
    ): ApiResponse {
        logger.info("TAG: CustomerProfileService.createCustomerAddress()")
        val customerProfile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !customerProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Customer profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                handleCreateCustomerAddress(customerProfile.get(), addressData)
            }
        }
    }

    private fun handleCreateCustomerAddress(
        customerProfileEntity: CustomerProfileEntity,
        addressData: AddressData
    ): ApiResponse {
        val addresses = customerProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        return when {
            addressExists -> {
                val error = GlobalRequestErrorHandler.handleBadRequestException(
                    Exception(
                        "Address already exists"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val newAddress = addressEntityService.createAddress(addressData)
                customerProfileEntity.addAddress(newAddress)
                customerProfileRepository.save(customerProfileEntity)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    customerProfileEntity
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }
}