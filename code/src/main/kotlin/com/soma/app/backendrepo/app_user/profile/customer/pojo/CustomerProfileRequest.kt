package com.soma.app.backendrepo.app_user.profile.customer.pojo

/**
 * Request body for updating customer profile information such as address, phone, etc.
 *
 */

data class CustomerProfileRequest(
    val paymentMethod: String? = null,
    // additional fields to update such as address, phone, etc.
)

