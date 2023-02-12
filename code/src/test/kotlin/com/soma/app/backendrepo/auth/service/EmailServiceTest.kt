package com.soma.app.backendrepo.auth.service

import com.soma.app.backendrepo.app_user.user.model.UserEntity
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

    private lateinit var userEntity: UserEntity

    private lateinit var token: String

    @Before
    fun setUp() {
        emailService = mock(
            EmailService::class.java
        )
        userEntity = UserEntity(
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
        emailService.sendPasswordResetEmail(userEntity, token)
        verify(emailService, atLeastOnce()).sendPasswordResetEmail(userEntity, token)
    }

    @Test
    fun `verify that sendPasswordConfirmationEmail method is called with correct arguments`() {

        emailService.sendPasswordConfirmationEmail(userEntity, token)
        verify(emailService, atLeastOnce()).sendPasswordConfirmationEmail(userEntity, token)
    }


}