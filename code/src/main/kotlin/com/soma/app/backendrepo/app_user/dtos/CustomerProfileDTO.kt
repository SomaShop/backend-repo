package com.soma.app.backendrepo.app_user.dtos

import com.soma.app.backendrepo.app_user.profile.customer.CustomerProfile
import java.util.UUID

/**
 * DTO for CustomerProfile entity to be used in the API layer.
 * This DTO is used to transfer data between the API layer and the service layer.
 */

data class CustomerProfileDTO(
    val paymentMethod: String? = null,
    val customerID: UUID? = null,
    val userDTO: UserDTO,
    // additional DTO fields such as address, phone, etc.
) {
    companion object {
        fun fromCustomerProfileEntity(customerProfile: CustomerProfile): CustomerProfileDTO {
            return CustomerProfileDTO(
                paymentMethod = customerProfile.paymentMethod,
                userDTO = UserDTO.fromUserEntity(customerProfile.getUser()),
                customerID = customerProfile.customerId,
                // additional fields such as address, phone, etc.
            )
        }
    }
}
