package com.soma.app.backendrepo.security.auth.service

import com.soma.app.backendrepo.app_user.profile.customer.CustomerProfileEntity
import com.soma.app.backendrepo.app_user.profile.customer.CustomerProfileRepository
import com.soma.app.backendrepo.app_user.profile.merchant.MerchantProfileEntity
import com.soma.app.backendrepo.app_user.profile.merchant.MerchantProfileRepository
import com.soma.app.backendrepo.app_user.user.model.UserEntityDTO
import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationService
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationTokenDTO
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.config.JwtProperties
import com.soma.app.backendrepo.error_handling.ApiResponse
import com.soma.app.backendrepo.error_handling.Exception
import com.soma.app.backendrepo.error_handling.GlobalRequestErrorHandler
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.dto.JwtAuthenticateTokenResponse
import com.soma.app.backendrepo.security.auth.dto.JwtRegistrationTokenResponseDTO
import com.soma.app.backendrepo.security.auth.reser_password.service.EmailService
import com.soma.app.backendrepo.security.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.security.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.utils.Logger
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.Date

/**
 * This class is used to authenticate the user and generate a JWT token for the user.
 * It also handles the registration of the user and the login of the user.
 */

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val customerProfileRepository: CustomerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager,
    private val passwordConfirmationService: PasswordConfirmationService,
    private val emailService: EmailService,
    private val jwtProperties: JwtProperties
) {
    private lateinit var customerProfile: CustomerProfileEntity
    private lateinit var merchantProfile: MerchantProfileEntity
    private lateinit var user: UserEntity
    private lateinit var passwordConfirmationToken: PasswordConfirmationToken
    // store associated user profile ID to be used in the profile update process
    // on the frontend
    private var associatedUserID: UUID? = null

    companion object {
        const val TAG = "AuthenticationService"
        val logger = Logger.getLogger<AuthenticationService>()
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
                user = UserEntity(
                    email = registrationRequest.email,
                    password = passwordEncoder.encode(registrationRequest.password),
                    firstName = registrationRequest.firstName,
                    lastName = registrationRequest.lastName,
                    role = registrationRequest.userRole,
                    permissions = registrationRequest.userRole.permissions
                )
                createUserProfile(registrationRequest.userRole)
                val confirmPassWordToken = jwtTokenProvider.createConfirmPasswordToken(user)
                val passwordTokenExpiresAt = jwtTokenProvider.getExpirationDateFromToken(confirmPassWordToken)
                passwordConfirmationToken = PasswordConfirmationToken(
                    token = confirmPassWordToken,
                    tokenExpiresAt = passwordTokenExpiresAt,
                    createdAt = Date(),
                    user = user
                )
                passwordConfirmationService.saveToken(passwordConfirmationToken)
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
                    data = JwtRegistrationTokenResponseDTO(
                        jwt,
                        tokenExpiryDate,
                        passwordConfirmationTokenDTO,
                        refreshTokenExpiresAt = refreshTokenExpiresAt,
                        associatedUserID = associatedUserID
                    )
                )
            }
        }

    }

    private fun createUserProfile(
        userRole: UserRole
    ) {
        when (userRole) {
            UserRole.ROLE_CUSTOMER -> {
                logger.info("Tag: $TAG, Message: Creating customer Profile")
                customerProfile = CustomerProfileEntity()
                user = userRepository.save(user)
                customerProfile.assignUser(user)
                customerProfile = customerProfileRepository.save(customerProfile)
                logger.info("Tag: $TAG, Message: customer Profile Created with ID: ${customerProfile.customerId}")
                associatedUserID = customerProfile.customerId
            }

            UserRole.ROLE_MERCHANT -> {
                logger.info("Tag: $TAG, Message: Creating Merchant Profile")
                merchantProfile = MerchantProfileEntity()
                user = userRepository.save(user)
                merchantProfile.assignUser(user)
                merchantProfile = merchantProfileRepository.save(merchantProfile)
                logger.info("Tag: $TAG, Message: Merchant Profile Created with ID: ${merchantProfile.merchantId}")
                associatedUserID = merchantProfile.merchantId
            }

            else -> {
                logger.info("Tag: $TAG, Message: Creating Admin Profile")
                user = userRepository.save(user)
                logger.info("Tag: $TAG, Message: Admin Profile Created with ID: ${user.userID}")
                associatedUserID = user.userID
            }
        }
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
            val associatedUserID = getAssociatedUserID(user.get())
            val jwt = jwtTokenProvider.createToken(user.get())
            val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
            val refreshExpiry = Date(Date().time + jwtProperties.refreshExpirationTime)
            val userEntityDto = UserEntityDTO.fromUserEntity(user.get())
            val tokenResponseDTO = JwtAuthenticateTokenResponse(
                jwt,
                tokenExpiryDate,
                userEntityDto,
                refreshExpiry,
                associatedUserID
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

     fun getAssociatedUserID(user: UserEntity) =
        when (user.role) {
            UserRole.ROLE_CUSTOMER -> {
                customerProfileRepository
                    .findByUser(user)
                    .get()
                    .customerId
            }

            UserRole.ROLE_MERCHANT -> {
                merchantProfileRepository
                    .findByUser(user)
                    .get()
                    .merchantId
            }

            else -> null
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