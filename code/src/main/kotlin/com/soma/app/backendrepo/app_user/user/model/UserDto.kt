package com.soma.app.backendrepo.app_user.user.model

import com.soma.app.backendrepo.app_user.profile.model.ProfileDTO
import org.springframework.security.core.GrantedAuthority

data class UserDto(
    val id: Long,
    val email: String,
    val password: String,
    val role: UserRole,
    val name: String,
    val profile: ProfileDTO,
    val permissions: List<UserPermission>,
    val authorities: Collection<GrantedAuthority>
)