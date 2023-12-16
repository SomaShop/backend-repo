package com.soma.app.backendrepo.app_user.pass_confirmation

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationRepository
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationService
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationTokenEntity
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
    private lateinit var emailConfirmationRepository: EmailConfirmationRepository
    @InjectMocks
    private lateinit var emailConfirmationService: EmailConfirmationService

    private lateinit var emailConfirmationTokenEntity: EmailConfirmationTokenEntity
    private lateinit var userEntity: UserEntity

    @Before
    fun setUp() {
        emailConfirmationRepository = mock(
            EmailConfirmationRepository::class.java
        )
        emailConfirmationService = EmailConfirmationService(
            emailConfirmationRepository
        )
        userEntity = UserEntity(
            firstName = "John",
            lastName = "Doe",
            email = "john@gmail.com",
            password = "password",
            role = UserRole.ROLE_CUSTOMER,
            permissions = UserRole.ROLE_CUSTOMER.permissions
        )
        emailConfirmationTokenEntity = EmailConfirmationTokenEntity(
            token = "token",
            userId = userEntity.userID
        )
    }

    @Test
    fun `verify that token is saved`() {
        `when`(emailConfirmationRepository.save(emailConfirmationTokenEntity))
            .thenReturn(emailConfirmationTokenEntity)
        emailConfirmationService.saveToken(emailConfirmationTokenEntity)
        verify(emailConfirmationRepository, atLeastOnce())
            .save(emailConfirmationTokenEntity)
    }

    @Test
    fun `verify that token is fetched`() {
        `when`(emailConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationService.getToken(emailConfirmationTokenEntity.token)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByToken(emailConfirmationTokenEntity.token)

        Assert.assertEquals(emailConfirmationTokenEntity,
            emailConfirmationService.getToken(emailConfirmationTokenEntity.token).get())
    }

    @Test
    fun `verify that findByUser return correct password token`() {
        `when`(emailConfirmationRepository.findByUserId(userEntity.userID))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationService.findByUserId(userEntity.userID)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByUserId(userEntity.userID)

        Assert.assertEquals(emailConfirmationTokenEntity,
            emailConfirmationService.findByUserId(userEntity.userID).get())
    }

    @Test
    fun `verify that confirm date is set`() {
        val updatedConfirmData = emailConfirmationTokenEntity.copy(
            confirmedAt = Date()
        )
        `when`(emailConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationService.setConfirmedAt(emailConfirmationTokenEntity.token)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByToken(emailConfirmationTokenEntity.token)

        Assert.assertNotNull(updatedConfirmData.confirmedAt)
    }

}