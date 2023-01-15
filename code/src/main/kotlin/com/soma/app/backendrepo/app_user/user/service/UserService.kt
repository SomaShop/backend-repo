package com.soma.app.backendrepo.app_user.user.service

import com.soma.app.backendrepo.app_user.profile.repository.ProfileRepository
import com.soma.app.backendrepo.app_user.user.UserPrincipal
import com.soma.app.backendrepo.app_user.user.model.ClaimRoles
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserDto
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.security.JwtAuthenticationResponse
import com.soma.app.backendrepo.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun register(user: UserDto): User {
        val existingUser = userRepository.findByEmail(user.email)
        if (existingUser != null) {
            throw IllegalArgumentException("User with this email already exists")
        }
        val hashedPassword = passwordEncoder.encode(user.password)
        val newUser = User(
            name = user.name,
            email = user.email,
            password = hashedPassword,
            role = UserRole.BUYER
        )
        userRepository.save(newUser)
        return newUser
    }

    fun getRoleFromToken(token: String): UserRole {
        return jwtTokenProvider.getClaimFromToken(token, ClaimRoles.Role.role)
    }

    fun getPermissionsFromToken(token: String): List<String> {
        return jwtTokenProvider.getClaimFromToken(token, ClaimRoles.Permission.role)
    }

    fun login(user: UserDto): JwtAuthenticationResponse {
        val existingUser = userRepository.findByEmail(user.email)
            ?: throw IllegalArgumentException("Invalid email or password")
        if (!passwordEncoder.matches(user.password, existingUser.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }
        val userPrincipal = UserPrincipal(
            email = existingUser.email,
            role = existingUser.role,
            permissions = user.permissions,
            authorities = user.authorities
        )
        val token = jwtTokenProvider.createToken(userPrincipal)
        return JwtAuthenticationResponse(token)
    }
}
