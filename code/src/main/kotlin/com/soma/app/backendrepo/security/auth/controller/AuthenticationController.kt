package com.soma.app.backendrepo.security.auth.controller

import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.security.auth.service.AuthenticationService
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.security.auth.pojos.RegistrationRequest
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/register")
    fun register(
        @RequestBody registrationRequest: RegistrationRequest
    ): ApiResponse {
        val apiResponse = authenticationService.register(registrationRequest)
        return when(apiResponse.error) {
            null -> {
                ApiResponse(
                    status = apiResponse.status,
                    error = null,
                    data = apiResponse.data
                )
            }
            else -> {
                ApiResponse(
                    status = apiResponse.status,
                    error = apiResponse.error,
                    data = apiResponse.data
                )
            }
        }

    }

    @PostMapping("/login")
    fun authenticateUser(
        @RequestBody authenticationRequest: AuthenticationRequest
    ): ApiResponse {
        val response = authenticationService.login(authenticationRequest)
        return when(response.error) {
            null -> {
                ApiResponse(
                    status = response.status,
                    error = null,
                    data = response.data
                )
            }
            else -> {
                ApiResponse(
                    status = response.status,
                    error = response.error,
                    data = response.data
                )
            }
        }
    }

    @PostMapping("/confirm-password")
    fun confirmEmail(@RequestParam token: String): ApiResponse {
        val response = authenticationService.confirmEmail(token)
        return when (response.error) {
            null -> {
                ApiResponse(
                    status = response.status,
                    error = null,
                    data = response.data
                )
            }
            else -> {
                ApiResponse(
                    status = response.status,
                    error = response.error,
                    data = response.data
                )
            }
        }
    }
}