package com.soma.app.backendrepo.auth.service

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationServiceImpl
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
import com.soma.app.backendrepo.config.jwt.JwtTokenProvider
import com.soma.app.backendrepo.authentication.reser_password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.authentication.reser_password.service.PasswordService
import com.soma.app.backendrepo.authentication.reser_password.pojos.UpdatePasswordRequest
import com.soma.app.backendrepo.email_service.EmailService
import com.soma.app.backendrepo.utils.ApiData
import com.soma.app.backendrepo.utils.ApiResult
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PasswordServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var emailConfirmationServiceImpl: EmailConfirmationServiceImpl

    @InjectMocks
    private lateinit var passwordService: PasswordService

    @Mock
    private lateinit var emailService: EmailService

    @Before
    fun setUp() {
        userRepository = mock(
            UserRepository::class.java
        )
        jwtTokenProvider = mock(
            JwtTokenProvider::class.java
        )
        passwordEncoder = mock(
            PasswordEncoder::class.java
        )
        emailConfirmationServiceImpl = mock(
            EmailConfirmationServiceImpl::class.java
        )
        emailService = mock(
            EmailService::class.java
        )
        passwordService = PasswordService(
            userRepository,
            jwtTokenProvider,
            passwordEncoder,
            emailService,
        )
    }


    @Test
    fun `validate Reset password Request should return the appropriate response`() {
        // Arrange
        val resetRequest = ResetPasswordRequest("user@example.com")
        val userEntity = Optional.of(
            UserEntity(
                UUID.randomUUID(),
                "firstName",
                "lastName",
                "user@example.com",
                "password",
                UserRole.ROLE_CUSTOMER,
                UserRole.ROLE_CUSTOMER.permissions,
            ),
        )
        Mockito.`when`(userRepository.findByEmail(resetRequest.email)).thenReturn(userEntity)

        // Act
        val result = passwordService.resetPassword(resetRequest)

        // Assert
        Assert.assertEquals(result, ApiResult.Success(ApiData(Any())))
    }

    @Test
    fun `updatePassword should update the user's password`() {
        val userEntity = UserEntity(
            UUID.randomUUID(),
            "jane",
            "doe",
            "jane@example.com",
            "password",
            UserRole.ROLE_CUSTOMER,
            UserRole.ROLE_CUSTOMER.permissions,
        )
        val newPassword = UpdatePasswordRequest("newPassword", "newPassword")
        val updatedPassUser = userEntity.copy(password = newPassword.password)
        Mockito.`when`(passwordEncoder.encode(newPassword.password)).thenReturn(newPassword.password)
        Mockito.`when`(userRepository.save(updatedPassUser)).thenReturn(updatedPassUser)

        passwordService.updatePassword("token", newPassword)

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(newPassword.password)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(updatedPassUser)
        Assert.assertEquals(newPassword, updatedPassUser.getPassword())
    }


    @Test
    fun `findUser should return the appropriate user`() {
        val token = "token"
        val userEntity = Optional.of(
            UserEntity(
                UUID.randomUUID(),
                "firstName",
                "lastName",
                "user@example.com",
                "password",
                UserRole.ROLE_CUSTOMER,
                UserRole.ROLE_CUSTOMER.permissions,
            )
        )
        Mockito.`when`(jwtTokenProvider.getEmailFromToken(token)).thenReturn("user@example.com")
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(userEntity)

        val result = passwordService.findUser(token)

        Assert.assertEquals(result, userEntity.get())
    }

}