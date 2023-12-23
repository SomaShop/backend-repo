package com.soma.app.backendrepo.app_user.pass_confirmation

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationRepository
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationServiceImpl
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
    private lateinit var emailConfirmationServiceImpl: EmailConfirmationServiceImpl

    private lateinit var emailConfirmationTokenEntity: EmailConfirmationTokenEntity
    private lateinit var userEntity: UserEntity

    @Before
    fun setUp() {
        emailConfirmationRepository = mock(
            EmailConfirmationRepository::class.java
        )
        emailConfirmationServiceImpl = EmailConfirmationServiceImpl(
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
            userId = userEntity.userID,
            verificationCode = "1234"
        )
    }

    @Test
    fun `verify that token is saved`() {
        `when`(emailConfirmationRepository.save(emailConfirmationTokenEntity))
            .thenReturn(emailConfirmationTokenEntity)
        emailConfirmationServiceImpl.saveToken(emailConfirmationTokenEntity)
        verify(emailConfirmationRepository, atLeastOnce())
            .save(emailConfirmationTokenEntity)
    }

    @Test
    fun `verify that token is fetched`() {
        `when`(emailConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationServiceImpl.getToken(emailConfirmationTokenEntity.token)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByToken(emailConfirmationTokenEntity.token)

        Assert.assertEquals(emailConfirmationTokenEntity,
            emailConfirmationServiceImpl.getToken(emailConfirmationTokenEntity.token).get())
    }

    @Test
    fun `verify that findByUser return correct password token`() {
        `when`(emailConfirmationRepository.findByUserId(userEntity.userID))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationServiceImpl.findByUserId(userEntity.userID)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByUserId(userEntity.userID)

        Assert.assertEquals(emailConfirmationTokenEntity,
            emailConfirmationServiceImpl.findByUserId(userEntity.userID).get())
    }

    @Test
    fun `verify that confirm date is set`() {
        val updatedConfirmData = emailConfirmationTokenEntity.copy(
            confirmedAt = Date()
        )
        `when`(emailConfirmationRepository.findByToken("token"))
            .thenReturn(Optional.of(emailConfirmationTokenEntity))

        emailConfirmationServiceImpl.saveEmailConfirmationToken(emailConfirmationTokenEntity)

        verify(emailConfirmationRepository, atLeastOnce())
            .findByToken(emailConfirmationTokenEntity.token)

        Assert.assertNotNull(updatedConfirmData.confirmedAt)
    }

}