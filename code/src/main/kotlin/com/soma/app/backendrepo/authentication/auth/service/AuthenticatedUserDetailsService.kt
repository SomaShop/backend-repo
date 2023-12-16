package com.soma.app.backendrepo.authentication.auth.service

import com.soma.app.backendrepo.model.app_user.AuthenticatedUser
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
import com.soma.app.backendrepo.error_handling.Exception
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * This class is used to load the user from the database and return an AuthenticatedUser object
 * which is used by spring security to authenticate the user.
 *
 */

@Service
class AuthenticatedUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): AuthenticatedUser {
        return userRepository
            .findByEmail(username)
            .map { AuthenticatedUser(it) }
            .orElseThrow {
                UsernameNotFoundException("Could not find user with email: $username")
            }
    }
}