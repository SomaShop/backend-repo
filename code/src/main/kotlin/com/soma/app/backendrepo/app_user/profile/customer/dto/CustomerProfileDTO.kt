package com.soma.app.backendrepo.app_user.profile.customer.dto

import com.soma.app.backendrepo.app_user.address.dto.AddressDto
import com.soma.app.backendrepo.app_user.user.model.UserEntityDTO
import com.soma.app.backendrepo.app_user.profile.customer.CustomerProfileEntity
import java.util.UUID

/**
 * DTO for CustomerProfile entity to be used in the API layer.
 * This DTO is used to transfer data between the API layer and the service layer.
 */

data class CustomerProfileDTO(
    val paymentMethod: String? = null,
    val customerID: UUID? = null,
    val userEntityDTO: UserEntityDTO,
    val addresses: List<AddressDto>? = null,
    // additional DTO fields such as address, phone, etc.
) {
    companion object {
        fun fromCustomerProfileEntity(customerProfileEntity: CustomerProfileEntity): CustomerProfileDTO {
            return CustomerProfileDTO(
                paymentMethod = customerProfileEntity.paymentMethod,
                userEntityDTO = UserEntityDTO.fromUserEntity(customerProfileEntity.getUser()),
                customerID = customerProfileEntity.customerId,
                addresses = customerProfileEntity.getAddresses().map { AddressDto.fromAddressEntity(it) },
                // additional fields such as address, phone, etc.
            )
        }
    }
}
