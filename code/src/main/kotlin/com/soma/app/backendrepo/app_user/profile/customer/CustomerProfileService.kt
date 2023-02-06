package com.soma.app.backendrepo.app_user.profile.customer

import com.soma.app.backendrepo.app_user.dtos.CustomerProfileDTO
import com.soma.app.backendrepo.app_user.profile.customer.pojo.CustomerProfileRequest
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.isCustomer
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Service for CustomerProfile entity.
 * This service is used to handle business logic.
 * This service is used to interact with the repository.
 *
 */

@Service
class CustomerProfileService(
    private val customerProfileRepository: CustomerProfileRepository
) {
    fun getCustomerProfile(user: User): ApiResponse {
        val customerProfile = customerProfileRepository.findByUser(user)
        return when {
            !customerProfile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        errorMessage = "Customer profile not found",
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }
            !user.isCustomer() -> {
                val error = GlobalRequestErrorHandler.handleForbiddenException(
                    Exception(
                        "You are not authorized to access this resource"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }
            else -> {
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    customerProfile.get()
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }

    @Transactional
    fun updateCustomerProfile(
        user: User,
        id: UUID,
        updateRequest: CustomerProfileRequest
    ): ApiResponse {
        val profile = customerProfileRepository.findByCustomerId(id)
        return when {
            !user.isCustomer() -> {
                val error = GlobalRequestErrorHandler.handleForbiddenException(
                    Exception(
                        "You are not authorized to access this resource"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }
            !profile.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserNotFoundException(
                    Exception(
                        "Customer profile not found"
                    )
                )
                ApiResponse(
                    status = error.statusCode.name,
                    error = error.responseData
                )
            }
            else -> {
                val customerProfile = profile.get()
                val updatedCustomerProfile = customerProfile.copy(
                    paymentMethod = updateRequest.paymentMethod,
                )
                updatedCustomerProfile.assignUser(user)
                customerProfileRepository.save(updatedCustomerProfile)
                val customerProfileDTO = CustomerProfileDTO.fromCustomerProfileEntity(
                    updatedCustomerProfile
                )
                ApiResponse(
                    status = "200 OK",
                    data = customerProfileDTO
                )
            }
        }
    }
}