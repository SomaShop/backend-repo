package com.soma.app.backendrepo.app_user.pass_confirmation

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationRepository
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationService
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PasswordConfirmationServiceTest {
    @Mock
    private lateinit var passwordConfirmationRepository: PasswordConfirmationRepository
    @InjectMocks
    private lateinit var passwordConfirmationService: PasswordConfirmationService

    private lateinit var passwordConfirmationToken: PasswordConfirmationToken
    private lateinit var user: User

    @Before
    fun setUp() {
        passwordConfirmationRepository = mock(
            PasswordConfirmationRepository::class.java
        )
        passwordConfirmationService = PasswordConfirmationService(
            passwordConfirmationRepository
        )
        user = User(
            firstName = "John",
            lastName = "Doe",
            email = "john@gmail.com",
            password = "password",
            role = UserRole.ROLE_CUSTOMER,
            permissions = UserRole.ROLE_CUSTOMER.permissions
        )
        passwordConfirmationToken = PasswordConfirmationToken(
            token = "token",
            user = user
        )
    }

    @Test
    fun `verify that token is saved`() {
        `when`(passwordConfirmationRepository.save(passwordConfirmationToken))
            .thenReturn(passwordConfirmationToken)
        passwordConfirmationService.saveToken(passwordConfirmationToken)
        verify(passwordConfirmationRepository, atLeastOnce())
            .save(passwordConfirmationToken)
    }

    @Test
    fun `verify that token is fetched`() {
        `when`(passwordConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(passwordConfirmationToken))

        passwordConfirmationService.getToken(passwordConfirmationToken.token)

        verify(passwordConfirmationRepository, atLeastOnce())
            .findByToken(passwordConfirmationToken.token)

        Assert.assertEquals(passwordConfirmationToken,
            passwordConfirmationService.getToken(passwordConfirmationToken.token).get())
    }

    @Test
    fun `verify that findByUser return correct password token`() {
        `when`(passwordConfirmationRepository.findByUser(user))
            .thenReturn(Optional.of(passwordConfirmationToken))

        passwordConfirmationService.findTokenByUser(user)

        verify(passwordConfirmationRepository, atLeastOnce())
            .findByUser(user)

        Assert.assertEquals(passwordConfirmationToken,
            passwordConfirmationService.findTokenByUser(user).get())
    }

    @Test
    fun `verify that confirm date is set`() {
        val updatedConfirmData = passwordConfirmationToken.copy(
            confirmedAt = Date()
        )
        `when`(passwordConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(passwordConfirmationToken))

        passwordConfirmationService.setConfirmedAt(passwordConfirmationToken.token)

        verify(passwordConfirmationRepository, atLeastOnce())
            .findByToken(passwordConfirmationToken.token)

        Assert.assertNotNull(updatedConfirmData.confirmedAt)
    }

}