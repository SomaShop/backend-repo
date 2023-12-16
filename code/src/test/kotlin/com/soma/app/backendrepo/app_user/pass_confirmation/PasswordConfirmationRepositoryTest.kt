package com.soma.app.backendrepo.app_user.pass_confirmation

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationRepository
import com.soma.app.backendrepo.authentication.email_confirmation.EmailConfirmationTokenEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.Optional

@RunWith(MockitoJUnitRunner::class)
class PasswordConfirmationRepositoryTest {

    @Mock
    private lateinit var repository: EmailConfirmationRepository

    private val userEntity1 = UserEntity(
        firstName = "john",
        lastName = "doe",
        email = "johndoe@example.com",
        password = "password",
        role = UserRole.ROLE_CUSTOMER,
        permissions = UserRole.ROLE_CUSTOMER.permissions
    )
    private val userEntity2 = UserEntity(
        firstName = "ali",
        lastName = "doe",
        email = "johndoe@example.com",
        password = "password",
        role = UserRole.ROLE_CUSTOMER,
        permissions = UserRole.ROLE_CUSTOMER.permissions
    )
    private val token1 = "token1"
    private val token2 = "token2"
    private val emailConfirmationTokenEntity1 = EmailConfirmationTokenEntity(token = token1, userId = userEntity1.userID)
    private val emailConfirmationTokenEntity2 = EmailConfirmationTokenEntity(token = token2, userId = userEntity2.userID)

    @Before
    fun setUp() {
        repository = mock(EmailConfirmationRepository::class.java)
        repository.saveAll(listOf(emailConfirmationTokenEntity1, emailConfirmationTokenEntity2))
    }

    @Test
    fun `when findByToken then return password confirmation token`() {
        `when`(repository.findByToken(token1))
            .thenReturn(Optional.of(emailConfirmationTokenEntity1))
        val found = repository.findByToken(token1)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().token, token1)
        Assert.assertEquals(found.get().userId, userEntity1.userID)
    }

    @Test
    fun `when findByUser then return password confirmation token`() {
        `when`(repository.findByUserId(userEntity2.userID))
            .thenReturn(Optional.of(emailConfirmationTokenEntity2))
        val found = repository.findByUserId(userEntity2.userID)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().token, token2)
        Assert.assertEquals(found.get().userId, userEntity2.userID)
    }
}

