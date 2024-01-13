package com.soma.app.backendrepo.profile.customer


import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.address.service.AddressEntityService
import com.soma.app.backendrepo.profile.customer.dto.CustomerProfileDTO
import com.soma.app.backendrepo.profile.customer.pojo.CustomerProfileData
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.error_handling.ErrorCode
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiError
import com.soma.app.backendrepo.utils.ApiResult
import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import org.springframework.http.HttpStatus

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

    fun getCustomerProfile(userEntity: UserEntity): ApiResult {
        val customerProfile = customerProfileRepository.findByUser(userEntity)
        return when {
            !customerProfile.isPresent -> {
                val errorMessage = "Customer profile not found"
                val errorCode = ErrorCode.CUSTOMER_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    customerProfile.get()
                )
                ApiResult.Success(
                    data = ApiData(customerProfileDTO)
                )
            }
        }
    }

    @Transactional
    fun updateCustomerProfile(
        userEntity: UserEntity,
        id: UUID,
        updateRequest: CustomerProfileData
    ): ApiResult {
        val profile = customerProfileRepository.findByCustomerId(id)
        return when {
            !profile.isPresent -> {
                val errorMessage = "Customer profile not found"
                val errorCode = ErrorCode.CUSTOMER_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                ApiResult.Success(data = ApiData(customerProfileDTO))
            }
        }
    }

    @Transactional
    fun updateCustomerPaymentMethod(
        userEntity: UserEntity,
        customerId: UUID,
        customerProfileRequest: CustomerProfileData
    ): ApiResult {
        val profile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !profile.isPresent -> {
                val errorMessage = "Customer profile not found"
                val errorCode = ErrorCode.CUSTOMER_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                ApiResult.Success(data = ApiData(customerProfileDTO))
            }
        }
    }

    @Transactional
    fun updateCustomerAddress(
        customerId: UUID,
        addressData: AddressData,
        addressId: UUID
    ): ApiResult {
        val customerProfile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !customerProfile.isPresent -> {
                val errorMessage = "Customer does not have any profile yet"
                val errorCode = ErrorCode.CUSTOMER_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
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
    ): ApiResult {
        val addresses = customerProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        val addressEntity =
            when (val address = addressEntityService.updateAddressEntity(addressId, addressData)) {
                null  -> null
                else -> address
            }
        return when {
            addressExists -> {
                val errorMessage = "Address already exists"
                val errorCode = ErrorCode.ADDRESS_ALREADY_EXISTS.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            addressEntity == null -> {
                val errorMessage = "Customer does not have any address yet"
                val errorCode = ErrorCode.ADDRESS_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                customerProfileEntity.updateMerchantAddresses(addressEntity)
                val savedCustomerProfileEntity = customerProfileRepository.save(customerProfileEntity)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    savedCustomerProfileEntity
                )
                ApiResult.Success(data = ApiData(customerProfileDTO))
            }
        }
    }

    fun createCustomerAddress(
        customerId: UUID,
        addressData: AddressData
    ): ApiResult {
        logger.info("TAG: CustomerProfileService.createCustomerAddress()")
        val customerProfile = customerProfileRepository.findByCustomerId(customerId)
        return when {
            !customerProfile.isPresent -> {
                val errorMessage = "Customer profile not found"
                val errorCode = ErrorCode.CUSTOMER_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                handleCreateCustomerAddress(customerProfile.get(), addressData)
            }
        }
    }

    private fun handleCreateCustomerAddress(
        customerProfileEntity: CustomerProfileEntity,
        addressData: AddressData
    ): ApiResult {
        val addresses = customerProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        return when {
            addressExists -> {
                val errorMessage = "Address already exists"
                val errorCode = ErrorCode.ADDRESS_ALREADY_EXISTS.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                val newAddress = addressEntityService.createAddress(addressData)
                customerProfileEntity.addAddress(newAddress)
                customerProfileRepository.save(customerProfileEntity)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    customerProfileEntity
                )
                ApiResult.Success(data = ApiData(customerProfileDTO))
            }
        }
    }
}