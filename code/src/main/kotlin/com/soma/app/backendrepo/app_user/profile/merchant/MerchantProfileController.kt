package com.soma.app.backendrepo.app_user.profile.merchant

import com.soma.app.backendrepo.app_user.profile.merchant.pojo.MerchantProfileRequest
import com.soma.app.backendrepo.app_user.user.model.AuthenticatedUser
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.utils.Logger
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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
@RequestMapping("/api/v1/merchantProfile")
class MerchantProfileController(
    private val merchantProfileService: MerchantProfileService,
) {
    val logger = Logger<MerchantProfileController>().getLogger()

    @GetMapping
    fun getMerchantProfile(@AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser): ApiResponse {
        logger.info("TAG: MerchantProfileController - getMerchantProfile() message: ${authenticationPrincipal.user.email}")
        val apiResponse = merchantProfileService.getMerchantProfile(authenticationPrincipal.user)
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

    @PutMapping("/update")
    fun updateMerchantProfile(
        @RequestParam merchantId: UUID,
        @RequestBody updateRequest: MerchantProfileRequest,
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser
    ): ApiResponse {
        logger.info("TAG: MerchantProfileController - updateMerchantProfile()")
        val apiResponse = merchantProfileService.updateMerchantProfile(
            user = authenticationPrincipal.user,
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

}

