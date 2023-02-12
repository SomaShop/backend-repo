package com.soma.app.backendrepo.app_user.profile.merchant.pojo

import com.soma.app.backendrepo.app_user.address.pojo.AddressData

/**
 * Request body for updating merchant profile information such as address, phone, etc.
 *
 */

data class MerchantProfileData(
    val businessName: String? = null,
    val businessAddress: String? = null,
    val businessPhone: String? = null,
    val businessDescription: String? = null,
    val businessType: String? = null,
    val businessCategory: String,
    val businessWebsite: String? = null,
    val businessEmail: String? = null,
    val businessLogo: String? = null,
    val businessCoverPhoto: String? = null,
)
