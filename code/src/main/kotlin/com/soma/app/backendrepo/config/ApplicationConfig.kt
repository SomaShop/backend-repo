package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.security.auth.service.AuthenticatedUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom

/**
 * ApplicationConfig class is used to configure the beans for the application.
 * It is used to configure the password encoder, authentication provider and authentication manager.
 */

@Configuration
class ApplicationConfig(
    private val authenticatedUserDetailsService: AuthenticatedUserDetailsService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // his configuration will make sure that the password encoder uses
        // the Bcrypt algorithm and it will add a random salt for each password hashed,
        // making it much harder for an attacker to crack the password.
        return BCryptPasswordEncoder(12, SecureRandom())
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(authenticatedUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(authenticationProvider())
    }
}