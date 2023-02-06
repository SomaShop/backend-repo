package com.soma.app.backendrepo.app_user.dtos

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserPermission
import java.util.UUID

/**
 * DTO for User Entity to be used in the API layer.
 */

data class UserDTO(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val permission: Set<UserPermission>,
    val verified : Boolean,
) {
    companion object {
        fun fromUserEntity(user: User): UserDTO {
            return UserDTO(
                id = user.userID!!,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                role = user.role.name,
                permission = user.permissions,
                verified = user.isEnabled(),
            )
        }
    }
}
