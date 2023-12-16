package com.soma.app.backendrepo.profile.customer.pojo

import com.soma.app.backendrepo.address.pojo.AddressData

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

