package com.soma.app.backendrepo.authentication.reser_password.controller

import com.soma.app.backendrepo.authentication.reser_password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.authentication.reser_password.pojos.UpdatePasswordRequest
import com.soma.app.backendrepo.authentication.reser_password.service.PasswordService
import com.soma.app.backendrepo.utils.ApiResult
import com.soma.app.backendrepo.utils.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/v1/password")
class PasswordController(
    private val passwordService: PasswordService,
) {

    val logger = Logger.getLogger<PasswordController>()

    @PostMapping("/reset_password")
    fun resetPassword(@RequestBody resetRequest: ResetPasswordRequest): ResponseEntity<ApiResult> {
        return when (val apiResponse = passwordService.resetPassword(resetRequest)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }

    @PutMapping("/update_password")
    fun updatePassword(
        @RequestParam token: String,
        @RequestBody updateRequest: UpdatePasswordRequest
    ): ResponseEntity<ApiResult> {
       return when (val apiResponse = passwordService.updatePassword(token, updateRequest)) {
            is ApiResult.Success -> ResponseEntity.ok(apiResponse)
            is ApiResult.Error -> ResponseEntity.badRequest().body(apiResponse)
        }
    }
}
