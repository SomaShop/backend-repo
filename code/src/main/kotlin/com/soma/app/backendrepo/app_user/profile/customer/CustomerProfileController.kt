package com.soma.app.backendrepo.app_user.profile.customer

import com.soma.app.backendrepo.app_user.profile.customer.pojo.CustomerProfileRequest
import com.soma.app.backendrepo.app_user.user.model.AuthenticatedUser
import com.soma.app.backendrepo.error_handling.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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
@RequestMapping("/api/v1/customerProfile")
class CustomerProfileController(
    private val customerProfileService: CustomerProfileService
) {
    @GetMapping
    fun getCustomerProfile(
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser
    ): ApiResponse {
        val apiResponse = customerProfileService.getCustomerProfile(authenticationPrincipal.user)
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

    @PutMapping("/update")
    fun updateCustomerProfile(
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser,
        @RequestParam customerId: UUID,
        @RequestBody customerProfileRequest: CustomerProfileRequest
    ): ApiResponse {
        val apiResponse = customerProfileService.updateCustomerProfile(
            authenticationPrincipal.user,
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
}