package com.soma.app.backendrepo.app_user.profile.merchant

import com.soma.app.backendrepo.app_user.address.pojo.AddressData
import com.soma.app.backendrepo.app_user.address.service.AddressEntityService
import com.soma.app.backendrepo.app_user.profile.merchant.dto.MerchantProfileDTO
import com.soma.app.backendrepo.app_user.profile.merchant.pojo.MerchantProfileData
import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.utils.RequestResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


/**
 * Service for MerchantProfile entity.
 * This service is used to handle business logic.
 * This service is used to interact with the repository.
 *
 */

@Service
class MerchantProfileService(
    private val merchantProfileRepository: MerchantProfileRepository,
    private val addressEntityService: AddressEntityService
) {
    fun getMerchantProfile(user: UserEntity): ApiResponse {
        val merchantProfile = merchantProfileRepository.findByUser(user)
        return when {
            !merchantProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        errorMessage = "Merchant profile not found",
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(
                    merchantProfile.get()
                )
                return ApiResponse("200 OK", merchantProfileDTO)
            }
        }
    }

    @Transactional
    fun updateMerchantProfile(
        user: UserEntity,
        id: UUID,
        updateRequest: MerchantProfileData
    ): ApiResponse {
        val profile = merchantProfileRepository.findByMerchantId(id)
        when {
            !profile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Merchant profile not found"
                    )
                )
                return ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                val merchantProfile = profile.get()
                val updatedMerchantProfile = merchantProfile.copy(
                    businessAddress = updateRequest.businessAddress ?: merchantProfile.businessAddress,
                    businessName = updateRequest.businessName ?: merchantProfile.businessName,
                    businessPhoneNumber = updateRequest.businessPhone ?: merchantProfile.businessPhoneNumber,
                    businessType = updateRequest.businessType ?: merchantProfile.businessType,
                    businessWebsite = updateRequest.businessWebsite ?: merchantProfile.businessWebsite,
                    businessDescription = updateRequest.businessDescription ?: merchantProfile.businessDescription,
                    businessLogo = updateRequest.businessLogo ?: merchantProfile.businessLogo,
                    businessCoverPhoto = updateRequest.businessCoverPhoto ?: merchantProfile.businessCoverPhoto,
                    businessEmail = updateRequest.businessEmail ?: merchantProfile.businessEmail,
                )
                updatedMerchantProfile.assignUser(user)
                merchantProfileRepository.save(updatedMerchantProfile)
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(updatedMerchantProfile)
                return ApiResponse("200 OK", merchantProfileDTO)
            }
        }
    }

    fun createMerchantAddress(merchantId: UUID, addressData: AddressData): ApiResponse {
        val merchantProfile = merchantProfileRepository.findByMerchantId(merchantId)
        return when {
            !merchantProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Merchant profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                handleMerchantCreationAddress(merchantProfile.get(), addressData)
            }
        }
    }

    private fun handleMerchantCreationAddress(
        merchantProfileEntity: MerchantProfileEntity,
        addressData: AddressData
    ): ApiResponse {
        val addresses = merchantProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        return when {
            addressExists -> {
                val error = GlobalRequestErrorHandler.handleAddressAlreadyExistsException(
                    Exception(
                        "Address already exists"
                    )
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body
                )
            }

            else -> {
                val address = addressEntityService.createAddress(addressData)
                merchantProfileEntity.addAddress(address)
                val savedMerchantAddressEntity = merchantProfileRepository.save(merchantProfileEntity)
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(savedMerchantAddressEntity)
                ApiResponse("200 OK", merchantProfileDTO)
            }
        }
    }

    @Transactional
    fun updateMerchantAddress(
        merchantId: UUID,
        addressId: UUID,
        addressData: AddressData
    ): ApiResponse {
        val merchantProfile = merchantProfileRepository.findByMerchantId(merchantId)
        return when {
            !merchantProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Merchant profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

            else -> {
                handleMerchantUpdateAddress(merchantProfile.get(), addressId, addressData)
            }
        }
    }

    private fun handleMerchantUpdateAddress(
        merchantProfileEntity: MerchantProfileEntity,
        addressId: UUID,
        addressData: AddressData
    ): ApiResponse {
        val addresses = merchantProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        val addressEntity =
            when (val updateResponse = addressEntityService.updateAddressEntity(addressId, addressData)) {
                is RequestResponse.Success -> updateResponse.data
                is RequestResponse.Error -> null
            }
        return when {
            addressEntity == null -> {
                val error = GlobalRequestErrorHandler.handleBadRequestException(
                    Exception(
                        "Address not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body
                )
            }

            addressExists -> {
                val error = GlobalRequestErrorHandler.handleAddressAlreadyExistsException(
                    Exception(
                        "Address already exists"
                    )
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body
                )
            }

            else -> {
                merchantProfileEntity.updateMerchantAddresses(addressEntity)
                val savedMerchantAddressEntity = merchantProfileRepository.save(merchantProfileEntity)
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(savedMerchantAddressEntity)
                ApiResponse("200 OK", merchantProfileDTO)
            }
        }
    }
}