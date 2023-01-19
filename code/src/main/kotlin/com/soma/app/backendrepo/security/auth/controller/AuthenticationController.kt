package com.soma.app.backendrepo.security.auth.controller

import com.soma.app.backendrepo.security.auth.service.AuthenticationService
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationResponse
import com.soma.app.backendrepo.security.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.utils.RequestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    companion object {
        const val TAG = "AuthenticationController"
    }
    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: RegistrationRequest): AuthenticationResponse {
        val response = authenticationService.register(registrationRequest)
        return when (response.body) {
            is RequestResponse.Success -> {
                (response.body as RequestResponse.Success).data
            }
            is RequestResponse.Error -> {
                val message = (response.body as RequestResponse.Error).message
                throw Exception("tag: $TAG, message: error occurred while registering user: $message ")
            }
            else -> {
                throw Exception("Unknown error occurred while registering user status: ${response.statusCode}")
            }
        }

    }

    @PostMapping("/login")
    fun authenticateUser(@RequestBody authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        val response = authenticationService.login(authenticationRequest)
        return when (response.body) {
            is RequestResponse.Success -> {
                (response.body as RequestResponse.Success).data
            }
            is RequestResponse.Error -> {
                val message = (response.body as RequestResponse.Error).message
                throw Exception("tag: $TAG, message: error occurred while logging in user: $message ")
            }
            else -> {
                throw Exception("tag: $TAG, message: Unknown error occurred while logging in user status: ${response.statusCode}")
            }
        }
    }

    @GetMapping("/test")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World from secure endpoint")
    }
}