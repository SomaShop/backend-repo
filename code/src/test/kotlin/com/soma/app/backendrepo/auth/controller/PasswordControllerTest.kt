package com.soma.app.backendrepo.auth.controller

import com.soma.app.backendrepo.security.auth.reser_password.controller.PasswordController
import com.soma.app.backendrepo.security.auth.reser_password.service.PasswordService
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class PasswordControllerTest {

    @Mock
    private lateinit var passwordController: PasswordController
    @Mock
    private lateinit var passwordService: PasswordService

    @Before
    fun setUp() {
        passwordController = mock(
            PasswordController::class.java
        )
        passwordService = mock(
            PasswordService::class.java
        )
    }

}