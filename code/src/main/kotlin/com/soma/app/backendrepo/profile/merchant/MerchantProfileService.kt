package com.soma.app.backendrepo.profile.merchant

import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.address.service.AddressEntityService
import com.soma.app.backendrepo.profile.merchant.dto.MerchantProfileDTO
import com.soma.app.backendrepo.profile.merchant.pojo.MerchantProfileData
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.error_handling.ErrorCode
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiError
import com.soma.app.backendrepo.utils.ApiResult
import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import org.springframework.http.HttpStatus


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
    val log = Logger.getLogger<MerchantProfileService>()
    fun getMerchantProfile(user: UserEntity): ApiResult {
        val merchantProfile = merchantProfileRepository.findByUser(user)
        return when {
            !merchantProfile.isPresent -> {
                val errorMessage = "Merchant profile not found"
                val errorCode = ErrorCode.MERCHANT_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(
                    merchantProfile.get()
                )
                ApiResult.Success(data = ApiData(merchantProfileDTO))
            }
        }
    }

    @Transactional
    fun updateMerchantProfile(
        user: UserEntity,
        id: UUID,
        updateRequest: MerchantProfileData
    ): ApiResult {
        val profile = merchantProfileRepository.findByMerchantId(id)
        when {
            !profile.isPresent -> {
                val errorMessage = "Merchant profile not found"
                val errorCode = ErrorCode.MERCHANT_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                return ApiResult.Success(data = ApiData(merchantProfileDTO))
            }
        }
    }

    fun createMerchantAddress(merchantId: UUID, addressData: AddressData): ApiResult {
        val merchantProfile = merchantProfileRepository.findByMerchantId(merchantId)
        return when {
            !merchantProfile.isPresent -> {
                val errorMessage = "Merchant profile not found"
                val errorCode = ErrorCode.MERCHANT_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                handleMerchantCreationAddress(merchantProfile.get(), addressData)
            }
        }
    }

    private fun handleMerchantCreationAddress(
        merchantProfileEntity: MerchantProfileEntity,
        addressData: AddressData
    ): ApiResult {
        val addresses = merchantProfileEntity.getAddresses()
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
                val address = addressEntityService.createAddress(addressData)
                merchantProfileEntity.addAddress(address)
                val savedMerchantAddressEntity = merchantProfileRepository.save(merchantProfileEntity)
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(savedMerchantAddressEntity)
                ApiResult.Success(data = ApiData(merchantProfileDTO))
            }
        }
    }

    @Transactional
    fun updateMerchantAddress(
        merchantId: UUID,
        addressId: UUID,
        addressData: AddressData
    ): ApiResult {
        val merchantProfile = merchantProfileRepository.findByMerchantId(merchantId)
        return when {
            !merchantProfile.isPresent -> {
                val errorMessage = "Merchant profile not found"
                val errorCode = ErrorCode.MERCHANT_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
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
    ): ApiResult {
        val addresses = merchantProfileEntity.getAddresses()
        val addressExists = addressEntityService.isDuplicateAddress(addresses, addressData)
        val addressEntity =
            when (val address = addressEntityService.updateAddressEntity(addressId, addressData)) {
                null -> null
                else -> address
            }
        return when {
            addressEntity == null -> {
                val errorMessage = "Address not found"
                val errorCode = ErrorCode.ADDRESS_NOT_FOUND.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            addressExists -> {
                val errorMessage = "Address already exists"
                val errorCode = ErrorCode.ADDRESS_ALREADY_EXISTS.name
                val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                ApiResult.Error(apiError)
                throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
            }

            else -> {
                merchantProfileEntity.updateMerchantAddresses(addressEntity)
                val savedMerchantAddressEntity = merchantProfileRepository.save(merchantProfileEntity)
                val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(savedMerchantAddressEntity)
                ApiResult.Success(data = ApiData(merchantProfileDTO))
            }
        }
    }
}