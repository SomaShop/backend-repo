package com.soma.app.backendrepo.authentication.auth.controller

import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.authentication.auth.service.AuthenticationServiceImpl
import com.soma.app.backendrepo.authentication.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.authentication.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.utils.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authenticationServiceImpl: AuthenticationServiceImpl
) {

    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: RegistrationRequest) : ResponseEntity<ApiResult> {
        return when(val apiResponse = authenticationServiceImpl.register(registrationRequest)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PostMapping("/login")
    fun authenticateUser(
        @RequestBody authenticationRequest: AuthenticationRequest
    ): ResponseEntity<ApiResult> {
        return when(val apiResponse = authenticationServiceImpl.login(authenticationRequest)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PostMapping("/confirm-email")
    fun confirmEmail(@RequestParam token: String): ResponseEntity<ApiResult> {
        return when (val apiResponse = authenticationServiceImpl.confirmEmail(token)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }
}