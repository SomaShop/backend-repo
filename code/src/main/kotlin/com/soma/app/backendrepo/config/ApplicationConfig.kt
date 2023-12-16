package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.config.application_properties.EmailProperty
import com.soma.app.backendrepo.authentication.auth.service.AuthenticatedUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * ApplicationConfig class is used to configure the beans for the application.
 * It is used to configure the password encoder, authentication provider and authentication manager.
 */

@Configuration
class ApplicationConfig(
    private val authenticatedUserDetailsService: AuthenticatedUserDetailsService,
    private val emailProperty: EmailProperty
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

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = emailProperty.host
        mailSender.port = emailProperty.port
        mailSender.username = emailProperty.email
        mailSender.password = emailProperty.password
        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        return mailSender
    }
}