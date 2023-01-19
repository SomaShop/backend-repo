package com.soma.app.backendrepo.security.auth.service

import com.soma.app.backendrepo.app_user.profile.model.Profile
import com.soma.app.backendrepo.app_user.profile.repository.ProfileRepository
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationResponse
import com.soma.app.backendrepo.security.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.utils.Logger
import com.soma.app.backendrepo.utils.RequestResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {
    companion object {
        const val TAG = "AuthenticationService"
        val log = Logger<AuthenticationService>().getLogger()
    }
    fun register(registrationRequest: RegistrationRequest): ResponseEntity<RequestResponse<AuthenticationResponse>> {
        val userEmail = userRepository.findByEmail(registrationRequest.email)
        return when {
            userEmail.isPresent -> {
                ResponseEntity.badRequest().body(RequestResponse.Error("Email already exists"))
            }

            else -> {
                val profile = Profile(
                    name = registrationRequest.firstName,
                    description = "I am ${registrationRequest.firstName}",
                    address = "Somewhere in the world",
                    phone = "0000000000",
                )
                val user = User(
                    email = registrationRequest.email,
                    password = passwordEncoder.encode(registrationRequest.password),
                    FirstName = registrationRequest.firstName,
                    LastName = registrationRequest.lastName,
                    role = registrationRequest.userRole,
                    permissions = registrationRequest.userRole.permissions
                )
                user.assignProfile(profile)
                profile.assignUser(user)
                userRepository.save(user)
                profileRepository.save(profile)
                val jwt = jwtTokenProvider.createToken(user)
                val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
                log.info("Tag: $TAG, Message: ${user.role}")
                log.info("Tag: $TAG, Message: ${user.permissions}")
                ResponseEntity.ok().body(RequestResponse.Success(AuthenticationResponse(jwt, tokenExpiryDate)))
            }
        }

    }

    fun login(authenticationRequest: AuthenticationRequest): ResponseEntity<RequestResponse<AuthenticationResponse>> {
        val user = userRepository.findByEmail(authenticationRequest.email)
        val authenticateResult = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.email,
                authenticationRequest.password
            )
        )
        return when {
            user.isEmpty || !user.isPresent -> {
                ResponseEntity.badRequest().body(RequestResponse.Error("User does not exist"))
            }

            !passwordEncoder.matches(authenticationRequest.password, user.get().password) -> {
                ResponseEntity.badRequest().body(RequestResponse.Error("Incorrect password"))
            }

            authenticateResult.isAuthenticated -> {
                val jwt = jwtTokenProvider.createToken(user.get())
                val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
                ResponseEntity.ok().body(RequestResponse.Success(AuthenticationResponse(jwt, tokenExpiryDate)))
            }
            else -> {
                ResponseEntity.badRequest().body(RequestResponse.Error("Could not authenticate user with the given credentials"))
            }
        }
    }

    fun findByEmail(email: String): User {
        val user = userRepository.findByEmail(email)
        return when  {
            user.isEmpty || !user.isPresent-> throw Exception("User does not exist")
            else -> user.get()
        }
    }

}