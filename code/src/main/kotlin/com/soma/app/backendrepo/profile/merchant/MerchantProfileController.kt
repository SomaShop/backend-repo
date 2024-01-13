package com.soma.app.backendrepo.profile.merchant

import com.soma.app.backendrepo.address.pojo.AddressData
import com.soma.app.backendrepo.profile.merchant.pojo.MerchantProfileData
import com.soma.app.backendrepo.model.app_user.AuthenticatedUser
import com.soma.app.backendrepo.utils.ApiResult
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
import org.springframework.http.ResponseEntity

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
    fun getMerchantProfile(@AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser): ResponseEntity<ApiResult> {
        logger.info("TAG: MerchantProfileController - getMerchantProfile() message: ${authenticationPrincipal.userEntity.email}")
        return when (val apiResponse = merchantProfileService.getMerchantProfile(authenticationPrincipal.userEntity)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PutMapping("/profile/update")
    fun updateMerchantProfile(
        @RequestParam merchantId: UUID,
        @RequestBody updateRequest: MerchantProfileData,
        @AuthenticationPrincipal authenticationPrincipal: AuthenticatedUser
    ): ResponseEntity<ApiResult> {
        logger.info("TAG: MerchantProfileController - updateMerchantProfile()")
        val apiResponse = merchantProfileService.updateMerchantProfile(
            user = authenticationPrincipal.userEntity,
            id = merchantId,
            updateRequest = updateRequest
        )
        return when (apiResponse) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PostMapping("/createAddress")
    fun createMerchantAddress(
        @RequestParam merchantId: UUID,
        @RequestBody addressData: AddressData,
    ): ResponseEntity<ApiResult> {
        logger.info("TAG: MerchantProfileController - createAddress()")
        val apiResponse = merchantProfileService.createMerchantAddress(
            merchantId = merchantId,
            addressData = addressData
        )
        return when (apiResponse) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PutMapping("/updateAddress")
    fun updateMerchantAddress(
        @RequestParam merchantId: UUID,
        @RequestParam addressId: UUID,
        @RequestBody addressData: AddressData,
    ): ResponseEntity<ApiResult> {
        logger.info("TAG: MerchantProfileController - updateAddress()")
        val apiResponse = merchantProfileService.updateMerchantAddress(
            merchantId = merchantId,
            addressData = addressData,
            addressId = addressId
        )
        return when (apiResponse) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

}

