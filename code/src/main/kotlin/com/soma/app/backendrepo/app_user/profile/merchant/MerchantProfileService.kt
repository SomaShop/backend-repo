package com.soma.app.backendrepo.app_user.profile.merchant

import com.soma.app.backendrepo.app_user.dtos.MerchantProfileDTO
import com.soma.app.backendrepo.app_user.profile.merchant.pojo.MerchantProfileRequest
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.isMerchant
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
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
    private val merchantProfileRepository: MerchantProfileRepository
) {
    fun getMerchantProfile(user: User): ApiResponse {
        val merchantProfile = merchantProfileRepository.findByUser(user)
        if (!user.isMerchant() || !merchantProfile.isPresent) {
            val error = GlobalRequestErrorHandler.handleUnauthorizedException(
                Exception(
                    "You are not authorized to access this resource"
                )
            )
            return ApiResponse(
                status = error.statusCode.name,
                error = error.responseData
            )
        }
        val merchantProfileDTO = MerchantProfileDTO.fromMerchantProfileEntity(merchantProfile.get())
        return ApiResponse("200 OK",merchantProfileDTO)
    }

    @Transactional
    fun updateMerchantProfile(
        user: User,
        id: UUID,
        updateRequest: MerchantProfileRequest
    ): ApiResponse {
        val profile = merchantProfileRepository.findByMerchantId(id)
        when {
            !user.isMerchant() -> {
                val error = GlobalRequestErrorHandler.handleUnauthorizedException(
                    Exception(
                        "You are not authorized to access this resource"
                    )
                )
                return ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }

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
                    businessPhone = updateRequest.businessPhone ?: merchantProfile.businessPhone,
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
}