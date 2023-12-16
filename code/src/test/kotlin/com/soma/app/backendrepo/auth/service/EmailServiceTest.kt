package com.soma.app.backendrepo.auth.service

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.email_service.EmailService
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
        emailService.sendPasswordResetEmail(userEntity.email, token)
        verify(emailService, atLeastOnce()).sendPasswordResetEmail(userEntity.email, token)
    }

    @Test
    fun `verify that sendPasswordConfirmationEmail method is called with correct arguments`() {

        emailService.sendEmailConfirmationEmail(userEntity, token)
        verify(emailService, atLeastOnce()).sendEmailConfirmationEmail(userEntity, token)
    }


}