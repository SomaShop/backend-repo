package com.soma.app.backendrepo.app_user.profile.customer.pojo

import com.soma.app.backendrepo.app_user.address.pojo.AddressData

/**
 * Request body for updating customer profile information such as address, phone, etc.
 *
 */

data class CustomerProfileData(
    val paymentMethod: String? = null,
    val customerPhoneNumber: String? = null,
    val address: AddressData? = null,
    // additional fields to update such as address, phone, etc.
)

