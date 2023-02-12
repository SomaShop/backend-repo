package com.soma.app.backendrepo.auth.service

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.security.auth.reser_password.service.EmailService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EmailServiceTest {

    @Mock
    private lateinit var emailService: EmailService

    private lateinit var user: User

    private lateinit var token: String

    @Before
    fun setUp() {
        emailService = mock(
            EmailService::class.java
        )
        user = User(
            userID = null,
            "firstName",
            "lastName",
            "user@example.com",
            "password",
            UserRole.ROLE_CUSTOMER,
            UserRole.ROLE_CUSTOMER.permissions,
        )
        token = "token"
    }

    @Test
    fun `verify that sendPasswordResetEmail method is called`() {
        val token = "token"
        emailService.sendPasswordResetEmail(user, token)
        verify(emailService, atLeastOnce()).sendPasswordResetEmail(user, token)
    }

    @Test
    fun `verify that sendPasswordConfirmationEmail method is called with correct arguments`() {

        emailService.sendPasswordConfirmationEmail(user, token)
        verify(emailService, atLeastOnce()).sendPasswordConfirmationEmail(user, token)
    }


}