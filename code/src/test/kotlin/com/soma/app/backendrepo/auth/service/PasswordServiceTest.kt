package com.soma.app.backendrepo.auth.service

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationService
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.reser_password.pojos.ResetPasswordRequest
import com.soma.app.backendrepo.security.auth.reser_password.service.PasswordService
import com.soma.app.backendrepo.app_user.dtos.JwtResetPasswordTokenResponse
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
    private lateinit var passwordConfirmationService: PasswordConfirmationService

    @InjectMocks
    private lateinit var passwordService: PasswordService

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
        passwordConfirmationService = mock(
            PasswordConfirmationService::class.java
        )
        passwordService = PasswordService(
            userRepository,
            jwtTokenProvider,
            passwordEncoder,
            passwordConfirmationService
        )
    }


    @Test
    fun `validate Reset password Request should return the appropriate response`() {
        // Arrange
        val resetRequest = ResetPasswordRequest("user@example.com")
        val user = Optional.of(
            User(
                UUID.randomUUID(),
                "firstName",
                "lastName",
                "user@example.com",
                "password",
                UserRole.CUSTOMER,
                UserRole.CUSTOMER.permissions,
            ),
        )
        Mockito.`when`(userRepository.findByEmail(resetRequest.email)).thenReturn(user)

        // Act
        val result = passwordService
            .validateResetRequest(resetRequest).data as User

        // Assert
        Assert.assertEquals(result, user.get())
    }

    @Test
    fun `generatePasswordResetToken should return the appropriate response`() {
        val user = Optional.of(
            User(
                UUID.randomUUID(),
                "firstName",
                "lastName",
                "user@example.com",
                "password",
                UserRole.CUSTOMER,
                UserRole.CUSTOMER.permissions,
            )
        )
        val passwordConfirmationToken = Optional.of(
            PasswordConfirmationToken(
                id = UUID.randomUUID(),
                token = "token",
                user = user.get(),
            )
        )
        val token = "token"
        val expiryData = Date()
        Mockito.`when`(passwordConfirmationService.findTokenByUser(user.get()))
            .thenReturn(passwordConfirmationToken)
        Mockito.`when`(jwtTokenProvider.getExpirationDateFromToken(token)).thenReturn(expiryData)

        val result = passwordService
            .generatePasswordResetToken(user.get())
        val actual = result.data as JwtResetPasswordTokenResponse
        val expected = JwtResetPasswordTokenResponse(
            token,
            expiryData,
        )

        Assert.assertEquals(expected, actual)
        Assert.assertEquals("200 OK", result.status)
    }

    @Test
    fun `updatePassword should update the user's password`() {
        val user = User(
            UUID.randomUUID(),
            "jane",
            "doe",
            "jane@example.com",
            "password",
            UserRole.CUSTOMER,
            UserRole.CUSTOMER.permissions,
        )
        val newPassword = "newpassword"
        val updatedPassUser = user.copy(password = newPassword)
        Mockito.`when`(passwordEncoder.encode(newPassword)).thenReturn(newPassword)
        Mockito.`when`(userRepository.save(updatedPassUser)).thenReturn(updatedPassUser)

        passwordService.updatePassword(user, newPassword)

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(newPassword)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(updatedPassUser)
        Assert.assertEquals(newPassword, updatedPassUser.getPassword())
    }


    @Test
    fun `findUser should return the appropriate user`() {
        val token = "token"
        val user = Optional.of(
            User(
                UUID.randomUUID(),
                "firstName",
                "lastName",
                "user@example.com",
                "password",
                UserRole.CUSTOMER,
                UserRole.CUSTOMER.permissions,
            )
        )
        Mockito.`when`(jwtTokenProvider.getEmailFromToken(token)).thenReturn("user@example.com")
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(user)

        val result = passwordService.findUser(token)

        Assert.assertEquals(result, user.get())
    }

}