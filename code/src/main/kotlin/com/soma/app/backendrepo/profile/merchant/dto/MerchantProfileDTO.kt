package com.soma.app.backendrepo.profile.merchant.dto

import com.soma.app.backendrepo.address.dto.AddressDto
import com.soma.app.backendrepo.model.app_user.UserEntityDTO
import com.soma.app.backendrepo.profile.merchant.MerchantProfileEntity
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
    val user: UserEntityDTO,
    val addresses : List<AddressDto>
) {
    companion object {
        fun fromMerchantProfileEntity(merchantProfileEntity: MerchantProfileEntity): MerchantProfileDTO {
            return MerchantProfileDTO(
                merchantId = merchantProfileEntity.merchantId!!,
                businessName = merchantProfileEntity.businessName ?: "",
                businessAddress = merchantProfileEntity.businessAddress ?: "",
                businessPhone = merchantProfileEntity.businessPhoneNumber ?: "",
                businessDescription = merchantProfileEntity.businessDescription ?: "",
                businessType = merchantProfileEntity.businessType ?: "",
                businessCategory = merchantProfileEntity.businessCategory ?: "",
                businessSubCategory = merchantProfileEntity.businessSubCategory ?: "",
                businessWebsite = merchantProfileEntity.businessWebsite ?: "",
                businessEmail = merchantProfileEntity.businessEmail ?: "",
                businessLogo = merchantProfileEntity.businessLogo ?: "",
                businessCoverPhoto = merchantProfileEntity.businessCoverPhoto ?: "",
                user = UserEntityDTO.fromUserEntity(merchantProfileEntity.getUser()),
                addresses = merchantProfileEntity.getAddresses().map { AddressDto.fromAddressEntity(it) }
            )
        }
    }
}
