package com.soma.app.backendrepo.profile.customer

import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.profile.customer.pojo.CustomerProfileData
import com.soma.app.backendrepo.model.app_user.AuthenticatedUser
import com.soma.app.backendrepo.error_handling.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Controller for CustomerProfile entity.
 * This controller is used to handle requests from the API layer.
 */

@RestController
@RequestMapping("/api/v1/customer")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
class CustomerProfileController(
    private val customerProfileService: CustomerProfileService
) {
    @GetMapping("/profile")
    fun getCustomerProfile(
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser
    ): ApiResponse {
        val apiResponse = customerProfileService.getCustomerProfile(authenticationPrincipal.userEntity)
        return when (apiResponse.error) {
            null -> ApiResponse(
                data = apiResponse.data,
                status = apiResponse.status,
            )
            else -> {
                ApiResponse(
                    error = apiResponse.error,
                    status = apiResponse.status,
                )
            }
        }
    }

    @PutMapping("/profile/update")
    fun updateCustomerProfile(
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser,
        @RequestParam customerId: UUID,
        @RequestBody customerProfileRequest: CustomerProfileData
    ): ApiResponse {
        val apiResponse = customerProfileService.updateCustomerProfile(
            authenticationPrincipal.userEntity,
            customerId,
            customerProfileRequest
        )
        return when (apiResponse.error) {
            null -> ApiResponse(
                data = apiResponse.data,
                status = apiResponse.status,
            )
            else -> {
                ApiResponse(
                    error = apiResponse.error,
                    status = apiResponse.status,
                )
            }
        }
    }

    @PutMapping("/updatePaymentMethod")
    fun updateCustomerPaymentMethod(
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser,
        @RequestParam customerId: UUID,
        @RequestBody customerProfileRequest: CustomerProfileData
    ): ApiResponse {
        val apiResponse = customerProfileService.updateCustomerPaymentMethod(
            authenticationPrincipal.userEntity,
            customerId,
            customerProfileRequest
        )
        return when (apiResponse.error) {
            null -> ApiResponse(
                data = apiResponse.data,
                status = apiResponse.status,
            )
            else -> {
                ApiResponse(
                    error = apiResponse.error,
                    status = apiResponse.status,
                )
            }
        }
    }

    @PostMapping("/createAddress")
    fun createCustomerAddress(
        @RequestParam customerId: UUID,
        @RequestBody addressData: AddressData
    ): ApiResponse {
        val apiResponse = customerProfileService.createCustomerAddress(
            customerId,
            addressData
        )
        return when (apiResponse.error) {
            null -> ApiResponse(
                data = apiResponse.data,
                status = apiResponse.status,
            )
            else -> {
                ApiResponse(
                    error = apiResponse.error,
                    status = apiResponse.status,
                )
            }
        }
    }

    @PutMapping("/updateAddress")
    fun updateCustomerAddress(
        @RequestParam customerId: UUID,
        @RequestParam addressId: UUID,
        @RequestBody addressData: AddressData
    ): ApiResponse {
        val apiResponse = customerProfileService.updateCustomerAddress(
            customerId,
            addressData,
            addressId
        )
        return when (apiResponse.error) {
            null -> ApiResponse(
                data = apiResponse.data,
                status = apiResponse.status,
            )
            else -> {
                ApiResponse(
                    error = apiResponse.error,
                    status = apiResponse.status,
                )
            }
        }
    }
}