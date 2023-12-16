package com.soma.app.backendrepo.app_user.user

import com.soma.app.backendrepo.model.app_user.UserEntity
import com.soma.app.backendrepo.model.app_user.UserRole
import com.soma.app.backendrepo.authentication.auth.repository.UserRepository
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
    private val user1 = UserEntity(
        firstName = "john",
        lastName = "doe",
        email = "john_doe@gmail.com",
        password = "password",
        role = UserRole.ROLE_CUSTOMER,
        permissions = UserRole.ROLE_CUSTOMER.permissions
    )

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
    }

    @Test
    fun `when findByEmail then return user`() {
        `when`(userRepository.findByEmail(user1.email))
            .thenReturn(Optional.of(user1))
        val found = userRepository.findByEmail(user1.email)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(found.get().email, user1.email)
        Assert.assertEquals(found.get().firstName, user1.firstName)
        Assert.assertEquals(found.get().lastName, user1.lastName)
        Assert.assertEquals(found.get().getPassword(), user1.getPassword())
        Assert.assertEquals(found.get().role, user1.role)
        Assert.assertEquals(found.get().permissions, user1.permissions)
    }
}