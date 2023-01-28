package com.soma.app.backendrepo.app_user.user

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.app_user.user.model.UserRole
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.util.Optional

@SpringBootTest
class UserRepositoryTest {
    @Mock
    lateinit var userRepository: UserRepository
    private val user1 = User(
        firstName = "john",
        lastName = "doe",
        email = "john_doe@gmail.com",
        password = "password",
        role = UserRole.BUYER,
        permissions = UserRole.BUYER.permissions
    )

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
    }

    @Test
    fun `when findByEmail then return user`() {
        `when`(userRepository.findByEmail(user1.username))
            .thenReturn(Optional.of(user1))
        val found = userRepository.findByEmail(user1.username)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().username, user1.username)
        Assert.assertEquals(found.get().firstName, user1.firstName)
        Assert.assertEquals(found.get().lastName, user1.lastName)
        Assert.assertEquals(found.get().password, user1.password)
        Assert.assertEquals(found.get().role, user1.role)
        Assert.assertEquals(found.get().permissions, user1.permissions)
    }
}