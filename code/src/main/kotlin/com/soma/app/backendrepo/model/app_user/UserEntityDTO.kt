package com.soma.app.backendrepo.model.app_user

import java.util.UUID

/**
 * DTO for User Entity to be used in the API layer.
 */

data class UserEntityDTO(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val permission: Set<UserPermission>,
    val verified : Boolean,
) {
    companion object {
        fun fromUserEntity(userEntity: UserEntity): UserEntityDTO {
            return UserEntityDTO(
                id = userEntity.userID!!,
                firstName = userEntity.firstName,
                lastName = userEntity.lastName,
                email = userEntity.email,
                role = userEntity.role.name,
                permission = userEntity.permissions,
                verified = userEntity.isEnabled(),
            )
        }
    }
}
