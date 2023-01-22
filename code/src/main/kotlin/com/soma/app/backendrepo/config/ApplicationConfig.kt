package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.utils.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom

@Configuration
class ApplicationConfig(
    private val userRepository: UserRepository
) {
    private final val logger = Logger<ApplicationConfig>().getLogger()
    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { email ->
            userRepository.findByEmail(email)
                .orElseThrow { UsernameNotFoundException("User not found with email: $email") }
        }
    }

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
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(authenticationProvider())
    }
}