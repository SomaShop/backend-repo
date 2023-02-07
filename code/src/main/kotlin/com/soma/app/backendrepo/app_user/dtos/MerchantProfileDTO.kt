package com.soma.app.backendrepo.app_user.dtos

import com.soma.app.backendrepo.app_user.profile.merchant.MerchantProfile
import java.util.UUID

/**
 * Data Transfer Object for MerchantProfile Entity to be used in the API.
 * This DTO is used to transfer data from the API to the service layer.
 */

data class MerchantProfileDTO(
    val merchantId: UUID,
    val businessName: String,
    val businessAddress: String,
    val businessPhone: String,
    val businessDescription: String,
    val businessType: String,
    val businessCategory: String,
    val businessSubCategory: String,
    val businessWebsite: String,
    val businessEmail: String,
    val businessLogo: String,
    val businessCoverPhoto: String,
    val user: UserDTO
) {
    companion object {
        fun fromMerchantProfileEntity(merchantProfile: MerchantProfile): MerchantProfileDTO {
            return MerchantProfileDTO(
                merchantId = merchantProfile.merchantId!!,
                businessName = merchantProfile.businessName ?: "",
                businessAddress = merchantProfile.businessAddress ?: "",
                businessPhone = merchantProfile.businessPhone ?: "",
                businessDescription = merchantProfile.businessDescription ?: "",
                businessType = merchantProfile.businessType ?: "",
                businessCategory = merchantProfile.businessCategory ?: "",
                businessSubCategory = merchantProfile.businessSubCategory ?: "",
                businessWebsite = merchantProfile.businessWebsite ?: "",
                businessEmail = merchantProfile.businessEmail ?: "",
                businessLogo = merchantProfile.businessLogo ?: "",
                businessCoverPhoto = merchantProfile.businessCoverPhoto ?: "",
                user = UserDTO.fromUserEntity(merchantProfile.getUser())
            )
        }
    }
}
