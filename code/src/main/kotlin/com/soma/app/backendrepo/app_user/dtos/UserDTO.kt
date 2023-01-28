package com.soma.app.backendrepo.app_user.dtos

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserPermission
import java.util.UUID

data class UserDTO(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val permission: Set<UserPermission>,
    val verified : Boolean
) {
    companion object {
        fun fromUser(user: User): UserDTO {
            return UserDTO(
                id = user.getId()!!,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.username,
                role = user.role.name,
                permission = user.permissions,
                verified = user.isEnabled
            )
        }
    }
}
