package com.soma.app.backendrepo.security.auth.service

import com.soma.app.backendrepo.app_user.dtos.JwtAuthenticateTokenResponse
import com.soma.app.backendrepo.app_user.dtos.JwtRegistrationTokenResponse
import com.soma.app.backendrepo.app_user.dtos.PasswordConfirmationTokenDTO
import com.soma.app.backendrepo.app_user.dtos.UserDTO
import com.soma.app.backendrepo.app_user.profile.model.Profile
import com.soma.app.backendrepo.app_user.profile.repository.ProfileRepository
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationService
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.config.JwtProperties
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.password.service.EmailService
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.security.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.utils.Logger
import org.springframework.security.authentication.*
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager,
    private val passwordConfirmationService: PasswordConfirmationService,
    private val emailService: EmailService,
    private val jwtProperties: JwtProperties
) {
    companion object {
        const val TAG = "AuthenticationService"
        val logger = Logger<AuthenticationService>().getLogger()
    }

    fun register(registrationRequest: RegistrationRequest): ApiResponse {
        val userEmail = userRepository.findByEmail(registrationRequest.email)
        // TODO: add Checks such as if password is strong enough, if email is valid, etc.
        return when {
            userEmail.isPresent -> {
                val error = GlobalRequestErrorHandler.handleUserAlreadyExistsException(
                    Exception("User with email: ${registrationRequest.email} already exists")
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body,
                    data = null
                )
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
                    firstName = registrationRequest.firstName,
                    lastName = registrationRequest.lastName,
                    role = registrationRequest.userRole,
                    permissions = registrationRequest.userRole.permissions
                )
                val confirmPassWordToken = jwtTokenProvider.createConfirmPasswordToken(user)
                val passwordTokenExpiresAt = jwtTokenProvider.getExpirationDateFromToken(confirmPassWordToken)
                val passwordConfirmationToken = PasswordConfirmationToken(
                    token = confirmPassWordToken,
                    tokenExpiresAt = passwordTokenExpiresAt,
                    createdAt = Date(),
                    user = user
                )
                assign(user, profile)
                save(user, profile, passwordConfirmationToken)
                val jwt = jwtTokenProvider.createToken(user)
                val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
                val now = Date()
                val refreshTokenExpiresAt = Date(now.time + jwtProperties.refreshExpirationTime)
                logger.info("Tag: $TAG, Message: ${user.role}")
                logger.info("Tag: $TAG, Message: ${user.permissions}")
                emailService.sendPasswordConfirmationEmail(user, passwordConfirmationToken.token)
                val passwordConfirmationTokenDTO =
                    PasswordConfirmationTokenDTO.fromPasswordConfirmationToken(passwordConfirmationToken)
                ApiResponse(
                    status = "200 OK",
                    error = null,
                    data = JwtRegistrationTokenResponse(
                        jwt,
                        tokenExpiryDate,
                        passwordConfirmationTokenDTO,
                        refreshTokenExpiresAt = refreshTokenExpiresAt,
                    )
                )
            }
        }

    }

    private fun save(
        user: User,
        profile: Profile,
        passwordConfirmationToken: PasswordConfirmationToken,
    ) {
        userRepository.save(user)
        profileRepository.save(profile)
        passwordConfirmationService.saveToken(passwordConfirmationToken)
    }

    private fun assign(
        user: User,
        profile: Profile
    ) {
        user.assignProfile(profile)
        profile.assignUser(user)
    }

    fun login(authenticationRequest: AuthenticationRequest): ApiResponse {
        logger.info("Tag: $TAG, Authenticating user with email: ${authenticationRequest.email}")
        val user = userRepository.findByEmail(authenticationRequest.email)
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.email,
                    authenticationRequest.password
                )
            )

            val jwt = jwtTokenProvider.createToken(user.get())
            val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
            val refreshExpiry = Date(Date().time + jwtProperties.refreshExpirationTime)
            val userDto = UserDTO.fromUser(user.get())
            val tokenResponseDTO = JwtAuthenticateTokenResponse(
                jwt,
                tokenExpiryDate,
                userDto,
                refreshExpiry
            )
            return ApiResponse(
                status = "200 OK",
                error = null,
                data = tokenResponseDTO
            )

        } catch (e: AuthenticationException) {
            val error = GlobalRequestErrorHandler.handleBadCredentialsException(
                when (e) {
                    is BadCredentialsException -> Exception("Incorrect Email or Password")
                    is DisabledException -> Exception("User is disabled")
                    is LockedException -> Exception("User account is locked")
                    is AccountExpiredException -> Exception("User account has expired")
                    is CredentialsExpiredException -> Exception("User credentials have expired")
                    else -> Exception("Could not authenticate user with the given credentials")
                }
            )
            return ApiResponse(
                status = error.statusCode.toString(),
                error = error.body,
            )
        }
    }

    fun findByEmail(email: String): User {
        val user = userRepository.findByEmail(email)
        return when {
            user.isEmpty || !user.isPresent -> throw Exception("User does not exist")
            else -> user.get()
        }
    }

    fun confirmEmail(token: String): ApiResponse {
        val passwordConfirmationToken = passwordConfirmationService.getToken(token)
        logger.info("Tag: $TAG, confirmEmail: $passwordConfirmationToken")
        return when {
            !passwordConfirmationToken.isPresent -> {
                val error = GlobalRequestErrorHandler.handleInvalidTokenException(
                    Exception("Token does not exist")
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body,
                )
            }

            passwordConfirmationToken.get().tokenExpiresAt?.before(Date()) == true -> {
                val error = GlobalRequestErrorHandler.handleTokenExpiredException(
                    Exception("Token has expired")
                )
                ApiResponse(
                    status = error.statusCode.toString(),
                    error = error.body
                )
            }

            else -> {
                val user = passwordConfirmationToken.get().user
                val passwordToken = passwordConfirmationToken.get().token
                passwordConfirmationService.setConfirmedAt(passwordToken)
                val confirmedUser = user.copy(enabled = true)
                userRepository.save(confirmedUser)
                ApiResponse(
                    status = "200",
                    data = "Email confirmed successfully"
                )
            }
        }
    }

}