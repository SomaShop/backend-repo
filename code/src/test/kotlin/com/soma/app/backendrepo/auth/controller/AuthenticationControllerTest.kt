package com.soma.app.backendrepo.auth.controller

import com.soma.app.backendrepo.security.auth.controller.AuthenticationController
import com.soma.app.backendrepo.security.auth.service.AuthenticationService
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
    private lateinit var authenticationService: AuthenticationService

    @Before
    fun setUp() {
        authenticationService = mock(
            AuthenticationService::class.java
        )
    }

}