package com.soma.app.backendrepo.security.auth.controller

import com.soma.app.backendrepo.app_user.dtos.UserDTO
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.security.auth.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/auth-demo")
class AuthDemoController(
    private val authenticationService: AuthenticationService
) {
    companion object {
        const val TAG = "AuthDemoController"
    }

    @GetMapping
    fun testDemo(principle: Principal): ResponseEntity<String> {
        val email = principle.name
        val user = authenticationService.findByEmail(email)
        return ResponseEntity.ok(
            "$TAG:: Hello ${user.firstName} from secure endpoint. " +
                    "Your role is ${user.role}. You have these authorities email is: ${user.permissions}")
    }

    /**
     * Make sure user is authenticated before accessing this endpoint
     * otherwise it will throw 403 Forbidden
     */
    @GetMapping("/current-user")
    fun getCurrentUser(principle: Principal): ApiResponse {
        val email = principle.name
        val user = authenticationService.findByEmail(email)
        val userDTO = UserDTO.fromUser(user)
        return ApiResponse(
            status = "200 OK",
            error = null,
            data = userDTO
        )
    }
}