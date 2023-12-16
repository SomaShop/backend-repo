package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.security.auth.service.AuthenticatedUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * SecurityConfig class is used to configure the security for the application.
 * It is used to configure the authentication provider, user details service and the security filter chain.
 * also it is used to configure the endpoints that need to be secured.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,
    private val authenticatedUserDetailsService: AuthenticatedUserDetailsService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
    http.csrf { csrf ->
        csrf
            .disable()
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers("/api/v1/merchant/**").hasAuthority("ROLE_MERCHANT")
                    .requestMatchers("/api/v1/customer/**").hasAuthority("ROLE_CUSTOMER")
                    .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .userDetailsService(authenticatedUserDetailsService)
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
    }.build()

}
