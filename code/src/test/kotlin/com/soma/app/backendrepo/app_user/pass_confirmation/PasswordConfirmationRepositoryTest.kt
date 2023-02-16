package com.soma.app.backendrepo.app_user.pass_confirmation

import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationRepository
import com.soma.app.backendrepo.app_user.user.pass_confirmation_token.PasswordConfirmationToken
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
    private lateinit var repository: PasswordConfirmationRepository

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
    private val passwordConfirmationToken1 = PasswordConfirmationToken(token = token1, user = userEntity1)
    private val passwordConfirmationToken2 = PasswordConfirmationToken(token = token2, user = userEntity2)

    @Before
    fun setUp() {
        repository = mock(PasswordConfirmationRepository::class.java)
        repository.saveAll(listOf(passwordConfirmationToken1, passwordConfirmationToken2))
    }

    @Test
    fun `when findByToken then return password confirmation token`() {
        `when`(repository.findByToken(token1))
            .thenReturn(Optional.of(passwordConfirmationToken1))
        val found = repository.findByToken(token1)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().token, token1)
        Assert.assertEquals(found.get().user, userEntity1)
    }

    @Test
    fun `when findByUser then return password confirmation token`() {
        `when`(repository.findByUser(userEntity2))
            .thenReturn(Optional.of(passwordConfirmationToken2))
        val found = repository.findByUser(userEntity2)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().token, token2)
        Assert.assertEquals(found.get().user, userEntity2)
    }
}

