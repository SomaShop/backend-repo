package com.soma.app.backendrepo.authentication.auth.controller

import com.soma.app.backendrepo.model.app_user.UserEntityDTO
import com.soma.app.backendrepo.model.app_user.AuthenticatedUser
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth-demo")
class AuthDemoController {
    companion object {
        const val TAG = "AuthDemoController"
    }

    @GetMapping
    fun testDemo(@AuthenticationPrincipal authenticatedUser: AuthenticatedUser): ResponseEntity<String> {
        val user = authenticatedUser.userEntity
        return ResponseEntity.ok(
            "$TAG:: Hello ${user.firstName} from secure endpoint. " +
                    "Your role is ${user.role}. You have these authorities email is: ${user.permissions}")
    }

    /**
     * Make sure user is authenticated before accessing this endpoint
     * otherwise it will throw 403 Forbidden
     */
    @GetMapping("/current-user")
    fun getCurrentUser(@AuthenticationPrincipal authenticatedUser: AuthenticatedUser): ApiResult {
        val user = authenticatedUser.userEntity
        val userEntityDTO = UserEntityDTO.fromUserEntity(user)
        return ApiResult.Success(data = ApiData(userEntityDTO))
    }
}