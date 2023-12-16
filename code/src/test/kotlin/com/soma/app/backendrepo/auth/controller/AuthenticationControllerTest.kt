package com.soma.app.backendrepo.auth.controller

import com.soma.app.backendrepo.authentication.auth.controller.AuthenticationController
import com.soma.app.backendrepo.authentication.auth.service.AuthenticationServiceImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@RunWith(MockitoJUnitRunner::class)
@WebMvcTest(AuthenticationController::class)
class AuthenticationControllerTest {
    @Mock
    private lateinit var authenticationServiceImpl: AuthenticationServiceImpl

    @Before
    fun setUp() {
        authenticationServiceImpl = mock(
            AuthenticationServiceImpl::class.java
        )
    }

}