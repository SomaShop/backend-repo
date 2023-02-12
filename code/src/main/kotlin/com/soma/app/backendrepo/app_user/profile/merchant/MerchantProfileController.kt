package com.soma.app.backendrepo.app_user.profile.merchant

import com.soma.app.backendrepo.app_user.address.pojo.AddressData
import com.soma.app.backendrepo.app_user.profile.merchant.pojo.MerchantProfileData
import com.soma.app.backendrepo.app_user.user.model.AuthenticatedUser
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.utils.Logger
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
 * Controller for MerchantProfile entity.
 * This controller is used to handle requests from the API layer.
 */


@RestController
@RequestMapping("/api/v1/merchant")
@PreAuthorize("hasRole('ROLE_MERCHANT')")
class MerchantProfileController(
    private val merchantProfileService: MerchantProfileService,
) {
    val logger = Logger.getLogger<MerchantProfileController>()

    @GetMapping("/profile")
    fun getMerchantProfile(@AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser): ApiResponse {
        logger.info("TAG: MerchantProfileController - getMerchantProfile() message: ${authenticationPrincipal.userEntity.email}")
        val apiResponse = merchantProfileService.getMerchantProfile(authenticationPrincipal.userEntity)
        return when (apiResponse.error) {
            null -> {
                ApiResponse(
                    status = apiResponse.status,
                    data = apiResponse.data
                )
            }

            else -> {
                return ApiResponse(
                    status = apiResponse.status,
                    error = apiResponse.error,
                    data = apiResponse.data
                )
            }
        }
    }

    @PutMapping("/profile/update")
    fun updateMerchantProfile(
        @RequestParam merchantId: UUID,
        @RequestBody updateRequest: MerchantProfileData,
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser
    ): ApiResponse {
        logger.info("TAG: MerchantProfileController - updateMerchantProfile()")
        val apiResponse = merchantProfileService.updateMerchantProfile(
            user = authenticationPrincipal.userEntity,
            id = merchantId,
            updateRequest = updateRequest
        )
        return when (apiResponse.error) {
            null -> {
                ApiResponse(
                    status = apiResponse.status,
                    data = apiResponse.data
                )
            }

            else -> {
                return ApiResponse(
                    status = apiResponse.status,
                    error = apiResponse.error,
                    data = apiResponse.data
                )
            }
        }
    }

    @PostMapping("/createAddress")
    fun createMerchantAddress(
        @RequestParam merchantId: UUID,
        @RequestBody addressData: AddressData,
    ): ApiResponse {
        logger.info("TAG: MerchantProfileController - createAddress()")
        val apiResponse = merchantProfileService.createMerchantAddress(
            merchantId = merchantId,
            addressData = addressData
        )
        return when (apiResponse.error) {
            null -> {
                ApiResponse(
                    status = apiResponse.status,
                    data = apiResponse.data
                )
            }

            else -> {
                return ApiResponse(
                    status = apiResponse.status,
                    error = apiResponse.error,
                    data = apiResponse.data
                )
            }
        }
    }

    @PutMapping("/updateAddress")
    fun updateMerchantAddress(
        @RequestParam merchantId: UUID,
        @RequestParam addressId: UUID,
        @RequestBody addressData: AddressData,
    ): ApiResponse {
        logger.info("TAG: MerchantProfileController - updateAddress()")
        val apiResponse = merchantProfileService.updateMerchantAddress(
            merchantId = merchantId,
            addressData = addressData,
            addressId = addressId
        )
        return when (apiResponse.error) {
            null -> {
                ApiResponse(
                    status = apiResponse.status,
                    data = apiResponse.data
                )
            }

            else -> {
                return ApiResponse(
                    status = apiResponse.status,
                    error = apiResponse.error,
                    data = apiResponse.data
                )
            }
        }
    }

}

