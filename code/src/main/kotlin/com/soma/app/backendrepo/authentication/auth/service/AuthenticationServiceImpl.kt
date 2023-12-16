package com.soma.app.backendrepo.authentication.auth.service

import com.soma.app.backendrepo.profile.customer.CustomerProfileEntity
import com.soma.app.backendrepo.profile.customer.CustomerProfileRepository
import com.soma.app.backendrepo.profile.merchant.MerchantProfileEntity
import com.soma.app.backendrepo.profile.merchant.MerchantProfileRepository
import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationService
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationTokenEntity
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationTokenDTO
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
import com.soma.app.backendrepo.config.application_properties.JwtProperties
import com.soma.app.backendrepo.config.jwt.JwtTokenProvider
import com.soma.app.backendrepo.authentication.auth.dto.JwtAuthenticateTokenResponse
import com.soma.app.backendrepo.authentication.auth.dto.JwtRegistrationTokenResponseDTO
import com.soma.app.backendrepo.email_service.EmailService
import com.soma.app.backendrepo.authentication.auth.pojos.AuthenticationRequest
import com.soma.app.backendrepo.authentication.auth.pojos.RegistrationRequest
import com.soma.app.backendrepo.authentication.auth.pojos.isNotStrongPassword
import com.soma.app.backendrepo.authentication.auth.pojos.isValidEmail
import com.soma.app.backendrepo.error_handling.ErrorCode
import com.soma.app.backendrepo.error_handling.exceptions.ApiException
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiError
import com.soma.app.backendrepo.utils.ApiResult
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
import org.springframework.http.HttpStatus

/**
 * This class is used to authenticate the user and generate a JWT token for the user.
 * It also handles the registration of the user and the login of the user.
 */

interface AuthenticationService {
    fun register(registrationRequest: RegistrationRequest): ApiResult
    fun login(authenticationRequest: AuthenticationRequest): ApiResult
    fun confirmEmail(token: String): ApiResult
}

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val customerProfileRepository: CustomerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager,
    private val emailConfirmationService: EmailConfirmationService,
    private val emailService: EmailService,
    private val jwtProperties: JwtProperties
): AuthenticationService {
    private lateinit var customerProfile: CustomerProfileEntity
    private lateinit var merchantProfile: MerchantProfileEntity
    private lateinit var user: UserEntity
    private lateinit var emailConfirmationTokenEntity: EmailConfirmationTokenEntity

    // store associated user profile ID to be used in the profile update process
    // on the frontend
    private var associatedUserID: UUID? = null

    companion object {
        const val TAG = "AuthenticationService"
        val logger = Logger.getLogger<AuthenticationServiceImpl>()
    }

    override fun register(registrationRequest: RegistrationRequest): ApiResult {
        var errorMessage = ""
        var errorCode = ""
        if (!registrationRequest.email.isValidEmail()) {
            errorMessage = "Email is not valid. Please put in a valid email"
            errorCode = ErrorCode.INVALID_EMAIL.name
        } else if (registrationRequest.isNotValid()) {
            errorMessage = "Please fill in all the required fields"
            errorCode = ErrorCode.INVALID_INPUT.name
        } else if (registrationRequest.password.isNotStrongPassword()) {
            errorMessage = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
            errorCode = ErrorCode.WEAK_PASSWORD.name
        } else if (registrationRequest.password != registrationRequest.confirmPassword) {
            errorMessage = "Passwords do not match"
            errorCode = ErrorCode.PASSWORD_MISMATCH.name
        }
        return if (errorMessage.isNotEmpty() && errorCode.isNotEmpty()) {
            val apiError = ApiError(errorCode = errorCode, message = errorMessage)
            ApiResult.Error(apiError)
            throw ApiException(apiError = apiError, status = HttpStatus.BAD_REQUEST.value())
        } else {
            val userEmail = userRepository.findByEmail(registrationRequest.email)
            when {
                userEmail.isPresent -> {
                    errorMessage = "User with email: ${registrationRequest.email} already exists"
                    errorCode = ErrorCode.USER_ALREADY_EXISTS.name
                    val apiError = ApiError(errorCode = errorCode, message = errorMessage)
                    ApiResult.Error(apiError)
                    throw ApiException(apiError = apiError, status = HttpStatus.INTERNAL_SERVER_ERROR.value())
                }
                else -> {
                    val dto = createRegistrationToken(registrationRequest)
                    ApiResult.Success(data = ApiData(dto))
                }
            }
        }
    }

    private fun createRegistrationToken(registrationRequest: RegistrationRequest): JwtRegistrationTokenResponseDTO {
        user = UserEntity(
            email = registrationRequest.email,
            password = passwordEncoder.encode(registrationRequest.password),
            firstName = registrationRequest.firstName,
            lastName = registrationRequest.lastName,
            role = registrationRequest.userRole,
            permissions = registrationRequest.userRole.permissions
        )
        createUserProfile(registrationRequest.userRole)
        val confirmPassWordToken = jwtTokenProvider.createPasswordToken(user)
        val passwordTokenExpiresAt = jwtTokenProvider.getExpirationDateFromToken(confirmPassWordToken)
        emailConfirmationTokenEntity = EmailConfirmationTokenEntity(
            token = confirmPassWordToken,
            tokenExpiresAt = passwordTokenExpiresAt,
            createdAt = Date(),
            userId = user.userID
        )
        emailConfirmationService.saveToken(emailConfirmationTokenEntity)
        val jwt = jwtTokenProvider.createToken(user)
        val tokenExpiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt)
        val now = Date()
        val refreshTokenExpiresAt = Date(now.time + jwtProperties.refreshExpirationTime)
        logger.info("Tag: $TAG, Message: ${user.role}")
        logger.info("Tag: $TAG, Message: ${user.permissions}")
        emailService.sendEmailConfirmationEmail(user, emailConfirmationTokenEntity.token)
        val emailConfirmationTokenDTO =
            EmailConfirmationTokenDTO.fromEmailConfirmationToken(emailConfirmationTokenEntity)
        return JwtRegistrationTokenResponseDTO(
            jwt,
            tokenExpiryDate,
            emailConfirmationTokenDTO,
            refreshTokenExpiresAt = refreshTokenExpiresAt,
            associatedUserID = associatedUserID
        )
    }

    private fun createUserProfile(userRole: UserRole) {
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

    override fun login(authenticationRequest: AuthenticationRequest): ApiResult {
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
            val tokenResponseDTO = JwtAuthenticateTokenResponse(
                jwt,
                tokenExpiryDate,
                user.get().userID,
                refreshExpiry,
                associatedUserID
            )

            return ApiResult.Success(data = ApiData(tokenResponseDTO))

        } catch (e: AuthenticationException) {
            val localizedMessage = when (e) {
                is BadCredentialsException -> e.message
                is DisabledException -> e.localizedMessage
                is LockedException -> e.localizedMessage
                is AccountExpiredException -> e.localizedMessage
                is CredentialsExpiredException -> e.localizedMessage
                else -> "Could not authenticate user with the given credentials"
            }
            val apiError = ApiError(errorCode = ErrorCode.BAD_CREDENTIALS.name, message = localizedMessage)
            throw ApiException(apiError = apiError, status = HttpStatus.UNAUTHORIZED.value())
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

    override fun confirmEmail(token: String): ApiResult {
        val emailConfirmationToken = emailConfirmationService.getToken(token)
        logger.info("Tag: $TAG, confirmEmail: $emailConfirmationToken")
        val errorMessage: String
        val errorCode: String
        val apiError: ApiError
        return when {
            !emailConfirmationToken.isPresent -> {
                errorMessage = "Token does not exist"
                errorCode = ErrorCode.TOKEN_NOT_FOUND.name
                apiError = ApiError(errorCode = errorCode, message = errorMessage)
                throw ApiException(apiError = apiError, status = HttpStatus.NOT_FOUND.value())
            }

            emailConfirmationToken.get().tokenExpiresAt?.before(Date()) == true -> {
                errorMessage = "Token has expired"
                errorCode = ErrorCode.TOKEN_EXPIRED.name
                apiError = ApiError(errorCode = errorCode, message = errorMessage)
                throw ApiException(apiError = apiError, status = HttpStatus.NOT_FOUND.value())
            }

            else -> {
                val userId = emailConfirmationToken.get().userId
                val user = userId?.let { userRepository.findById(it) }
                if (user?.isPresent == false) {
                    errorMessage = "User not found"
                    errorCode = ErrorCode.USER_NOT_FOUND.name
                    apiError = ApiError(errorCode = errorCode, message = errorMessage)
                    ApiResult.Error(apiError)
                    throw ApiException(apiError = apiError, status = HttpStatus.NOT_FOUND.value())
                }

                val passwordToken = emailConfirmationToken.get().token
                emailConfirmationService.setConfirmedAt(passwordToken)
                val confirmedUser = user!!.get().copy(enabled = true)
                userRepository.save(confirmedUser)
                ApiResult.Success(data = ApiData("Email confirmed successfully"))
            }
        }
    }

}