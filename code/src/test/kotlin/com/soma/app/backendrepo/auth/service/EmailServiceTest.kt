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
    fun `verify that sendEmail method is called`() {
        val subject = "subject"
        val text = "text"
        emailService.sendEmail(userEntity.email, subject, text)
        verify(emailService, atLeastOnce()).sendEmail(userEntity.email, subject, text)
    }


}